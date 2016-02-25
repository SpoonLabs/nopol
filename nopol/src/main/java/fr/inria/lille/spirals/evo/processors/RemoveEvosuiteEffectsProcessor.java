package fr.inria.lille.spirals.evo.processors;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;

public class RemoveEvosuiteEffectsProcessor extends AbstractProcessor<CtClass>{

	private static final ArrayList<String> UnwantedAnnotations = new ArrayList<String>();
	{
		UnwantedAnnotations.add("org.junit.runner.RunWith");
		UnwantedAnnotations.add("org.evosuite.runtime.EvoRunnerParameters");
		UnwantedAnnotations.add("org.junit.BeforeClass");
		UnwantedAnnotations.add("org.junit.Before");
		UnwantedAnnotations.add("org.junit.After");
		UnwantedAnnotations.add("org.junit.AfterClass");
	}
	
	public void removeClassAnnotations(CtClass element){
		List<CtAnnotation<? extends Annotation>> annotations = element.getAnnotations();
		List<CtAnnotation<? extends Annotation>> removed = new ArrayList<CtAnnotation<? extends Annotation>>();
		//System.out.println("####["+element.getSimpleName()+"] Remove Annotations");
		for(CtAnnotation annotation : annotations){
			if(UnwantedAnnotations.contains(annotation.getActualAnnotation().annotationType().getName())){
				removed.add(annotation);
			}
		}	
		for(CtAnnotation annotation : removed){
			//System.out.println("    remove "+annotation);
			element.removeAnnotation(annotation);
		}
	}
	
	public void removeAnnotatedMethods(CtClass element){
		List<CtMethod> removed = new ArrayList<CtMethod>();
		Set<CtMethod<?>> methods = element.getMethods();
		for(CtMethod method : methods){
			List<CtAnnotation<? extends Annotation>> annotations = method.getAnnotations();
			for(CtAnnotation annotation : annotations){
				if(UnwantedAnnotations.contains(annotation.getActualAnnotation().annotationType().getName())){
					removed.add(method);
					break;
				}
			}
		}
		//System.out.println("####["+element.getSimpleName()+"] Remove annotated method");
		for(CtMethod method : removed){
			//System.out.println("    remove "+method.getSimpleName());
			element.removeMethod(method);
		}
	}
	
	public void process(CtClass element) {
		removeClassAnnotations(element);
		removeAnnotatedMethods(element);
	}

}
