package fr.inria.lille.commons.compiler;

import javax.tools.SimpleJavaFileObject;

import fr.inria.lille.commons.io.FileHandler;

public class VirtualSourceFileObject extends SimpleJavaFileObject {

	public VirtualSourceFileObject(String simpleClassName, String sourceContent) {
		super(FileHandler.uriFrom(simpleClassName + Kind.SOURCE.extension), Kind.SOURCE);
		this.sourceContent = sourceContent;
	}
	
	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors) {
		return sourceContent;
	}
	
	private String sourceContent;
}
