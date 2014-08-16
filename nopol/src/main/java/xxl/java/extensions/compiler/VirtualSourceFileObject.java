package xxl.java.extensions.compiler;

import javax.tools.SimpleJavaFileObject;

import xxl.java.extensions.library.FileLibrary;

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
