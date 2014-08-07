package fr.inria.lille.commons.test;

import static fr.inria.lille.commons.io.JavaLibrary.folderPathSeparator;
import static org.junit.Assert.assertFalse;

import java.io.File;

import org.junit.Test;

import fr.inria.lille.commons.io.FileHandler;

public class JavaLibraryTest {

	@Test
	public void absolutePath() {
		File workingDirectory = FileHandler.openFrom(".");
		String folderSeparator = "" + folderPathSeparator();
		assertFalse(workingDirectory.getAbsolutePath().endsWith(folderSeparator));
	}
	
}
