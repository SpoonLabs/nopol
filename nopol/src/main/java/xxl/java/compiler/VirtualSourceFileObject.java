package xxl.java.compiler;

import xxl.java.library.FileLibrary;

import javax.tools.SimpleJavaFileObject;

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
