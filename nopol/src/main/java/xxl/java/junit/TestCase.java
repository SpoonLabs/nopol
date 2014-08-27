package xxl.java.junit;

import java.util.Collection;

import xxl.java.container.classic.MetaSet;

public class TestCase {

	public static TestCase from(String qualifiedClassName, String testName) {
		return new TestCase(qualifiedClassName, testName);
	}
	
	public static Collection<String> testClasses(Collection<TestCase> testCases) {
		Collection<String> testClasses = MetaSet.newHashSet();
		for (TestCase testCase : testCases) {
			testClasses.add(testCase.className());
		}
		return testClasses;
	}
	
	public static Collection<String> testNames(Collection<TestCase> testCases) {
    	Collection<String> testNames = MetaSet.newHashSet();
    	for (TestCase testCase : testCases) {
    		testNames.add(testCase.testName());
    	}
    	return testNames;
	}
	
	private TestCase(String qualifiedClassName, String testName) {
		this.qualifiedClassName = qualifiedClassName;
		this.testName = testName;
	}
	
	public String className() {
		return qualifiedClassName;
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
		int result = prime + ((qualifiedClassName == null) ? 0 : qualifiedClassName.hashCode());
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
		if (qualifiedClassName == null) {
			if (other.qualifiedClassName != null)
				return false;
		} else if (!qualifiedClassName.equals(other.qualifiedClassName))
			return false;
		if (testName == null) {
			if (other.testName != null)
				return false;
		} else if (!testName.equals(other.testName))
			return false;
		return true;
	}

	private String qualifiedClassName;
	private String testName;
}
