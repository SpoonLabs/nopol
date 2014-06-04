package fr.inria.lille.commons.suite;

public class TestCase {

	public TestCase(String className, String testName) {
		this.className = className;
		this.testName = testName;
	}
	
	public String className() {
		return className;
	}
	
	public String testName() {
		return testName;
	}
	
	@Override
	public String toString() {
		return className() + "#" + testName();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = prime + ((className == null) ? 0 : className.hashCode());
		result = prime * result + ((testName == null) ? 0 : testName.hashCode());
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
		TestCase other = (TestCase) obj;
		if (className == null) {
			if (other.className != null)
				return false;
		} else if (!className.equals(other.className))
			return false;
		if (testName == null) {
			if (other.testName != null)
				return false;
		} else if (!testName.equals(other.testName))
			return false;
		return true;
	}

	private String className;
	private String testName;
}
