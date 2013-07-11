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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Favio D. DeMarco
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		if (3 != args.length) {
			printUsage();
			return;
		}
		String pName = args[0];

		File sourceFolder = new File(args[1]);

		checkArgument(sourceFolder.exists(), "%s: does not exist.", sourceFolder);
		checkArgument(sourceFolder.isDirectory(), "%s: is not a directory.", sourceFolder);

		String[] paths = args[2].split(Character.toString(File.pathSeparatorChar));

		new Main(pName, sourceFolder, paths).run();
	}

	private static void printUsage() {
		System.out.println("java " + Main.class.getName() + " <package> <source folder> <classpath>");
	}

	private final String[] classpath;

	private final String mainPackage;

	private final File sourceFolder;

	/**
	 * 
	 */
	private Main(final String pName, final File sourceFolder, final String[] classpath) {
		this.mainPackage = checkNotNull(pName);
		this.sourceFolder = checkNotNull(sourceFolder);
		this.classpath = checkNotNull(classpath);
	}

	void run() {

		List<URL> urls = new ArrayList<>();
		for (String path : this.classpath) {
			try {
				urls.add(new File(path).toURI().toURL());
			} catch (MalformedURLException e) {
				printUsage();
				// TODO Auto-generated catch block
				throw new RuntimeException(e);
			}
		}
		System.out.println(new JEFix(this.mainPackage, this.sourceFolder, urls.toArray(new URL[urls.size()])).build());
	}
}
