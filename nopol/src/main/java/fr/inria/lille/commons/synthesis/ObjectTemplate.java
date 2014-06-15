package fr.inria.lille.commons.synthesis;

public abstract class ObjectTemplate<T> {

	public ObjectTemplate(Class<T> aClass) {
		myClass = aClass;
	}
	
	protected Class<T> myClass() {
		return myClass;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = prime * myClass().getName().hashCode();
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
		ObjectTemplate other = (ObjectTemplate) obj;
		if (myClass == null) {
			if (other.myClass != null)
				return false;
		} else if (! myClass.getName().equals(other.myClass.getName()))
			return false;
		return true;
	}

	private Class<T> myClass;
}
