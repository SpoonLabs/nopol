/*
 * Copyright (C) 2013 INRIA
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package fr.inria.lille.jsemfix.test.junit;

import static com.google.common.base.Preconditions.checkNotNull;

import org.junit.runner.Description;

import fr.inria.lille.jsemfix.test.Test;

/**
 * @author Favio D. DeMarco
 *
 */
public final class JUnitTest implements Test {

	private final Description failure;

	/**
	 * @param failure
	 * 
	 */
	public JUnitTest(final Description failure) {
		this.failure = checkNotNull(failure);
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		JUnitTest other = (JUnitTest) obj;
		if (this.failure == null) {
			if (other.failure != null) {
				return false;
			}
		} else if (!this.failure.equals(other.failure)) {
			return false;
		}
		return true;
	}

	@Override
	public String getClassName() {
		return this.failure.getClassName();
	}

	@Override
	public String getMethodName() {
		return this.failure.getMethodName();
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.failure.hashCode();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JUnitTest [failure=");
		builder.append(this.failure);
		builder.append("]");
		return builder.toString();
	}
}
