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
package fr.inria.lille.jefix;

import static com.google.common.base.Preconditions.checkState;

import java.io.File;

/**
 * @author Favio D. DeMarco
 * 
 */
public final class SourceLocation {

	private final String containingClassName;

	private final int lineNumber;

	/**
	 * @param containingClassName
	 * @param lineNumber
	 */
	public SourceLocation(final String containingClassName, final int lineNumber) {
		this.containingClassName = containingClassName;
		this.lineNumber = lineNumber;
	}

	/**
	 * @return the containingClassName
	 */
	public String getContainingClassName() {
		return this.containingClassName;
	}

	/**
	 * @return the lineNumber
	 */
	public int getLineNumber() {
		return this.lineNumber;
	}

	public File getSourceFile(final File sourceFolder) {
		return this.getSourceFile(sourceFolder.getAbsolutePath());
	}

	public File getSourceFile(final String sourceFolder) {
		String classPath = this.containingClassName.replace('.', File.separatorChar);
		File sourceFile = new File(sourceFolder, classPath + ".java");
		checkState(sourceFile.exists(), "%s: does not exist.", sourceFile);
		return sourceFile;
	}
}
