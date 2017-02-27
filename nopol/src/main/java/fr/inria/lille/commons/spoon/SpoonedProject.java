package fr.inria.lille.commons.spoon;

import fr.inria.lille.repair.common.config.NopolContext;
import spoon.processing.Processor;
import spoon.reflect.declaration.CtType;
import xxl.java.container.classic.MetaList;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import static java.util.Arrays.asList;

public class SpoonedProject extends SpoonedFile {

    public SpoonedProject(File[] sourceFiles, NopolContext nopolContext) {
        super(sourceFiles, nopolContext);
    }

    @Override
    protected Collection<? extends CtType<?>> modelledClasses() {
        return typeFactory().getAll();
    }

    protected CtType<?> modelledClass(String qualifiedName) {
        CtType<?> ctType = typeFactory().get(qualifiedName);
        // the modelled class can only be a top level class otherwise the class will not compile
        if (ctType != null && !ctType.isTopLevel()) {
            ctType = ctType.getTopLevelType();
        }
        return ctType;
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
        if (modelledClass == null) {
            return null;
        }
        return new SpoonedClass(this, modelledClass, nopolContext);
    }

    public ClassLoader processedAndDumpedToClassLoader(Collection<String> qualifiedNames, Processor<?> processor) {
        return processedAndDumpedToClassLoader(qualifiedNames, asList(processor));
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

    public void processClasses(Collection<String> qualifiedNames, Collection<? extends Processor<?>> processors) {
        super.processModelledClasses(modelledClasses(qualifiedNames), processors);
    }

    public Map<String, String> sourcesForClasses(Collection<String> qualifiedNames) {
        return super.sourcesForModelledClasses(modelledClasses(qualifiedNames));
    }
}
