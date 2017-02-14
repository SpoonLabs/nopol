package fr.inria.lille.evo.processors;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;

public class RemoveEvosuiteEffectsProcessor extends AbstractProcessor<CtClass>{

    private static final ArrayList<String> UNWATED_ANNOTATIONS = new ArrayList<String>();
    {
        UNWATED_ANNOTATIONS.add("org.junit.runner.RunWith");
        UNWATED_ANNOTATIONS.add("org.evosuite.runtime.EvoRunnerParameters");
        UNWATED_ANNOTATIONS.add("org.junit.BeforeClass");
        UNWATED_ANNOTATIONS.add("org.junit.Before");
        UNWATED_ANNOTATIONS.add("org.junit.After");
        UNWATED_ANNOTATIONS.add("org.junit.AfterClass");
    }

    public void removeClassAnnotations(CtClass element){
        List<CtAnnotation<? extends Annotation>> annotations = element.getAnnotations();
        List<CtAnnotation<? extends Annotation>> removed = new ArrayList<CtAnnotation<? extends Annotation>>();
        for(CtAnnotation annotation : annotations){
            if(UNWATED_ANNOTATIONS.contains(annotation.getActualAnnotation().annotationType().getName())){
                removed.add(annotation);
            }
        }	
        for(CtAnnotation annotation : removed){
            element.removeAnnotation(annotation);
        }
    }

    public void removeAnnotatedMethods(CtClass element){
        List<CtMethod> removed = new ArrayList<CtMethod>();
        Set<CtMethod<?>> methods = element.getMethods();
        for(CtMethod method : methods){
            List<CtAnnotation<? extends Annotation>> annotations = method.getAnnotations();
            for(CtAnnotation annotation : annotations){
                if(UNWATED_ANNOTATIONS.contains(annotation.getActualAnnotation().annotationType().getName())){
                    removed.add(method);
                    break;
                }
            }
        }
        for(CtMethod method : removed){
            element.removeMethod(method);
        }
    }

    public void process(CtClass element) {
        removeClassAnnotations(element);
        removeAnnotatedMethods(element);
    }

}
