package fr.inria.lille.commons.trace;

import static java.lang.String.format;

import java.util.Map;

import fr.inria.lille.commons.collections.MapLibrary;

public class Specification<T> {

	public Specification(Map<String, Object> values, T expectedOutput) {
		this.values = MapLibrary.copyOf(values);
		this.expectedOutput = expectedOutput;
	}
	
	public Map<String, Object> inputs() {
		return values;
	}
	
	public T output() {
		return expectedOutput;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((expectedOutput == null) ? 0 : expectedOutput.hashCode());
		result = prime * result + ((values == null) ? 0 : values.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Specification<?> other = (Specification<?>) obj;
		if (expectedOutput == null) {
			if (other.expectedOutput != null)
				return false;
		} else if (!expectedOutput.equals(other.expectedOutput))
			return false;
		if (values == null) {
			if (other.values != null)
				return false;
		} else if (!values.equals(other.values))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return format("input: %s. output: %s", inputs().toString(), output().toString());
	}

	private T expectedOutput;
	private Map<String, Object> values;
}
