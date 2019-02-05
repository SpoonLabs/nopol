package fr.inria.lille.commons.spoon.filter;

import spoon.reflect.code.CtAssignment;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.visitor.filter.AbstractFilter;

public class VariableAssignmentFilter extends AbstractFilter<CtAssignment<?, ?>> {

    public VariableAssignmentFilter(CtVariable<?> variable) {
        super(CtAssignment.class);
        this.variable = variable;
    }

    @Override
    public boolean matches(CtAssignment<?, ?> element) {
        return element.getAssigned().getShortRepresentation().equals(variable().getShortRepresentation());
    }

    private CtVariable<?> variable() {
        return variable;
    }

    private CtVariable<?> variable;
}
