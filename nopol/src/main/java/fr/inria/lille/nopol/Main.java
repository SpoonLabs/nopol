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
package fr.inria.lille.nopol;

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
		if (2 != args.length) {
			printUsage();
			return;
		}
		File sourceFolder = new File(args[0]);

		checkArgument(sourceFolder.exists(), "%s: does not exist.", sourceFolder);
		checkArgument(sourceFolder.isDirectory(), "%s: is not a directory.", sourceFolder);

		// XXX FIXME TODO this line adds the analyzed project classpath for the compiler, it should use another thread
		// with a URLClassLoader, for example.
		// see JDTCompiler.getLibraryAccess()...
		System.setProperty("java.class.path", System.getProperty("java.class.path") + File.pathSeparatorChar + args[1]);

		String[] paths = args[1].split(Character.toString(File.pathSeparatorChar));

		new Main(sourceFolder, paths).run();
	}

	private static void printUsage() {
		System.out.println("java " + Main.class.getName() + " <source folder> <classpath>");
	}

	private final String[] classpath;

	private final File sourceFolder;

	/**
	 * 
	 */
	private Main(final File sourceFolder, final String[] classpath) {
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
		System.out.println("Suggested patch: "
				+ new NoPol(this.sourceFolder, urls.toArray(new URL[urls.size()])).build());
	}
}
