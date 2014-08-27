package xxl.java.library;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static xxl.java.library.JavaLibrary.asClasspath;
import static xxl.java.library.JavaLibrary.simpleClassName;
import static xxl.java.library.JavaLibrary.classesFromClasspath;
import static xxl.java.library.JavaLibrary.folderPathSeparator;
import static xxl.java.library.JavaLibrary.packageName;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import org.junit.Test;

import xxl.java.container.classic.MetaCollection;

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
		assertEquals("", simpleClassName(""));
		assertEquals("HelloWorld", simpleClassName("HelloWorld"));
		assertEquals("HelloWorld", simpleClassName("java.HelloWorld"));
		assertEquals("HelloWorld", simpleClassName("java.api.HelloWorld"));
	}
	
	@Test
	public void packageNameFromQualifiedName() {
		assertEquals("", packageName(""));
		assertEquals("", packageName("HelloWorld"));
		assertEquals("java", packageName("java.HelloWorld"));
		assertEquals("java.api", packageName("java.api.HelloWorld"));
	}
	
	@Test
	public void loadingClassFromClasspath() throws Exception {
		String qualifiedName = "xxl.java.library.HelloWorld";
		URL classpath = FileLibrary.resource("/helloWorld/HelloWorld.jar");
		Collection<Class<?>> classes = classesFromClasspath(new URL[] { classpath }, asList(qualifiedName));
		assertEquals(1, classes.size());
		Class<?> helloWorldClass = MetaCollection.any(classes);
		Method greet = helloWorldClass.getMethod("greet");
		assertEquals("Hello, world!", greet.invoke(helloWorldClass.newInstance()));
	}
}
