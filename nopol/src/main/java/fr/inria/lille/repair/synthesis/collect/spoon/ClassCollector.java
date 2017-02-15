package fr.inria.lille.repair.synthesis.collect.spoon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.reference.CtTypeReference;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Thomas Durieux on 10/03/15.
 */
public class ClassCollector extends AbstractProcessor<CtTypedElement<?>> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Set<String> classes;
    private final String buggyMethod;

    public ClassCollector(String buggyMethod) {
        super();
        classes = new HashSet<>();
        this.buggyMethod = buggyMethod;
    }

    @Override
    public boolean isToBeProcessed(CtTypedElement<?> candidate) {
        CtMethod parent = candidate.getParent(CtMethod.class);
        if (parent == null) {
            return false;
        }
        return parent.getSimpleName().equals(buggyMethod);
    }

    public Set<String> getClasses() {
        return classes;
    }

    @Override
    public void process(CtTypedElement element) {
        List<CtTypeReference<?>> listDependencies = getDependencies(element);

        CtTypeReference<Exception> exception = getFactory().Class().createReference(Exception.class);
        for (CtTypeReference<?> ctTypeReference : listDependencies) {
            if (ctTypeReference == null) {
                continue;
            }
            if (ctTypeReference.isPrimitive()) {
                continue;
            }
            if (ctTypeReference.toString().equals("<nulltype>")) {
                continue;
            }
            if (exception.isAssignableFrom(ctTypeReference)) {
                continue;
            }
            if (classes.contains(ctTypeReference.getQualifiedName())) {
                return;
            }
            if (classes.add(ctTypeReference.getQualifiedName())) {
                logger.debug("[class] " + ctTypeReference.getQualifiedName());
            }
        }

    }

    /**
     * get all dependencies added by a CtTypedElement
     *
     * @param element
     * @return all dependencies added by element
     */
    private List<CtTypeReference<?>> getDependencies(CtTypedElement<?> element) {
        List<CtTypeReference<?>> listDependencies = new ArrayList<>();
        // Literal
        if (element instanceof CtAnnotation) {
            return listDependencies;
        }
        if (element instanceof CtLiteral<?>) {
            CtLiteral<?> literal = (CtLiteral<?>) element;
            literal.getValue();
            if (literal.getValue() instanceof CtTypeReference<?>) {
                listDependencies.add((CtTypeReference<?>) literal.getValue());
            } else if (literal.getValue() instanceof CtTypedElement<?>) {
                listDependencies.add(((CtTypedElement<?>) literal.getValue())
                        .getType());
            }
        }
        // method invocation
        if (element instanceof CtInvocation<?>) {
            CtInvocation<?> invocation = (CtInvocation<?>) element;
            // the class of the method
            listDependencies.add(invocation.getExecutable().getDeclaringType());
        }
        listDependencies.add(element.getType());
        return listDependencies;
    }
}
