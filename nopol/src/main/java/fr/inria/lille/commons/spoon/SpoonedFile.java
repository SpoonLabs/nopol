package fr.inria.lille.commons.spoon;

import fr.inria.lille.commons.spoon.util.SpoonModelLibrary;
import fr.inria.lille.repair.common.config.NopolContext;
import org.apache.log4j.Level;
import org.slf4j.Logger;
import spoon.compiler.Environment;
import spoon.processing.ProcessInterruption;
import spoon.processing.ProcessingManager;
import spoon.processing.Processor;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.support.JavaOutputProcessor;
import spoon.support.RuntimeProcessingManager;
import spoon.support.StandardEnvironment;
import xxl.java.compiler.BytecodeClassLoader;
import xxl.java.compiler.BytecodeClassLoaderBuilder;
import xxl.java.compiler.DynamicClassCompiler;
import xxl.java.container.classic.MetaList;
import xxl.java.container.classic.MetaMap;
import xxl.java.container.classic.MetaSet;
import xxl.java.library.JavaLibrary;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static xxl.java.library.LoggerLibrary.loggerFor;

public abstract class SpoonedFile {

    private File[] sourceFiles;
    private URL[] projectClasspath;
    private URL[] compilationClasspath;
    private Factory factory;
    private ProcessingManager manager;
    private DynamicClassCompiler compiler;
    private Map<String, byte[]> compiledClasses;
    private DefaultJavaPrettyPrinter prettyPrinter;

    protected final NopolContext nopolContext;

    public SpoonedFile(File[] sourceFiles, NopolContext nopolContext) {
        this.nopolContext = nopolContext;
        this.sourceFiles = sourceFiles;
        this.projectClasspath = nopolContext.getProjectClasspath();

        factory = SpoonModelLibrary.newFactory();
        factory.getEnvironment().setComplianceLevel(nopolContext.getComplianceLevel());
        factory.getEnvironment().setCommentEnabled(false);
        factory.getEnvironment().setLevel(Level.OFF.toString());

        factory = SpoonModelLibrary.modelFor(factory, sourceFiles, projectClasspath());

        compiler = new DynamicClassCompiler(compilationClasspath());
        manager = new RuntimeProcessingManager(factory);
        compiledClasses = MetaMap.newHashMap();
        prettyPrinter = new DefaultJavaPrettyPrinter(spoonEnvironment());
    }

    protected abstract Collection<? extends CtType<?>> modelledClasses();

    public void generateOutputFile(File destinationFolder) {
        Processor<?> writer = new JavaOutputProcessor(destinationFolder, new DefaultJavaPrettyPrinter(new StandardEnvironment()));
        process(writer);
    }

    public void generateOutputCompiledFile(File destinationFolder) throws IOException {
        Iterator<String> it = this.compiledClasses.keySet().iterator();
        while (it.hasNext()) {
            String className = it.next();
            String fileName = className.replace(".", "/") + ".class";
            File classFile = new File(destinationFolder.getAbsolutePath() + "/" + fileName);
            if (!classFile.exists()) {
                classFile.getParentFile().mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(classFile);
            fos.write(this.compiledClasses.get(className));
            fos.close();
        }
    }

    public Collection<CtPackage> allPackages() {
        return spoonFactory().Package().getAll();
    }

    public Collection<CtPackage> topPackages() {
        Collection<CtPackage> topPackages = MetaSet.newHashSet();
        for (CtPackage aPackage : allPackages()) {
            if (!aPackage.getTypes().isEmpty()) {
                CtPackage parent = aPackage.getParent(CtPackage.class);
                if (parent == null || parent.getTypes().isEmpty()) {
                    topPackages.add(aPackage);
                }
            }
        }
        return topPackages;
    }

    public Collection<String> allPackageNames() {
        return packageNames(allPackages());
    }

    public Collection<String> topPackageNames() {
        return packageNames(topPackages());
    }

    public Collection<String> packageNames(Collection<CtPackage> packages) {
        Collection<String> names = MetaList.newArrayList();
        for (CtPackage aPackage : packages) {
            names.add(aPackage.getQualifiedName());
        }
        return names;
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
        processModelledClasses(modelledClasses(), processors);
    }

    protected synchronized void processModelledClasses(Collection<? extends CtType<?>> modelledClasses, Collection<? extends Processor<?>> processors) {
        setProcessors(processors);
        for (CtType<?> modelledClass : modelledClasses) {
            String qualifiedName = modelledClass.getQualifiedName();
            //logDebug(logger(), format("[Spoon processing of %s]", qualifiedName));
            try {
                processingManager().process(modelledClass);
            } catch (ProcessInterruption e) {
                continue;
            }
        }
        compileModelledClasses(modelledClasses);
    }

    private void setProcessors(Collection<? extends Processor<?>> processors) {
        processingManager().getProcessors().clear();
        for (Processor<?> processor : processors) {
            processingManager().addProcessor(processor);
        }
    }

    protected byte[] compileModelledClass(CtType<?> modelledClass) {
        return compileModelledClasses(asList(modelledClass)).get(modelledClass.getQualifiedName());
    }

    protected Map<String, byte[]> compileModelledClasses(Collection<? extends CtType<?>> modelledClasses) {
        Map<String, String> processedSources = sourcesForModelledClasses(modelledClasses);
        Map<String, byte[]> newBytecodes = compilationFor(processedSources);
        compiledClasses().putAll(newBytecodes);
        return newBytecodes;
    }

    protected synchronized String sourceForModelledClass(CtType<?> modelledClass) {
        //logDebug(logger(), format("[Scanning source code of %s]", modelledClass.getQualifiedName()));
        prettyPrinter().scan(modelledClass);
        String packageDeclaration = "package " + modelledClass.getPackage().getQualifiedName() + ";";
        String sourceCode = packageDeclaration + JavaLibrary.lineSeparator() + prettyPrinter().toString();
        prettyPrinter().reset();
        return sourceCode;
    }

    protected Map<String, String> sourcesForModelledClasses(Collection<? extends CtType<?>> modelledClasses) {
        Map<String, String> processedClasses = MetaMap.newHashMap();
        for (CtType<?> modelledClass : modelledClasses) {
            processedClasses.put(modelledClass.getQualifiedName(), sourceForModelledClass(modelledClass));
        }
        return processedClasses;
    }

    protected Map<String, byte[]> compilationFor(Map<String, String> processedSources) {
        return compiler().javaBytecodeFor(processedSources, compiledClasses());
    }

    protected BytecodeClassLoader newBytecodeClassloader(Map<String, byte[]> compiledClasses) {
        return BytecodeClassLoaderBuilder.loaderWith(compiledClasses, compilationClasspath());
    }

    protected File[] sourceFiles() {
        return sourceFiles;
    }

    public URL[] projectClasspath() {
        return projectClasspath;
    }

    public URL[] compilationClasspath() {
        if (compilationClasspath == null) {
            List<URL> urls = MetaList.newArrayList(projectClasspath());
            urls.addAll(asList(JavaLibrary.systemClasspathURLs()));
            compilationClasspath = urls.toArray(new URL[urls.size()]);
        }
        return compilationClasspath;
    }

    public Factory spoonFactory() {
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
        return "Spoon model for: " + sourceFiles();
    }

    private Logger logger() {
        return loggerFor(this);
    }
}
