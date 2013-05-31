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
package fr.inria.lille.jsemfix.conditional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;

/**
 * @author Favio D. DeMarco
 * 
 */
public final class Conditionals {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		if (2 != args.length) {
			printUsage();
			return;
		}
		String pName = args[0];

		File sourceFolder = new File(args[1]);

		checkArgument(sourceFolder.exists(), "%s: does not exist.", sourceFolder);
		checkArgument(sourceFolder.isDirectory(), "%s: is not a directory.", sourceFolder);

		new Conditionals(pName, sourceFolder).run();
	}

	private static void printUsage() {
		System.out.println("java " + Conditionals.class.getName() + " <package> <source folder>");
	}

	private final String mainPackage;

	private final File sourceFolder;

	/**
	 * 
	 */
	private Conditionals(final String pName, final File sourceFolder) {
		this.mainPackage = checkNotNull(pName);
		this.sourceFolder = checkNotNull(sourceFolder);
	}

	void run() {
		System.out.println(new ConditionalsMatrix(this.mainPackage, this.sourceFolder).build());
	}
}
