package fr.inria.lille.evo.processors;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * This processor remove all methods that are not methodTested and not in keptMethods list.
 * just compare the signatures of methods.
 * @author dufaux
 *
 */
public class TestSelectionProcessor extends AbstractProcessor<CtMethod<?>>{

    private List<CtMethod<?>> keptMethods;

    public TestSelectionProcessor(List<CtMethod<?>> keptMethods ){
        this.keptMethods = keptMethods;
    }

    public void process(CtMethod<?> element) {
        element.getParent(CtClass.class).removeMethod(element);
    }

    @Override
    public boolean isToBeProcessed(CtMethod element){

        boolean isTest = false;
        for(CtAnnotation<? extends Annotation> annotation : element.getAnnotations()){
            if (annotation.getType().equals(annotation.getFactory().Annotation().createReference(org.junit.Test.class))) {
                isTest = true;
            }
        }

        if(!isTest){
            return false; //keep this one
        }

        for(CtMethod<?> method : keptMethods){
            if(element.getSignature().equals(method.getSignature())){
                return false; //keep this one
            }
        }

        return super.isToBeProcessed(element);
    }

}
