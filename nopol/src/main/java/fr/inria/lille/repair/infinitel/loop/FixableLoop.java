package fr.inria.lille.repair.infinitel.loop;

import java.util.Collection;

import xxl.java.junit.TestCase;

public class FixableLoop {

	public FixableLoop(While loop, Collection<TestCase> failingTests, Collection<TestCase> passingTests) {
		this.loop = loop;
		this.failingTests = failingTests;
		this.passingTests = passingTests;
	}
	
	public While loop() {
		return loop;
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
		result = prime * result + ((loop() == null) ? 0 : loop().hashCode());
		result = prime * result + ((failingTests() == null) ? 0 : failingTests().hashCode());
		result = prime * result + ((passingTests() == null) ? 0 : passingTests().hashCode());
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
		if (loop() == null) {
			if (other.loop() != null)
				return false;
		} else if (!loop().equals(other.loop()))
			return false;
		if (failingTests() == null) {
			if (other.failingTests() != null)
				return false;
		} else if (!failingTests().equals(other.failingTests()))
			return false;
		if (passingTests() == null) {
			if (other.passingTests() != null)
				return false;
		} else if (!passingTests().equals(other.passingTests()))
			return false;
		return true;
	}

	private While loop;
	private Collection<TestCase> failingTests;
	private Collection<TestCase> passingTests;
}
