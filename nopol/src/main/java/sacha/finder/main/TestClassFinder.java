package sacha.finder.main;

import sacha.classloader.enrich.EnrichableClassloader;
import sacha.finder.classes.impl.ProjectFinder;
import sacha.finder.filters.impl.TestFilter;
import sacha.finder.processor.Processor;

public class TestClassFinder{
	
	private EnrichableClassloader urlClassloader;

	public TestClassFinder(EnrichableClassloader classloader) {
		this.urlClassloader=classloader;
	}

	public Class<?>[] findTestClasses(){
		return new Processor(new ProjectFinder(urlClassloader), new TestFilter()).process();
	}

}
