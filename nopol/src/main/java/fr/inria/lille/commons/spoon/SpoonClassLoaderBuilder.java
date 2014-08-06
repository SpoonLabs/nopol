package fr.inria.lille.commons.spoon;

import static fr.inria.lille.commons.utils.LoggerLibrary.logDebug;
import static fr.inria.lille.commons.utils.LoggerLibrary.newLoggerFor;
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
import fr.inria.lille.commons.collections.ListLibrary;
import fr.inria.lille.commons.collections.MapLibrary;
import fr.inria.lille.commons.compiler.BytecodeClassLoader;
import fr.inria.lille.commons.compiler.BytecodeClassLoaderBuilder;
import fr.inria.lille.commons.compiler.DynamicClassCompiler;
import fr.inria.lille.commons.io.JavaLibrary;
import fr.inria.lille.commons.spoon.util.SpoonModelLibrary;

public class SpoonClassLoaderBuilder {
	
	public SpoonClassLoaderBuilder(File sourceFile) {
		logDebug(logger, format("[Building Spoon model from %s]", sourceFile.getPath()));
		factory = SpoonModelLibrary.modelFor(sourceFile);
		manager = new RuntimeProcessingManager(spoonFactory());
		compiler = new DynamicClassCompiler();
		compiledClasses = compilationOf(allModelledClasses());
	}
	
	public Collection<CtSimpleType<?>> allModelledClasses() {
		return typeFactory().getAll();
	}
	
	public Collection<CtSimpleType<?>> modelledClasses(Collection<String> qualifiedNames) {
		Collection<CtSimpleType<?>> modelledClasses = ListLibrary.newLinkedList();
		for (String qualifiedName : qualifiedNames) {
			modelledClasses.add(modelledClass(qualifiedName));
		}
		return modelledClasses;
	}
	
	public CtSimpleType<?> modelledClass(String qualifiedName) {
		return typeFactory().get(qualifiedName);
	}
	
	public ClassLoader buildSpooning(String qualifiedName, URL[] classpath, Processor<?> processor) {
		return buildSpooning(asList(qualifiedName), classpath, asList(processor));
	}
	
	public ClassLoader buildSpooning(Collection<String> qualifiedNames, URL[] classpath, Collection<? extends Processor<?>> processors) {
		return buildSpooningModel(modelledClasses(qualifiedNames), classpath, processors);
	}
	
	public ClassLoader buildSpooningAll(URL[] classpath, Collection<? extends Processor<?>> processors) {
		return buildSpooningModel(allModelledClasses(), classpath, processors);
	}
	
	public ClassLoader buildSpooningModel(Collection<CtSimpleType<?>> modelledClasses, URL[] classpath, Collection<? extends Processor<?>> processors) {
		processClasses(modelledClasses, processors);
		return newBytecodeClassloader(compiledClasses(), classpath);
	}
	
	public ClassLoader buildWithCurrentModel(URL[] classpath) {
		return newBytecodeClassloader(compiledClasses(), classpath);
	}
	
	private BytecodeClassLoader newBytecodeClassloader(Map<String, byte[]> compiledClasses, URL[] classpath) {
		return BytecodeClassLoaderBuilder.loaderWith(compiledClasses, classpath);
	}
	
	public void processClass(String qualifiedName, Processor<?> processor) {
		processClass(qualifiedName, asList(processor));
	}
	
	public void processClass(String qualifiedName, Collection<? extends Processor<?>> processors) {
		processClasses(modelledClasses(asList(qualifiedName)), processors);
	}
	
	public void processClasses(Collection<CtSimpleType<?>> modelledClasses, Collection<? extends Processor<?>> processors) {
		setProcessors(processors);
		for (CtSimpleType<?> modelledClass : modelledClasses) {
			String qualifiedName = modelledClass.getQualifiedName();
			logDebug(logger, format("[Spoon processing of %s]", qualifiedName));
			processingManager().process(modelledClass);
		}
		compiledClasses().putAll(compilationOf(modelledClasses));
	}
	
	private void setProcessors(Collection<? extends Processor<?>> processors) {
		processingManager().getProcessors().clear();
		for(Processor<?> processor : processors) {
			processingManager().addProcessor(processor);
		}
	}
	
	private Map<String, byte[]> compilationOf(Collection<CtSimpleType<?>> modelledClasses) {
		Map<String, String> processedSources = sourcesFrom(modelledClasses);
		Map<String, byte[]> newBytecodes = compiler().javaBytecodeFor(processedSources);
		return newBytecodes;
	}
	
	private Map<String, String> sourcesFrom(Collection<CtSimpleType<?>> modelledClasses) {
		Map<String, String> processedClasses = MapLibrary.newHashMap();
		for (CtSimpleType<?> modelledClass : modelledClasses) {
			processedClasses.put(modelledClass.getQualifiedName(), sourceFrom(modelledClass));
		}
		return processedClasses;
	}
	
	private String sourceFrom(CtSimpleType<?> c) {
		DefaultJavaPrettyPrinter printer = new DefaultJavaPrettyPrinter(spoonEnvironment());
		printer.scan(c);
		return c.getPackage().toString() + JavaLibrary.lineSeparator() +  printer.toString();
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

	protected ProcessingManager processingManager() {
		return manager;
	}
	
	private DynamicClassCompiler compiler() {
		return compiler;
	}
	
	private Map<String, byte[]> compiledClasses() {
		return compiledClasses;
	}
	
	private Factory factory;
	private ProcessingManager manager;
	private DynamicClassCompiler compiler;
	private Map<String, byte[]> compiledClasses;
	
	private static Logger logger = newLoggerFor(SpoonClassLoaderBuilder.class);
}