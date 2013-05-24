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
package fr.inria.lille.jsemfix.patch;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;

/**
 * @author Favio D. DeMarco
 * 
 */
public final class SimplePatch implements Patch {

	private final File file;
	private final int lineNumber;
	private final String source;

	/**
	 * XXX FIXME TODO too many arguments
	 * 
	 * @param file
	 * @param source
	 * @param lineNumber
	 */
	public SimplePatch(final File file, final int lineNumber, final String source) {
		this.file = checkNotNull(file);
		checkArgument(file.exists(), "File %s doesn't exists.", file);
		checkArgument(file.isFile(), "%s is not a file.", file);
		this.source = checkNotNull(source);
		checkArgument(lineNumber > 0, "Line number (%s) should be greater than zero.", lineNumber);
		this.lineNumber = lineNumber;
	}

	/**
	 * @see fr.inria.lille.jsemfix.patch.Patch#asString()
	 */
	@Override
	public String asString() {
		return this.source;
	}

	@Override
	public File getFile() {
		return this.file;
	}

	/**
	 * @see fr.inria.lille.jsemfix.patch.Patch#getLineNumber()
	 */
	@Override
	public int getLineNumber() {
		return this.lineNumber;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("%s:%d: %s", this.file, this.lineNumber, this.source);
	}
}
