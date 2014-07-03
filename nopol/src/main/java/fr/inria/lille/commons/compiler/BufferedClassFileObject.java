package fr.inria.lille.commons.compiler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.tools.SimpleJavaFileObject;

import fr.inria.lille.commons.io.FileHandler;

public class BufferedClassFileObject extends SimpleJavaFileObject {

	public BufferedClassFileObject(String qualifiedName, Kind kind) {
		super(FileHandler.uriFrom(qualifiedName), kind);
	}
	
	@Override
	public InputStream openInputStream() {
		return new ByteArrayInputStream(byteCodes());
	}

	@Override
	public OutputStream openOutputStream() {
      byteCodes = new ByteArrayOutputStream();
      return byteCodes;
	}
	
	public byte[] byteCodes() {
		return byteCodes.toByteArray();
	}
	
	private ByteArrayOutputStream byteCodes;
}
