package fr.inria.lille.commons.compiler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.tools.SimpleJavaFileObject;

import fr.inria.lille.commons.utils.library.FileLibrary;

public class VirtualClassFileObject extends SimpleJavaFileObject {

	public VirtualClassFileObject(String qualifiedName, Kind kind) {
		super(FileLibrary.uriFrom(qualifiedName), kind);
	}
	
	public VirtualClassFileObject(String qualifiedName, Kind kind, byte[] bytes) {
		this(qualifiedName, kind);
		setBytecodes(bytes);
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
	
	private void setBytecodes(byte[] bytes) {
		try {
			openOutputStream().write(bytes);
		} catch (IOException ioe) {
			throw new RuntimeException("IOException during VirtualClassFileObject creation");
		}
	}
	
	public byte[] byteCodes() {
		return byteCodes.toByteArray();
	}
	
	private ByteArrayOutputStream byteCodes;
}
