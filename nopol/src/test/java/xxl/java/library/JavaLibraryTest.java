package xxl.java.library;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static xxl.java.library.JavaLibrary.asClasspath;
import static xxl.java.library.JavaLibrary.className;
import static xxl.java.library.JavaLibrary.folderPathSeparator;
import static xxl.java.library.JavaLibrary.packageName;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

public class JavaLibraryTest {

	@Test
	public void absolutePath() {
		File workingDirectory = FileLibrary.openFrom(".");
		String folderSeparator = "" + folderPathSeparator();
		assertFalse(workingDirectory.getAbsolutePath().endsWith(folderSeparator));
	}
	
	@Test
	public void joinClasspaths() throws MalformedURLException {
		URL url = new URL("file:///imaginary/project/folder/src");
		URL url2 = new URL("file:///imaginary/dependency/lib.jar");
		String classpath = asClasspath(new URL[] {url, url2});
		Character classPathSeparator = File.pathSeparatorChar;
		assertEquals("/imaginary/project/folder/src" + classPathSeparator + "/imaginary/dependency/lib.jar", classpath);
	}
	
	@Test
	public void classNameFromQualifiedName() {
		assertEquals("", className(""));
		assertEquals("HelloWorld", className("HelloWorld"));
		assertEquals("HelloWorld", className("java.HelloWorld"));
		assertEquals("HelloWorld", className("java.api.HelloWorld"));
	}
	
	@Test
	public void packageNameFromQualifiedName() {
		assertEquals("", packageName(""));
		assertEquals("", packageName("HelloWorld"));
		assertEquals("java", packageName("java.HelloWorld"));
		assertEquals("java.api", packageName("java.api.HelloWorld"));
	}
	
}
