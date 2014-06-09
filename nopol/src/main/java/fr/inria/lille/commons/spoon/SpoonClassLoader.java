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
import java.net.URL;
import java.net.URLClassLoader;
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
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.support.DefaultCoreFactory;
import spoon.support.RuntimeProcessingManager;
import spoon.support.StandardEnvironment;
import spoon.support.util.BasicCompilationUnit;
import fr.inria.lille.commons.collections.ListLibrary;
import fr.inria.lille.commons.collections.MapLibrary;
import fr.inria.lille.commons.collections.SetLibrary;

/** 
 * A classloader that gets classes from Java source files and process them before actually loading them.
 */
public class SpoonClassLoader extends URLClassLoader {

	public SpoonClassLoader(File sourceFile, URL[] classpath) {
		super(classpath);
		sourcePath = sourceFile;
		compiler = new JDTByteCodeCompiler();
		environment = new StandardEnvironment();
		classcache = new TreeMap<String, Class<?>>();
		factory = new FactoryImpl(new DefaultCoreFactory(), getEnvironment());
		manager = new RuntimeProcessingManager(getFactory());
		getEnvironment().setDebug(true);
		buildModel();
	} 
	
	public SpoonClassLoader(File sourceFolder, URL[] classpath, Processor<?>... processors) {
		this(sourceFolder, classpath, ListLibrary.newLinkedList(processors));
	}
	
	public SpoonClassLoader(File sourceFolder, URL[] classpath, Collection<Processor<?>> processors) {
		this(sourceFolder, classpath);
		addProcessors(processors);
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
	
	protected Factory getFactory() {
		return factory;
	}
	
	protected File getSourcePath() {
		return sourcePath;
	}
	
	protected Environment getEnvironment() {
		return environment;
	}

	protected JDTByteCodeCompiler getCompiler() {
		return compiler;
	}
	
	protected Map<String, Class<?>> getClasscache() {
		return classcache;
	}
	
	protected ProcessingManager getProcessingManager() {
		return manager;
	}
	
	protected TypeFactory getTypeFactory() {
		return getFactory().Type();
	}

	public boolean alreadyLoaded(String className) {
		return getClasscache().containsKey(className);
	}
	
	protected boolean alreadyLoaded(CtSimpleType<?> modelledClass) {
		return alreadyLoaded(modelledClass.getQualifiedName());
	}
	
	public void addProcessors(Collection<Processor<?>> processors) {
		for (Processor<?> processor : processors) {
			addProcessor(processor);
		}
	}

	public void addProcessor(Processor<?> processor) {
		getProcessingManager().addProcessor(processor);
	}
	
	public CtSimpleType<?> modelledClass(final String qualifiedName) {
		return getTypeFactory().get(qualifiedName);
	}
	
	public Collection<CtSimpleType<?>> modelledClasses() {
		return getTypeFactory().getAll();
	}
	
	public Collection<String> modelledClassesNames() {
		Collection<String> classes = SetLibrary.newHashSet();
		for (CtSimpleType<?> modelledClass : modelledClasses()) {
			classes.add(modelledClass.getQualifiedName());
		}
		return classes;
	}
	
	@Override
	public Class<?> loadClass(final String name) throws ClassNotFoundException {
		if (alreadyLoaded(name)) {
			return getClasscache().get(name);
		}
		Class<?> targetClass;
		CtSimpleType<?> modelledClass = modelledClass(name);
		if (modelledClass == null) {
			targetClass = super.loadClass(name);
		} else {
			targetClass = classesCreatedFrom(modelledClass).get(name);
		}
		if (targetClass == null) {
			throw new ClassNotFoundException(name);
		}
		return targetClass;
	}
	
	private Map<String, Class<?>> classesCreatedFrom(final CtSimpleType<?> c) {
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