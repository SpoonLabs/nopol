package fr.inria.lille.repair.infinitel.loop;

import java.util.Collection;

import fr.inria.lille.commons.suite.TestCase;

public class FixableLoop extends While {

	public FixableLoop(While loop, Collection<TestCase> failingTests, Collection<TestCase> passingTests) {
		super(loop.astLoop(), loop.breakStatements(), loop.returnStatements());
		this.failingTests = failingTests;
		this.passingTests = passingTests;
	}

	public Collection<TestCase> failingTests() {
		return failingTests;
	}
	
	public Collection<TestCase> passingTests() {
		return passingTests;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((failingTests == null) ? 0 : failingTests.hashCode());
		result = prime * result + ((passingTests == null) ? 0 : passingTests.hashCode());
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
		FixableLoop other = (FixableLoop) obj;
		if (failingTests == null) {
			if (other.failingTests != null)
				return false;
		} else if (!failingTests.equals(other.failingTests))
			return false;
		if (passingTests == null) {
			if (other.passingTests != null)
				return false;
		} else if (!passingTests.equals(other.passingTests))
			return false;
		return true;
	}

	private Collection<TestCase> failingTests;
	private Collection<TestCase> passingTests;
}
