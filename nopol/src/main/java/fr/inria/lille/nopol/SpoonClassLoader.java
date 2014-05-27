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
import java.io.IOException;
import java.util.List;
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

import com.martiansoftware.jsap.JSAPException;

import fr.inria.lille.commons.collections.MapLibrary;

/**
 * A classloader that gets classes from Java source files and process them
 * before actually loading them.
 */
public class SpoonClassLoader extends ClassLoader {

	public SpoonClassLoader(File sourceFolder) {
		super();
		sourcePath = sourceFolder;
	}

	public Factory getFactory() {
		if (factory == null) {
			factory = new FactoryImpl(getCoreFactory(), getEnvironment());
		}
		return factory;
	}

	public CoreFactory getCoreFactory() {
		if (coreFactory == null) {
			coreFactory = new DefaultCoreFactory();
		}
		return coreFactory;
	}
	
	public Environment getEnvironment() {
		if (environment == null) {
			environment = new StandardEnvironment();
		}
		return environment;
	}

	public JDTByteCodeCompiler getCompiler() {
		if (compiler == null) {
			compiler = new JDTByteCodeCompiler();
		}
		return compiler;
	}
	
	public Map<String, Class<?>> getClasscache() {
		return classcache;
	}
	
	public ProcessingManager getProcessingManager() {
		if (processing == null) {
			processing = new RuntimeProcessingManager(getFactory());
		}
		return processing;
	}

	@Override
	public Class<?> loadClass(final String name) throws ClassNotFoundException {
		if (classcache.containsKey(name)) {
			return classcache.get(name);
		}
		Class<?> clas = createClass(name);
		if (clas == null) {
			clas = findSystemClass(name);
		}
		if (clas == null) {
			throw new ClassNotFoundException(name);
		}
		return clas;
	}
	
	private Class<?> createClass(final String qualifiedName) {
		Class<?> targetClass = null;
		try {
			processJavaFile(qualifiedName);
			targetClass = classcache.get(qualifiedName);
		} catch (Exception e) {

		}
		return targetClass;
	}

	private void processJavaFile(final String qualifiedName) throws Exception {
		CtSimpleType<?> c = classFrom(qualifiedName);
		processClass(c);
		Map<String, Class<?>> compiledClasses = compiledClassesFrom(c);
		MapLibrary.putAll(compiledClasses, classcache);
	}

	private CtSimpleType<?> classFrom(final String qualifiedName) throws ClassNotFoundException, JSAPException, IOException, Exception {
		CtSimpleType<?> c = getFactory().Type().get(qualifiedName);
		if (c == null) {
			File f = resolve(qualifiedName);
			if (f != null && f.exists()) {
				SpoonCompiler builder = new Launcher().createCompiler(getFactory());
				builder.addInputSource(sourcePath);
				builder.addTemplateSource(sourcePath);
				builder.build();
				c = getFactory().Type().get(qualifiedName);
			}
		}
		if (c == null) {
			throw new ClassNotFoundException(qualifiedName);
		}
		return c;
	}
	
	private File resolve(final String qualifiedName) {
		File current = sourcePath;
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
	
	private void processClass(CtSimpleType<?> c) {
		getProcessingManager().process(c);
	}
	
	private Map<String, Class<?>> compiledClassesFrom(CtSimpleType<?> c) {
		BasicCompilationUnit unit = compilationUnitFor(c);
		List<ClassFile> classFiles = getCompiler().compile(new ICompilationUnit[] { unit });
		return collectClasses(classFiles);
	}
	
	private BasicCompilationUnit compilationUnitFor(CtSimpleType<?> c) {
		DefaultJavaPrettyPrinter printer = new DefaultJavaPrettyPrinter(getEnvironment());
		printer.scan(c);
		String[] tmp = c.getQualifiedName().split("[.]");
		char[][] pack = new char[tmp.length - 1][];

		for (int i = 0; i < tmp.length - 1; i++) {
			pack[i] = tmp[i].toCharArray();
		}

		String classBody = printer.toString();
		StringBuffer classBuffer = new StringBuffer(classBody.length() + 100);
		classBuffer.append(c.getPackage()).append(classBody);
		BasicCompilationUnit unit = new BasicCompilationUnit(classBuffer.toString().toCharArray(), pack, c.getSimpleName() + ".java");
		return unit;
	}
	
	private Map<String, Class<?>> collectClasses(List<ClassFile> classes) throws ClassFormatError {
		Map<String, Class<?>> addedClasses = MapLibrary.newHashMap();
		for (ClassFile classFile : classes) {
			byte[] fileBytes = classFile.getBytes();
			String name = new String(classFile.fileName()).replace('/', '.');
			Class<?> definedClass = defineClass(name, fileBytes, 0, fileBytes.length);
			addedClasses.put(name, definedClass);
		}
		return addedClasses;
	}

	private Factory factory;
	private File sourcePath;
	private CoreFactory coreFactory;
	private Environment environment;
	private JDTByteCodeCompiler compiler;
	private ProcessingManager processing;
	private final Map<String, Class<?>> classcache = new TreeMap<String, Class<?>>();
}
