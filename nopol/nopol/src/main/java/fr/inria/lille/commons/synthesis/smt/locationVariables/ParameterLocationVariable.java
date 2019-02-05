package fr.inria.lille.commons.synthesis.smt.locationVariables;

import fr.inria.lille.commons.synthesis.operator.Parameter;


public class ParameterLocationVariable<T> extends LocationVariable<T> {

    public ParameterLocationVariable(Parameter<T> parameter, String subexpression, OperatorLocationVariable<?> operatorLocationVariable) {
        super(parameter, subexpression);
        this.operatorLocationVariable = operatorLocationVariable;
    }

    public OperatorLocationVariable<?> operatorLocationVariable() {
        return operatorLocationVariable;
    }

    @Override
    public Parameter<T> objectTemplate() {
        return (Parameter<T>) super.objectTemplate();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((operatorLocationVariable == null) ? 0 : operatorLocationVariable.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        ParameterLocationVariable<?> other = (ParameterLocationVariable<?>) obj;
        if (operatorLocationVariable == null) {
            if (other.operatorLocationVariable != null)
                return false;
        } else if (!operatorLocationVariable.equals(other.operatorLocationVariable))
            return false;
        return true;
    }

    private OperatorLocationVariable<?> operatorLocationVariable;
}
