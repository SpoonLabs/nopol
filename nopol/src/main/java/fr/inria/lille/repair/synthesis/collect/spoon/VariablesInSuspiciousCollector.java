package fr.inria.lille.repair.synthesis.collect.spoon;

import fr.inria.lille.repair.nopol.SourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.AbstractFilter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Thomas Durieux on 10/03/15.
 */
public class VariablesInSuspiciousCollector extends AbstractProcessor<CtTypedElement<?>> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Set<CtTypedElement> variables;
    private final SourceLocation location;

    public VariablesInSuspiciousCollector(SourceLocation location) {
        super();
        variables = new HashSet<>();
        this.location = location;
    }

    @Override
    public boolean isToBeProcessed(CtTypedElement<?> candidate) {
        if (candidate.getPosition() == null) {
            return false;
        }
        return candidate.getPosition().getLine() == location.getLineNumber();
    }

    public Set<CtTypedElement> getVariables() {
        return variables;
    }

    @Override
    public void process(CtTypedElement element) {
        List<CtTypedElement> elems = element.getElements(new AbstractFilter<CtTypedElement>(CtTypedElement.class) {
            @Override
            public boolean matches(CtTypedElement CtTypedElement) {
                return true;
            }
        });
        for (CtTypedElement ctTypedElement : elems) {
            if (ctTypedElement instanceof CtVariableAccess ||
                    ctTypedElement instanceof CtLiteral ||
                    ctTypedElement instanceof CtFieldAccess ||
                    ctTypedElement instanceof CtInvocation) {
                if (variables.contains(ctTypedElement)) {
                    return;
                }
                variables.add(ctTypedElement);
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
