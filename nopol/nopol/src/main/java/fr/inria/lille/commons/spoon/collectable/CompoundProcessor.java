package fr.inria.lille.commons.spoon.collectable;

import spoon.processing.AbstractProcessor;
import spoon.processing.Processor;
import spoon.reflect.code.CtCodeElement;
import xxl.java.container.classic.MetaList;

import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;

public class CompoundProcessor<T extends CtCodeElement> extends AbstractProcessor<T> {

    public CompoundProcessor(Class<T> targetClass, Processor<? super T>... processors) {
        this(targetClass, asList(processors));
    }

    public CompoundProcessor(Class<T> targetClass, Collection<Processor<? super T>> processors) {
        this.targetCtClass = targetClass;
        subprocessors = MetaList.newLinkedList();
        subprocessors().addAll(processors);
    }

    @Override
    public boolean isToBeProcessed(T element) {
        return targetCtClass().isInstance(element);
    }

    @Override
    public void process(T element) {
        for (Processor<? super T> subprocessor : subprocessors()) {
            subprocessor.process(element);
        }
    }

    private Class<T> targetCtClass() {
        return targetCtClass;
    }

    private List<Processor<? super T>> subprocessors() {
        return subprocessors;
    }

    private Class<T> targetCtClass;
    private List<Processor<? super T>> subprocessors;
}
