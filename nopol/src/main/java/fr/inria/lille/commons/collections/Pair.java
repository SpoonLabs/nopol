package fr.inria.lille.commons.collections;

import static java.lang.String.format;

public class Pair<U, V> {

	public Pair(U first, V second) {
		this.first = first;
		this.second = second;
	}
	
	public U first() {
		return first;
	}
	
	public V second() {
		return second;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((first == null) ? 0 : first.hashCode());
		result = prime * result + ((second == null) ? 0 : second.hashCode());
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
		Pair<?,?> other = (Pair<?,?>) obj;
		if (first == null) {
			if (other.first != null)
				return false;
		} else if (!first.equals(other.first))
			return false;
		if (second == null) {
			if (other.second != null)
				return false;
		} else if (!second.equals(other.second))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return format("<%s, %s>", first().toString(), second().toString());
	}

	private U first;
	private V second;
}
