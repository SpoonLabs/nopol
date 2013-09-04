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

import java.io.File;

import javax.annotation.Nullable;

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

	private void checkState(final boolean expression, @Nullable final String errorMessageTemplate,
			@Nullable final Object... errorMessageArgs) {
		if (!expression) {
			throw new SourceFileNotFoundException(String.format(errorMessageTemplate, errorMessageArgs));
		}
	}

	/**
	 * @return the containingClassName
	 */
	public String getContainingClassName() {
		return containingClassName;
	}

	/**
	 * @return the lineNumber
	 */
	public int getLineNumber() {
		return lineNumber;
	}

	public File getSourceFile(final File sourceFolder) {
		return this.getSourceFile(sourceFolder.getAbsolutePath());
	}

	public File getSourceFile(final String sourceFolder) {
		String classPath = containingClassName.replace('.', File.separatorChar);
		int inertTypeIndex = classPath.indexOf('$');
		if (inertTypeIndex > 0) {
			classPath = classPath.substring(0, inertTypeIndex);
		}
		File sourceFile = new File(sourceFolder, classPath + ".java");
		checkState(sourceFile.exists(), "%s: does not exist.", sourceFile);
		return sourceFile;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("SourceLocation %s:%d", containingClassName, lineNumber);
	}
}
