package sacha.finder.main;

import java.net.URLClassLoader;

import sacha.finder.classes.impl.ClassloaderFinder;
import sacha.finder.filters.impl.TestFilter;
import sacha.finder.processor.Processor;

public class TestInURLClassloader{
	
	private URLClassLoader urlClassloader;

	public TestInURLClassloader(URLClassLoader classloader) {
		this.urlClassloader=classloader;
	}

	public Class<?>[] find(){
		return new Processor(new ClassloaderFinder(urlClassloader), new TestFilter()).process();
	}

}
