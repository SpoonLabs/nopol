package fr.inria.lille.commons.synthesis;

public class Variable<T> extends ObjectTemplate<T> {
	
	public static Variable from(String name, Object value) {
		return new Variable(value.getClass(), name, value);
	}
	
	public Variable(Class<T> variableClass, String name, T value) {
		super(variableClass);
		this.name = name;
		this.value = value;
	}
	
	public String name() {
		return name;
	}
	
	public T value() {
		return value;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Variable other = (Variable) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	private T value;
	private String name;
}
