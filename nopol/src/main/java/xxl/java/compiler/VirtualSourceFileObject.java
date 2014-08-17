package xxl.java.compiler;

import javax.tools.SimpleJavaFileObject;

import xxl.java.library.FileLibrary;

public class VirtualSourceFileObject extends SimpleJavaFileObject {

	public VirtualSourceFileObject(String simpleClassName, String sourceContent) {
		super(FileLibrary.uriFrom(simpleClassName + Kind.SOURCE.extension), Kind.SOURCE);
		this.sourceContent = sourceContent;
	}
	
	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors) {
		return sourceContent;
	}
	
	private String sourceContent;
}
