package fr.inria.lille.commons.spoon;

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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;

import spoon.Launcher;
import spoon.compiler.Environment;
import spoon.compiler.SpoonCompiler;
import spoon.processing.ProcessingManager;
import spoon.processing.Processor;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.support.DefaultCoreFactory;
import spoon.support.RuntimeProcessingManager;
import spoon.support.StandardEnvironment;
import spoon.support.util.BasicCompilationUnit;
import fr.inria.lille.commons.collections.MapLibrary;

/** 
 * A classloader that gets classes from Java source files and process them before actually loading them.
 * As any other classloader, `SpoonClassLoader` can load a given class only once. This means that to use
 * Spoon several times on the same sourceFolder you should instantiate a `SpoonClassLoader` each time.
 */
public class SpoonClassLoader extends ClassLoader {

	public static Map<String, Class<?>> transformedClassesFrom(File sourceFolder, Collection<Processor<?>> processors) {
		SpoonClassLoader spooner = new SpoonClassLoader(sourceFolder);
		spooner.addProcessors(processors);
		spooner.loadModelledClasses();
		return spooner.getClasscache();
	}
	
	public SpoonClassLoader(File sourceFolder) {
		super();
		sourcePath = sourceFolder;
		compiler = new JDTByteCodeCompiler();
		environment = new StandardEnvironment();
		classcache = new TreeMap<String, Class<?>>();
		factory = new FactoryImpl(new DefaultCoreFactory(), getEnvironment());
		manager = new RuntimeProcessingManager(getFactory());
		getEnvironment().setDebug(true);
		buildModel();
	}

	private void buildModel() {
		try {
			SpoonCompiler builder = new Launcher().createCompiler(getFactory());
			builder.addInputSource(getSourcePath());
			builder.addTemplateSource(getSourcePath());
			builder.build();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Factory getFactory() {
		return factory;
	}
	
	public File getSourcePath() {
		return sourcePath;
	}
	
	public Environment getEnvironment() {
		return environment;
	}

	public JDTByteCodeCompiler getCompiler() {
		return compiler;
	}
	
	public Map<String, Class<?>> getClasscache() {
		return classcache;
	}
	
	protected ProcessingManager getProcessingManager() {
		return manager;
	}

	public void addProcessors(Collection<Processor<?>> processors) {
		for (Processor<?> processor : processors) {
			addProcessor(processor);
		}
	}

	public void addProcessor(Processor<?> processor) {
		getProcessingManager().addProcessor(processor);
	}
	
	public Collection<CtSimpleType> modelledClasses() {
		return InstanceOfClassFilter.classDefinitionsIn(getFactory());
	}
	
	public Map<String, Class<?>> loadModelledClasses() {
		Map<String, Class<?>> loadedClasses = MapLibrary.newHashMap();
		for (CtSimpleType modelledClass : modelledClasses()) {
			loadedClasses.putAll(initializeClassFrom(modelledClass));
		}
		return loadedClasses;
	}
	
	@Override
	public Class<?> loadClass(final String name) throws ClassNotFoundException {
		if (getClasscache().containsKey(name)) {
			return getClasscache().get(name);
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
			CtSimpleType<?> c = modelledClassFor(qualifiedName);
			initializeClassFrom(c);
			targetClass = getClasscache().get(qualifiedName);
		} catch (Exception e) {

		}
		return targetClass;
	}
	
	private CtSimpleType<?> modelledClassFor(final String qualifiedName) throws ClassNotFoundException {
		CtSimpleType<?> c = getFactory().Type().get(qualifiedName);
		if (c == null) {
			throw new ClassNotFoundException(qualifiedName);
		}
		return c;
	}

	private Map<String, Class<?>> initializeClassFrom(final CtSimpleType<?> c) {
		processClass(c);
		Map<String, Class<?>> compiledClasses = compiledClassesFrom(c);
		getClasscache().putAll(compiledClasses);
		return compiledClasses;
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

	private File sourcePath;
	private Factory factory;
	private Environment environment;
	private ProcessingManager manager;
	private JDTByteCodeCompiler compiler;
	private final Map<String, Class<?>> classcache;
}