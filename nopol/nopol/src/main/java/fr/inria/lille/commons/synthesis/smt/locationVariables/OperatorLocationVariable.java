package fr.inria.lille.commons.synthesis.smt.locationVariables;

import fr.inria.lille.commons.synthesis.operator.Operator;
import fr.inria.lille.commons.synthesis.operator.Parameter;
import xxl.java.container.classic.MetaList;

import java.util.Collection;
import java.util.List;

public class OperatorLocationVariable<T> extends LocationVariable<T> {

    public OperatorLocationVariable(Operator<T> operator, String subexpression) {
        super(operator, subexpression);
        parameterLocationVariables = parameterLocationVariablesFor(operator);
    }

    private List<ParameterLocationVariable<?>> parameterLocationVariablesFor(Operator<T> operator) {
        List<ParameterLocationVariable<?>> variables = MetaList.newArrayList();
        int parameterIndex = 0;
        for (Parameter<?> parameter : operator.parameters()) {
            variables.add(new ParameterLocationVariable(parameter, subexpression() + "<" + parameterIndex + ">", this));
            parameterIndex += 1;
        }
        return variables;
    }

    @Override
    public Operator<T> objectTemplate() {
        return (Operator<T>) super.objectTemplate();
    }

    public Collection<ParameterLocationVariable<?>> parameterLocationVariables() {
        return parameterLocationVariables;
    }

    private List<ParameterLocationVariable<?>> parameterLocationVariables;
}
