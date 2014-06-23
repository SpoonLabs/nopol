package fr.inria.lille.commons.synthesis.expression;

import fr.inria.lille.commons.classes.ClassLibrary;

public abstract class ObjectTemplate<T> {

	public ObjectTemplate(Class<T> aClass) {
		myClass = aClass;
	}
	
	public Class<T> type() {
		return myClass;
	}
	
	public boolean typeIsSuperClassOf(Class<?> aClass) {
		return ClassLibrary.isSuperclassOf(aClass, type());
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = prime * type().getName().hashCode();
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
		ObjectTemplate<?> other = (ObjectTemplate<?>) obj;
		if (myClass == null) {
			if (other.type() != null)
				return false;
		} else if (! myClass.getName().equals(other.type().getName()))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "Object template of class " + type();
	}

	private Class<T> myClass;
}
