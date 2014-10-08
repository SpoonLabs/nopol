package xxl.java.library;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static xxl.java.library.JavaLibrary.asClasspath;
import static xxl.java.library.JavaLibrary.asFilePath;
import static xxl.java.library.JavaLibrary.classFromClasspath;
import static xxl.java.library.JavaLibrary.folderPathSeparator;
import static xxl.java.library.JavaLibrary.packageName;
import static xxl.java.library.JavaLibrary.simpleClassName;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

public class JavaLibraryTest {

	public static class Inner {
		public static class NestedInner {
			
		}
	}
	
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
	public void filePathFromClasspath() throws MalformedURLException {
		URL url = new URL("file:///imaginary/project/folder/src");
		URL url2 = new URL("file:///imaginary/dependency/lib.jar");
		URL[] classpath = new URL[] {url, url2};
		String[] filePath = asFilePath(classpath);
		assertTrue(2 == filePath.length);
		assertEquals("/imaginary/project/folder/src", filePath[0]);
		assertEquals("/imaginary/dependency/lib.jar", filePath[1]);
	}
	
	@Test
	public void classNameFromQualifiedName() {
		assertEquals("", simpleClassName(""));
		assertEquals("HelloWorld", simpleClassName("HelloWorld"));
		assertEquals("HelloWorld", simpleClassName("java.HelloWorld"));
		assertEquals("HelloWorld", simpleClassName("java.api.HelloWorld"));
		assertEquals("Greeting", simpleClassName("HelloWorld$Greeting"));
		assertEquals("Greeting", simpleClassName("java.api.HelloWorld$Greeting"));
		assertEquals("Inner", simpleClassName(Inner.class.getName()));
		assertEquals("Inner", simpleClassName(Inner.class.getSimpleName()));
		assertEquals("Inner", simpleClassName(Inner.class.getCanonicalName()));
		assertEquals("NestedInner", simpleClassName(Inner.NestedInner.class.getName()));
		assertEquals("NestedInner", simpleClassName(Inner.NestedInner.class.getSimpleName()));
		assertEquals("NestedInner", simpleClassName(Inner.NestedInner.class.getCanonicalName()));
	}
	
	@Test
	public void packageNameFromQualifiedName() {
		assertEquals("", packageName(""));
		assertEquals("", packageName("HelloWorld"));
		assertEquals("", packageName("HelloWorld$Greeting"));
		assertEquals("java", packageName("java.HelloWorld"));
		assertEquals("java.api", packageName("java.api.HelloWorld"));
		assertEquals("java.api", packageName("java.api.HelloWorld$Greeting"));
		assertEquals("", packageName(Inner.class.getSimpleName()));
		assertEquals(getClass().getPackage().toString(), "package " + packageName(Inner.class.getName()));
		assertEquals("", packageName(Inner.NestedInner.class.getSimpleName()));
		assertEquals(getClass().getPackage().toString(), "package " + packageName(Inner.NestedInner.class.getName()));
	}
	
	@Test
	public void loadingClassFromClasspath() throws Exception {
		String qualifiedName = "xxl.java.library.HelloWorld";
		URL classpath = FileLibrary.resource("/helloWorld/HelloWorld.jar");
		Class<?> helloWorldClass = classFromClasspath(classpath, qualifiedName);
		assertTrue(helloWorldClass != null);
		Method greet = helloWorldClass.getMethod("greet");
		assertEquals("Hello, world!", greet.invoke(helloWorldClass.newInstance()));
	}
}
