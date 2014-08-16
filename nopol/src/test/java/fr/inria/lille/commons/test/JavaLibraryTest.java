package fr.inria.lille.commons.test;

import static org.junit.Assert.assertFalse;
import static xxl.java.extensions.library.JavaLibrary.folderPathSeparator;

import java.io.File;

import org.junit.Test;

import xxl.java.extensions.library.FileLibrary;

public class JavaLibraryTest {

	@Test
	public void absolutePath() {
		File workingDirectory = FileLibrary.openFrom(".");
		String folderSeparator = "" + folderPathSeparator();
		assertFalse(workingDirectory.getAbsolutePath().endsWith(folderSeparator));
	}
}
