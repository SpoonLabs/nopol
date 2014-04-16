package fr.inria.lille.nopol;
/*
 * Spoon - http://spoon.gforge.inria.fr/
 * Copyright (C) 2006 INRIA Futurs <renaud.pawlak@inria.fr>
 * 
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */



import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;

import spoon.Launcher;
import spoon.compiler.Environment;
import spoon.compiler.SpoonCompiler;
import spoon.processing.ProcessingManager;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.factory.CoreFactory;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.support.DefaultCoreFactory;
import spoon.support.RuntimeProcessingManager;
import spoon.support.StandardEnvironment;
import spoon.support.util.BasicCompilationUnit;

/**
 * A classloader that gets classes from Java source files and process them
 * before actually loading them.
 */
public class SpoonClassLoader extends ClassLoader {

	private final Map<String, Class<?>> classcache = new TreeMap<String, Class<?>>();

	private CoreFactory coreFactory;

	private Environment environment;

	private Factory factory;

	private ProcessingManager processing;

	private File sourcePath;

	/**
	 * Constructs a Spoon classloader.
	 */
	public SpoonClassLoader() {
		super();
	}

	/**
	 * Constructs a Spoon classloader within the context of a given parent
	 * classloader.
	 */
	public SpoonClassLoader(final ClassLoader parent) {
		super(parent);
	}

	private Class<?> createClass(final String qualifiedName) {
		try {
			// Process file
			this.processJavaFile(qualifiedName);
			return this.classcache.get(qualifiedName);
		} catch (Exception e) {

		}
		return null;
	}

	/**
	 * Gets the associated (default) core factory.
	 */
	public CoreFactory getCoreFactory() {
		if (this.coreFactory == null) {
			this.coreFactory = new DefaultCoreFactory();
		}
		return this.coreFactory;
	}

	/**
	 * Gets the associated (standard) environment.
	 */
	public Environment getEnvironment() {
		if (this.environment == null) {
			this.environment = new StandardEnvironment();
		}
		return this.environment;
	}

	/**
	 * Gets the associated factory.
	 */
	public Factory getFactory() {
		if (this.factory == null) {
			this.factory = new FactoryImpl(this.getCoreFactory(), this.getEnvironment());
		}
		return this.factory;
	}

	/**
	 * Gets the processing manager.
	 */
	public ProcessingManager getProcessingManager() {
		if (this.processing == null) {
			this.processing = new RuntimeProcessingManager(this.getFactory());
		}
		return this.processing;
	}

	/**
	 * Gets the source path.
	 */
	public File getSourcePath() {
		if (this.sourcePath == null) {
			this.sourcePath = new File("");
		}
		return this.sourcePath;
	}

	private void injectMetaClass(final Class<?> clas) throws NoSuchFieldException,
	SecurityException, IllegalArgumentException, IllegalAccessException {
		// try to inject metaclass
		Field field = clas.getField("METACLASS");
		if (field == null) {
			return;
		}
		System.out.println("try to inject metaclass in" + clas.getName());

		CtSimpleType<?> metaclass = this.factory.Type().get(clas);
		field.setAccessible(true);
		field.set(null, metaclass);
	}

	/**
	 * Loads a given class from its name.
	 */
	@Override
	public Class<?> loadClass(final String name) throws ClassNotFoundException {

		// Look in cache
		if (this.classcache.containsKey(name)) {
			return this.classcache.get(name);
		}

		// Try to gets from spoon factory
		Class<?> clas = null;
		clas = this.createClass(name);

		// Try to get in system class
		if (clas == null) {
			clas = this.findSystemClass(name);
		}

		if (clas == null) {
			throw new ClassNotFoundException(name);
		}

		try {
			this.injectMetaClass(clas);
		} catch (Exception e) {
		}

		return clas;
	}

	private void processJavaFile(final String qualifiedName) throws Exception {
		// Try to resolve in model
		CtSimpleType<?> c = this.getFactory().Type().get(qualifiedName);

		// Try to resolve in source path
		if (c == null) {
			File f = this.resolve(qualifiedName);
			if (f == null || !f.exists()) {
				throw new ClassNotFoundException(qualifiedName);
			}
			
			SpoonCompiler builder = new Launcher().createCompiler(getFactory());
			/* workaround
			 * we rebuild all the model in order to get dependencies because this is another factory
			 */
			builder.addInputSource(sourcePath);
			builder.addTemplateSource(sourcePath);
			builder.build();

			c = this.getFactory().Type().get(qualifiedName);
		}

		// not resolved
		if (c == null) {
			throw new ClassNotFoundException(qualifiedName);
		}
		// Processing it
		this.getProcessingManager().process(c);

		// Printing it
		DefaultJavaPrettyPrinter printer = new DefaultJavaPrettyPrinter(
				this.getEnvironment());
		printer.scan(c);

		String[] tmp = c.getQualifiedName().split("[.]");
		char[][] pack = new char[tmp.length - 1][];

		for (int i = 0; i < tmp.length - 1; i++) {
			pack[i] = tmp[i].toCharArray();
		}

		String classBody = printer.toString();
		StringBuffer classBuffer = new StringBuffer(classBody.length() + 100);
		classBuffer.append(c.getPackage()).append(classBody);

		

		BasicCompilationUnit unit = new BasicCompilationUnit(
				classBuffer.toString().toCharArray(), pack, c.getSimpleName()
				+ ".java");

		JDTByteCodeCompiler comp  = new JDTByteCodeCompiler();
		comp.compile(new ICompilationUnit[] { unit });

		for (ClassFile f : comp.getClassFiles()) {
			String name = new String(f.fileName()).replace('/', '.');
			Class<?> cl = this.defineClass(name, f.getBytes(), 0,
					f.getBytes().length);
			this.classcache.put(name, cl);
		}

	}

	private File resolve(final String qualifiedName) {
		File current = this.sourcePath;
		String[] path = qualifiedName.split("[.]");
		for (String p : path) {
			for (File f : current.listFiles()) {
				if (f.getName().equals(p) || f.getName().equals(p + ".java")) {
					current = f;
					continue;
				}
			}
		}
		if (!current.isDirectory()) {
			return current;
		}
		return null;
	}

	/**
	 * Sets the core factory.
	 */
	public void setCoreFactory(final CoreFactory coreFactory) {
		this.coreFactory = coreFactory;
	}

	/**
	 * Sets the environment.
	 */
	public void setEnvironment(final Environment environment) {
		this.environment = environment;
	}

	/**
	 * Sets the factory.
	 */
	public void setFactory(final Factory factory) {
		this.factory = factory;
	}

	/**
	 * Sets the used processing manager.
	 */
	public void setProcessingManager(final ProcessingManager processing) {
		this.processing = processing;
	}

	/**
	 * Sets the source path.
	 */
	public void setSourcePath(final File sourcePath) {
		this.sourcePath = sourcePath;
	}

}
