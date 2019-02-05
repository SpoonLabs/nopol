package xxl.java.compiler;

import xxl.java.library.JavaLibrary;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

public class JarPackage {

    public JarPackage() {
        classes = new HashMap<String, byte[]>();
        classesByPackage = new HashMap<String, Collection<String>>();
    }

    public JarPackage(String qualifiedName, byte[] compilation) {
        this();
        add(qualifiedName, compilation);
    }

    public JarPackage(Map<String, byte[]> qualifiedNamesAndCompilation) {
        this();
        addAll(qualifiedNamesAndCompilation);
    }

    public void addAll(Map<String, byte[]> qualifiedNamesAndCompilation) {
        for (Entry<String, byte[]> qualifiedNameAndCompilation : qualifiedNamesAndCompilation.entrySet()) {
            add(qualifiedNameAndCompilation.getKey(), qualifiedNameAndCompilation.getValue());
        }
    }

    public void add(String qualifiedName, byte[] compilation) {
        String packageName = packageName(qualifiedName);
        if (!classesByPackage().containsKey(packageName)) {
            classesByPackage().put(packageName, new HashSet<String>());
        }
        classesByPackage().get(packageName).add(qualifiedName);
        classes().put(qualifiedName, compilation);
    }

    public File saveTo(File destinationFolder, String packageName) {
        try {
            String jarFilePath = jarFilePath(destinationFolder, packageName);
            FileOutputStream jarFileOutputStream = new FileOutputStream(jarFilePath);
            JarOutputStream jarStream = new JarOutputStream(jarFileOutputStream);
            fillStream(jarStream);
            jarStream.close();
            jarFileOutputStream.close();
            return new File(jarFilePath);
        } catch (Exception e) {
            throw new RuntimeException("Could not create JAR file in " + destinationFolder);
        }
    }

    private String jarFilePath(File destinationFolder, String packageName) {
        if (!packageName.toLowerCase().endsWith(".jar")) {
            packageName += ".jar";
        }
        return destinationFolder.getAbsolutePath() + separator() + packageName;
    }

    private void fillStream(JarOutputStream jarStream) throws Exception {
        for (String packageName : classesByPackage().keySet()) {
            jarStream.putNextEntry(new ZipEntry(toFolderPath(packageName)));
            jarStream.closeEntry();
            for (String qualifiedName : classesByPackage().get(packageName)) {
                jarStream.putNextEntry(new ZipEntry(toClassPath(qualifiedName)));
                jarStream.write(classes().get(qualifiedName));
                jarStream.closeEntry();
            }
        }
    }

    protected String className(String qualifiedClassName) {
        return JavaLibrary.simpleClassName(qualifiedClassName);
    }

    protected String packageName(String qualifiedClassName) {
        return JavaLibrary.packageName(qualifiedClassName);
    }

    protected String toFolderPath(String path) {
        return path.replace('.', separator()) + separator();
    }

    protected String toClassPath(String qualifiedName) {
        return toFolderPath(packageName(qualifiedName)) + className(qualifiedName) + ".class";
    }

    private Character separator() {
        return File.separatorChar;
    }

    private Map<String, byte[]> classes() {
        return classes;
    }

    private Map<String, Collection<String>> classesByPackage() {
        return classesByPackage;
    }

    private Map<String, byte[]> classes;
    private Map<String, Collection<String>> classesByPackage;
}
