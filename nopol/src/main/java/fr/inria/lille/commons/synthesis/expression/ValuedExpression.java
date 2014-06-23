package fr.inria.lille.commons.synthesis.expression;

public class ValuedExpression<T> extends Expression<T> {

	public static ValuedExpression<?> from(String expression, Object value) {
		return new ValuedExpression(value.getClass(), expression, value);
	}
	
	public ValuedExpression(Class<T> resultType, String expression, T value) {
		super(resultType, expression);
		this.value = value;
	}

	public T value() {
		return value;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		ValuedExpression<?> other = (ValuedExpression<?>) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	private T value;
}
