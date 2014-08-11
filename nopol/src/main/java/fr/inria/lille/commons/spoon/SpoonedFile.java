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
import fr.inria.lille.commons.collections.MapLibrary;
import fr.inria.lille.commons.compiler.BytecodeClassLoader;
import fr.inria.lille.commons.compiler.BytecodeClassLoaderBuilder;
import fr.inria.lille.commons.compiler.DynamicClassCompiler;
import fr.inria.lille.commons.io.JavaLibrary;
import fr.inria.lille.commons.spoon.util.SpoonModelLibrary;
import fr.inria.lille.commons.string.StringLibrary;

public abstract class SpoonedFile {
	
	protected abstract Collection<? extends CtSimpleType<?>> modelledClasses();
	
	public SpoonedFile(File sourceFile, URL[] classpath) {
		this.sourceFile = sourceFile;
		this.classpath = classpath;
		logDebug(logger, format("[Building Spoon model from %s]", sourceFile()));
		factory = SpoonModelLibrary.modelFor(sourceFile, stringClasspath());
		compiler = new DynamicClassCompiler();
		manager = new RuntimeProcessingManager(spoonFactory());
		compiledClasses = MapLibrary.newHashMap();
		prettyPrinter = new DefaultJavaPrettyPrinter(spoonEnvironment());
	}

	public ClassLoader dumpedToClassLoader() {
		return newBytecodeClassloader(compiledClasses());
	}
	
	public ClassLoader processedAndDumpedToClassLoader(Processor<?> processor) {
		return processedAndDumpedToClassLoader(asList(processor));
	}
	
	public ClassLoader processedAndDumpedToClassLoader(Collection<? extends Processor<?>> processors) {
		process(processors);
		return newBytecodeClassloader(compiledClasses());
	}
	
	public void process(Processor<?> processor) {
		process(asList(processor));
	}
	
	public void process(Collection<? extends Processor<?>> processors) {
		processModelledClasses(modelledClasses(), processors);;
	}
	
	protected synchronized void processModelledClasses(Collection<? extends CtSimpleType<?>> modelledClasses, Collection<? extends Processor<?>> processors) {
		setProcessors(processors);
		for (CtSimpleType<?> modelledClass : modelledClasses) {
			String qualifiedName = modelledClass.getQualifiedName();
			logDebug(logger, format("[Spoon processing of %s]", qualifiedName));
			processingManager().process(modelledClass);
		}
		compileModelledClasses(modelledClasses);
	}
	
	private void setProcessors(Collection<? extends Processor<?>> processors) {
		processingManager().getProcessors().clear();
		for(Processor<?> processor : processors) {
			processingManager().addProcessor(processor);
		}
	}
	
	protected byte[] compileModelledClass(CtSimpleType<?> modelledClass) {
		return compileModelledClasses(asList(modelledClass)).get(modelledClass.getQualifiedName());
	}
	
	protected Map<String, byte[]> compileModelledClasses(Collection<? extends CtSimpleType<?>> modelledClasses) {
		Map<String, String> processedSources = sourcesForModelledClasses(modelledClasses);
		Map<String, byte[]> newBytecodes = compilationFor(processedSources);
		compiledClasses().putAll(newBytecodes);
		return newBytecodes;
	}

	protected synchronized String sourceForModelledClass(CtSimpleType<?> modelledClass) {
		logDebug(logger, format("[Scanning source code of %s]", modelledClass.getQualifiedName()));
		prettyPrinter().scan(modelledClass);
		String sourceCode = modelledClass.getPackage().toString() + JavaLibrary.lineSeparator() +  prettyPrinter().toString();
		prettyPrinter().reset();
		return sourceCode;
	}
	
	protected Map<String, String> sourcesForModelledClasses(Collection<? extends CtSimpleType<?>> modelledClasses) {
		Map<String, String> processedClasses = MapLibrary.newHashMap();
		for (CtSimpleType<?> modelledClass : modelledClasses) {
			processedClasses.put(modelledClass.getQualifiedName(), sourceForModelledClass(modelledClass));
		}
		return processedClasses;
	}
	
	protected Map<String, byte[]> compilationFor(Map<String, String> processedSources) {
		return compiler().javaBytecodeFor(processedSources);
	}
	
	protected BytecodeClassLoader newBytecodeClassloader(Map<String, byte[]> compiledClasses) {
		return BytecodeClassLoaderBuilder.loaderWith(compiledClasses, classpath());
	}
	
	protected File sourceFile() {
		return sourceFile;
	}
	
	protected URL[] classpath() {
		return classpath;
	}
	
	protected String stringClasspath() {
		return StringLibrary.asClasspath(classpath);
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

	protected DynamicClassCompiler compiler() {
		return compiler;
	}

	protected Map<String, byte[]> compiledClasses() {
		return compiledClasses;
	}

	protected DefaultJavaPrettyPrinter prettyPrinter() {
		return prettyPrinter;
	}
	
	@Override
	public String toString() {
		return "Spoon model for: " + sourceFile();
	}
	
	private File sourceFile;
	private URL[] classpath;
	private Factory factory;
	private ProcessingManager manager;
	private DynamicClassCompiler compiler;
	private Map<String, byte[]> compiledClasses;
	private DefaultJavaPrettyPrinter prettyPrinter;

	protected static Logger logger = newLoggerFor(SpoonedFile.class);
}
