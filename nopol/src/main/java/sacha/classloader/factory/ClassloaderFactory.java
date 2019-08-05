package sacha.classloader.factory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sacha.classloader.enrich.EnrichableClassloader;

public class ClassloaderFactory {

	public static EnrichableClassloader getEnrichableClassloader(){
		String classPath = System.getProperty("java.class.path");
		List<URL> urls = new ArrayList<>();
		for (String classpathElement : splitClassPath(classPath)) {
			try {
				urls.add(new URL("file://"+classpathElement));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return new EnrichableClassloader(urls.toArray(new URL[0]));
	}

	private static List<String> splitClassPath(String classPath) {
		final String separator = System.getProperty("path.separator");
		return Arrays.asList(classPath.split(separator));
	}
}
