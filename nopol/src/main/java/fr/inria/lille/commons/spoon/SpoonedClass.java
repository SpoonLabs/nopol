package fr.inria.lille.commons.spoon;

import fr.inria.lille.repair.common.config.NopolContext;
import spoon.reflect.declaration.CtType;

import java.io.File;
import java.util.Collection;

import static java.util.Arrays.asList;

public class SpoonedClass extends SpoonedFile {

    public SpoonedClass(SpoonedProject parentProject, CtType<?> modelledClass, NopolContext nopolContext) {
        super(new File[] { modelledClass.getPosition().getFile() }, nopolContext);
        this.simpleType = modelledClass;
        this.parentProject = parentProject;
        qualifiedClassName = modelledClass.getQualifiedName();
        compiledClasses().putAll(parentProject().compiledClasses());
    }

    @Override
    protected Collection<? extends CtType<?>> modelledClasses() {
        return asList(modelledClass());
    }

    public String qualifiedClassName() {
        return qualifiedClassName;
    }

    public String sourceCode() {
        return super.sourceForModelledClass(modelledClass());
    }

    public CtType<?> getSimpleType() {
        return simpleType;
    }

    protected CtType<?> modelledClass() {
        return typeFactory().get(qualifiedClassName());
    }

    protected SpoonedProject parentProject() {
        return parentProject;
    }

    private String qualifiedClassName;
    private SpoonedProject parentProject;
    private CtType<?> simpleType;
}
