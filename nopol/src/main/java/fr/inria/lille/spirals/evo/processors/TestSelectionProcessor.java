package fr.inria.lille.spirals.evo.processors;

import java.lang.annotation.Annotation;
import java.util.List;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;

/**
 * This processor remove all method which are not contains in keptMethod and methodTested
 * just compare the signatures of methods.
 * @author dufaux
 *
 */
public class TestSelectionProcessor extends AbstractProcessor<CtMethod>{

	private CtMethod methodTested;
	private List<CtMethod> keptMethods;

	public TestSelectionProcessor(CtMethod methodTested, List<CtMethod> keptMethods ){
		this.methodTested = methodTested;
		this.keptMethods = keptMethods;
	}

	public void process(CtMethod element) {
		
		//getEnvironment().setAutoImports(true);
		boolean isTest = false;
		for(CtAnnotation<? extends Annotation> annotation : element.getAnnotations()){
			if(annotation.getSignature().equals("@org.junit.Test")){
				isTest = true;
			}
		}
		
		if(!isTest){
			//System.out.println("keep '"+element.getSignature()+"'");
			return; //keep this one
		}
		
		if(methodTested != null && element.getSignature().equals(methodTested.getSignature())){
			//System.out.println("keep '"+element.getSignature()+"'");
			return; //keep this one
		}
		
		for(CtMethod method : keptMethods){
			if(element.getSignature().equals(method.getSignature())){
				//System.out.println("keep '"+element.getSignature()+"'");
				return; //keep this one
			}
		}
		
		//System.out.println("remove "+element.getSignature());
		element.getParent(CtClass.class).removeMethod(element);
	}

}
