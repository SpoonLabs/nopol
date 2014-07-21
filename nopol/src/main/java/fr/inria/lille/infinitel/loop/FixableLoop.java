package fr.inria.lille.infinitel.loop;

import java.util.Collection;

import spoon.reflect.cu.SourcePosition;
import fr.inria.lille.commons.suite.TestCase;

public class FixableLoop {

	public FixableLoop(SourcePosition position, Collection<TestCase> failingTests, Collection<TestCase> passingTests) {
		this.position = position;
		this.failingTests = failingTests;
		this.passingTests = passingTests;
	}
	
	public SourcePosition position() {
		return position;
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
		int result = 1;
		result = prime * result + ((failingTests == null) ? 0 : failingTests.hashCode());
		result = prime * result + ((passingTests == null) ? 0 : passingTests.hashCode());
		result = prime * result + ((position == null) ? 0 : position.hashCode());
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
		if (position == null) {
			if (other.position != null)
				return false;
		} else if (!position.equals(other.position))
			return false;
		return true;
	}

	private SourcePosition position;
	private Collection<TestCase> failingTests;
	private Collection<TestCase> passingTests;
}
