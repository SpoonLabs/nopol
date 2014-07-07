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
import static fr.inria.lille.commons.string.StringLibrary.javaNewline;
import static java.util.Arrays.asList;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import spoon.compiler.Environment;
import spoon.processing.ProcessingManager;
import spoon.processing.Processor;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.support.RuntimeProcessingManager;
import fr.inria.lille.commons.collections.MapLibrary;
import fr.inria.lille.commons.compiler.DynamicClassCompiler;


/** 
 * A classloader that gets classes from Java source files and process them before actually loading them.
 */
public class SpoonClassLoader {

	public SpoonClassLoader(File sourceFolder, Collection<? extends Processor<?>> processors) {
		this(sourceFolder);
		addProcessors(processors);
	}
	
	public SpoonClassLoader(File sourceFile) {
		compiler = new DynamicClassCompiler();
		factory = SpoonLibrary.modelFor(sourceFile);
		manager = new RuntimeProcessingManager(getFactory());
	} 
	
	protected Factory getFactory() {
		return factory;
	}
	
	protected Environment getEnvironment() {
		return getFactory().getEnvironment();
	}

	protected DynamicClassCompiler getCompiler() {
		return compiler;
	}
	
	protected ProcessingManager getProcessingManager() {
		return manager;
	}
	
	protected TypeFactory getTypeFactory() {
		return getFactory().Type();
	}

	public void addProcessors(Collection<? extends Processor<?>> processors) {
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
	
	public ClassLoader classLoaderProcessing(CtSimpleType<?> modelledClass) {
		return classLoaderProcessing((Collection) asList(modelledClass));
	}
	
	public ClassLoader classLoaderProcessing(Collection<CtSimpleType<?>> modelledClasses) {
		Map<String, String> processedClasses = processedSources(modelledClasses);
		return getCompiler().classLoaderFor(processedClasses);
	}
	
	private Map<String, String> processedSources(Collection<CtSimpleType<?>> modelledClasses) {
		Map<String, String> processedClasses = MapLibrary.newHashMap();
		for (CtSimpleType<?> modelledClass : modelledClasses) {
			processClass(modelledClass);
			String qualifiedName = modelledClass.getQualifiedName();
			String sourceCode = sourceContent(modelledClass);
			processedClasses.put(qualifiedName, sourceCode);
		}
		return processedClasses;
	}
	
	private void processClass(CtSimpleType<?> c) {
		getProcessingManager().process(c);
	}

	private String sourceContent(CtSimpleType<?> c) {
		DefaultJavaPrettyPrinter printer = new DefaultJavaPrettyPrinter(getEnvironment());
		printer.scan(c);
		return c.getPackage().toString() + javaNewline() +  printer.toString();
	}
	
	private Factory factory;
	private ProcessingManager manager;
	private DynamicClassCompiler compiler;
}