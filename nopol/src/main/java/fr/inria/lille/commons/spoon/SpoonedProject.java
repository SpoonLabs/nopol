package fr.inria.lille.commons.spoon;

import static java.util.Arrays.asList;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

import spoon.processing.Processor;
import spoon.reflect.declaration.CtType;
import xxl.java.container.classic.MetaList;

public class SpoonedProject extends SpoonedFile {
	
	public SpoonedProject(File[] sourceFile, URL[] classpath) {
		super(sourceFile, classpath);
	}
	
	@Override
	protected Collection<? extends CtType<?>> modelledClasses() {
		return typeFactory().getAll();
	}
	
	protected CtType<?> modelledClass(String qualifiedName) {
		return typeFactory().get(qualifiedName);
	}
	
	protected Collection<CtType<?>> modelledClasses(Collection<String> qualifiedNames) {
		Collection<CtType<?>> modelledClasses = MetaList.newLinkedList();
		for (String qualifiedName : qualifiedNames) {
			modelledClasses.add(modelledClass(qualifiedName));
		}
		return modelledClasses;
	}
	
	public SpoonedClass forked(String qualifiedName) {
		CtType<?> modelledClass = modelledClass(qualifiedName);
		if(modelledClass == null) {
			return null;
		}
		return new SpoonedClass(this, modelledClass);
	}
	
	public ClassLoader processedAndDumpedToClassLoader(String qualifiedName, Processor<?> processor) {
		return processedAndDumpedToClassLoader(asList(qualifiedName), processor);
	}
	
	public ClassLoader processedAndDumpedToClassLoader(Collection<String> qualifiedNames, Processor<?> processor) {
		return processedAndDumpedToClassLoader(qualifiedNames, asList(processor));
	}
	
	public ClassLoader processedAndDumpedToClassLoader(String qualifiedName, Collection<? extends Processor<?>> processors) {
		return processedAndDumpedToClassLoader(asList(qualifiedName), processors);
	}
	
	public ClassLoader processedAndDumpedToClassLoader(Collection<String> qualifiedNames, Collection<? extends Processor<?>> processors) {
		processClasses(qualifiedNames, processors);
		return super.newBytecodeClassloader(compiledClasses());
	}
	
	public void processClass(String qualifiedName, Processor<?> processor) {
		processClass(asList(qualifiedName), processor);
	}
	
	public void processClass(Collection<String> qualifiedNames, Processor<?> processor) {
		processClasses(qualifiedNames, asList(processor));
	}
	
	public void processClass(String qualifiedName, Collection<? extends Processor<?>> processors) {
		processClasses(asList(qualifiedName), processors);
	}
	
	public void processClasses(Collection<String> qualifiedNames, Collection<? extends Processor<?>> processors) {
		super.processModelledClasses(modelledClasses(qualifiedNames), processors);
	}
	
	public String sourceForClass(String qualifiedName) {
		return sourcesForClasses(asList(qualifiedName)).get(qualifiedName);
	}
	
	public Map<String, String> sourcesForClasses(Collection<String> qualifiedNames) {
		return super.sourcesForModelledClasses(modelledClasses(qualifiedNames));
	}
}
