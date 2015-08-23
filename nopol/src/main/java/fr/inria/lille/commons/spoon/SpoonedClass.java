package fr.inria.lille.commons.spoon;

import static java.util.Arrays.asList;

import java.io.File;
import java.util.Collection;

import spoon.reflect.declaration.CtType;

public class SpoonedClass extends SpoonedFile {

	public SpoonedClass(SpoonedProject parentProject, CtType<?> modelledClass) {
		super(new File[] {modelledClass.getPosition().getFile()}, parentProject.projectClasspath());
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
