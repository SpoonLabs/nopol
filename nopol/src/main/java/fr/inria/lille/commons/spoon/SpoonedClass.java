package fr.inria.lille.commons.spoon;

import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.Map;

import spoon.reflect.declaration.CtSimpleType;

public class SpoonedClass extends SpoonedFile {

	public SpoonedClass(SpoonedProject parentProject, CtSimpleType<?> modelledClass) {
		super(modelledClass.getPosition().getFile(), parentProject.classpath());
		this.parentProject = parentProject;
		qualifiedClassName = modelledClass.getQualifiedName();
		compiledClasses().putAll(parentProject().compiledClasses());
	}
	
	@Override
	protected Collection<? extends CtSimpleType<?>> modelledClasses() {
		return asList(modelledClass());
	}
	
	@Override
	protected Map<String, byte[]> compilationFor(Map<String, String> processedSources) {
		compiledClasses().remove(qualifiedClassName());
		return compiler().javaBytecodeFor(processedSources, compiledClasses());
	}
	
	public String qualifiedClassName() {
		return qualifiedClassName;
	}
	
	public String sourceCode() {
		return super.sourceForModelledClass(modelledClass());
	}
	
	protected CtSimpleType<?> modelledClass() {
		return typeFactory().get(qualifiedClassName());
	}
	
	protected SpoonedProject parentProject() {
		return parentProject;
	}
	
	private String qualifiedClassName;
	private SpoonedProject parentProject;
}
