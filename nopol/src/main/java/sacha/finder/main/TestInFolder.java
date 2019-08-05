package sacha.finder.main;

import sacha.finder.classes.impl.SourceFolderFinder;
import sacha.finder.filters.impl.TestFilter;
import sacha.finder.processor.Processor;

public class TestInFolder{
	
	private String testFolder = null;

	public TestInFolder(String testFolder) {
		this.testFolder=testFolder;
	}

	public Class<?>[] find(){
		return new Processor(new SourceFolderFinder(testFolder), new TestFilter()).process();
	}

}
