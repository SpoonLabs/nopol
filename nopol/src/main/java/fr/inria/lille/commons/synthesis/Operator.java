package fr.inria.lille.commons.synthesis;

import java.util.List;

public class Operator<T> extends ObjectTemplate<T> {

	public Operator(Class<T> resultType, List<Parameter<?>> parameters) {
		super(resultType);
		this.parameters = parameters;
	}
	
	public boolean admitsAsArguments(List<Variable<?>> variables) {
		if (variables.size() == numberOfParameters()) {
			return admitsAll(variables);
		}
		return false;
	}

	private boolean admitsAll(List<Variable<?>> variables) {
		int index = 0;
		for (Parameter<?> parameter : parameters()) {
			Variable<?> variable = variables.get(index);
			if (! parameter.admitsAsArgument(variable)) {
				return false;
			}
			index += 1;
		}
		return true;
	}
	
	public int numberOfParameters() {
		return parameters().size();
	}
	
	private List<Parameter<?>> parameters() {
		return parameters;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
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
		Operator other = (Operator) obj;
		if (parameters == null) {
			if (other.parameters != null)
				return false;
		} else if (!parameters.equals(other.parameters))
			return false;
		return true;
	}

	private List<Parameter<?>> parameters;
}