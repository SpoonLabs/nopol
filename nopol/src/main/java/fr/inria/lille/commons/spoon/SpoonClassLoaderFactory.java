package fr.inria.lille.commons.spoon;

import static fr.inria.lille.commons.classes.LoggerLibrary.logDebug;
import static fr.inria.lille.commons.classes.LoggerLibrary.newLoggerFor;
import static fr.inria.lille.commons.string.StringLibrary.javaNewline;
import static java.lang.String.format;
import static java.util.Arrays.asList;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;

import spoon.compiler.Environment;
import spoon.processing.ProcessingManager;
import spoon.processing.Processor;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.support.RuntimeProcessingManager;
import fr.inria.lille.commons.collections.MapLibrary;
import fr.inria.lille.commons.compiler.BytecodeClassLoaderBuilder;
import fr.inria.lille.commons.compiler.DynamicClassCompiler;
import fr.inria.lille.commons.spoon.util.SpoonModelLibrary;

public class SpoonClassLoaderFactory {

	public SpoonClassLoaderFactory(File sourceFolder, Processor<?> processor) {
		this(sourceFolder, asList(processor));
	}
	
	public SpoonClassLoaderFactory(File sourceFolder, Collection<? extends Processor<?>> processors) {
		this(sourceFolder);
		addProcessors(processors);
	}
	
	public SpoonClassLoaderFactory(File sourceFile) {
		compiler = new DynamicClassCompiler();
		logDebug(logger, format("[Building Spoon model from %s]", sourceFile.getPath()));
		factory = SpoonModelLibrary.modelFor(sourceFile);
		manager = new RuntimeProcessingManager(spoonFactory());
	} 
	
	public void addProcessors(Collection<? extends Processor<?>> processors) {
		for (Processor<?> processor : processors) {
			addProcessor(processor);
		}
	}

	public void addProcessor(Processor<?> processor) {
		processingManager().addProcessor(processor);
	}
	
	public CtSimpleType<?> modelledClass(final String qualifiedName) {
		return typeFactory().get(qualifiedName);
	}
	
	public Collection<CtSimpleType<?>> modelledClasses() {
		return typeFactory().getAll();
	}
	
	public ClassLoader classLoaderProcessing(CtSimpleType<?> modelledClass) {
		return classLoaderProcessing((Collection) asList(modelledClass));
	}
	
	public ClassLoader classLoaderProcessing(Collection<CtSimpleType<?>> modelledClasses) {
		Map<String, String> processedClasses = processedSources(modelledClasses);
		ClassLoader loader = BytecodeClassLoaderBuilder.loaderFor(processedClasses);
		return loader;
	}
	
	public ClassLoader classLoaderProcessing(Collection<CtSimpleType<?>> modelledClasses, URL[] classpath) {
		Map<String, String> processedClasses = processedSources(modelledClasses);
		ClassLoader loader = BytecodeClassLoaderBuilder.loaderFor(processedClasses, classpath);
		return loader;
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
		logDebug(logger, format("[Spoon processing of %s]", c.getQualifiedName()));
		processingManager().process(c);
	}

	private String sourceContent(CtSimpleType<?> c) {
		DefaultJavaPrettyPrinter printer = new DefaultJavaPrettyPrinter(spoonEnvironment());
		printer.scan(c);
		return c.getPackage().toString() + javaNewline() +  printer.toString();
	}
	
	protected Factory spoonFactory() {
		return factory;
	}
	
	protected TypeFactory typeFactory() {
		return spoonFactory().Type();
	}
	
	protected Environment spoonEnvironment() {
		return spoonFactory().getEnvironment();
	}

	protected DynamicClassCompiler compiler() {
		return compiler;
	}
	
	protected ProcessingManager processingManager() {
		return manager;
	}
	
	private Factory factory;
	private ProcessingManager manager;
	private DynamicClassCompiler compiler;
	
	private static Logger logger = newLoggerFor(SpoonClassLoaderFactory.class);
}