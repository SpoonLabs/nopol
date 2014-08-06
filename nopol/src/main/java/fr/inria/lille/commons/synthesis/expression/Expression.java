package fr.inria.lille.commons.synthesis.expression;

import java.util.Collection;
import java.util.List;

import fr.inria.lille.commons.collections.ListLibrary;


public class Expression<T> extends ObjectTemplate<T> {
	
	public static Expression<?> from(String expression, Object value) {
		return new Expression(value.getClass(), expression);
	}
	
	public static List<String> expressionsOf(Collection<? extends Expression<?>> expressions) {
		List<String> collected = ListLibrary.newArrayList();
		for (Expression<?> expression : expressions) {
			collected.add(expression.expression());
		}
		return collected;
	}

	public Expression(Class<T> resultType, String expression) {
		super(resultType);
		this.expression = expression;
	}
	
	public String expression() {
		return expression;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((expression() == null) ? 0 : expression().hashCode());
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
		Expression<?> other = (Expression<?>) obj;
		if (expression() == null) {
			if (other.expression() != null)
				return false;
		} else if (!expression().equals(other.expression()))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return expression();
	}
	
	private String expression;
}