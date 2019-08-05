package sacha.finder.main;

import sacha.finder.classes.impl.ClasspathFinder;
import sacha.finder.filters.impl.TestFilter;
import sacha.finder.processor.Processor;

public class TestInClasspath{

	public Class<?>[] find(){
		return new Processor(new ClasspathFinder(), new TestFilter()).process();
	}

}
