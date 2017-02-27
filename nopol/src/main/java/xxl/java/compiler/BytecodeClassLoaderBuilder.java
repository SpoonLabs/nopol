package xxl.java.compiler;

import xxl.java.container.classic.MetaMap;

import java.net.URL;
import java.util.Map;

public class BytecodeClassLoaderBuilder {

    public static BytecodeClassLoader loaderFor(String qualifiedName, String sourceContent) {
        Map<String, byte[]> bytecodes = bytecodes(qualifiedName, sourceContent);
        return loaderWith(bytecodes);
    }

    public static BytecodeClassLoader loaderFor(String qualifiedName, String sourceContent, URL[] classpath) {
        Map<String, byte[]> bytecodes = bytecodes(qualifiedName, sourceContent);
        return loaderWith(bytecodes, classpath);
    }

    public static BytecodeClassLoader loaderFor(String qualifiedName, String sourceContent, ClassLoader parentClassLoader) {
        Map<String, byte[]> bytecodes = bytecodes(qualifiedName, sourceContent);
        return loaderWith(bytecodes, parentClassLoader);
    }

    public static BytecodeClassLoader loaderFor(Map<String, String> qualifiedNameAndContent) {
        Map<String, byte[]> bytecodes = bytecodes(qualifiedNameAndContent);
        return loaderWith(bytecodes);
    }

    public static BytecodeClassLoader loaderFor(Map<String, String> qualifiedNameAndContent, URL[] classpath) {
        Map<String, byte[]> bytecodes = bytecodes(qualifiedNameAndContent);
        return loaderWith(bytecodes, classpath);
    }

    public static BytecodeClassLoader loaderFor(Map<String, String> qualifiedNameAndContent, ClassLoader parentClassLoader) {
        Map<String, byte[]> bytecodes = bytecodes(qualifiedNameAndContent);
        return loaderWith(bytecodes, parentClassLoader);
    }

    public static BytecodeClassLoader loaderWith(String qualifiedName, byte[] bytecodes) {
        return loaderWith(MetaMap.newHashMap(qualifiedName, bytecodes));
    }

    public static BytecodeClassLoader loaderWith(String qualifiedName, byte[] bytecodes, URL[] classpath) {
        return loaderWith(MetaMap.newHashMap(qualifiedName, bytecodes), classpath);
    }

    public static BytecodeClassLoader loaderWith(String qualifiedName, byte[] bytecodes, ClassLoader parentClassLoader) {
        return loaderWith(MetaMap.newHashMap(qualifiedName, bytecodes), parentClassLoader);
    }

    public static BytecodeClassLoader loaderWith(String qualifiedName, byte[] bytecodes, URL[] classpath, ClassLoader parentClassLoader) {
        return loaderWith(MetaMap.newHashMap(qualifiedName, bytecodes), classpath, parentClassLoader);
    }

    public static BytecodeClassLoader loaderWith(Map<String, byte[]> bytecodes) {
        return loaderWith(bytecodes, new URL[]{});
    }

    public static BytecodeClassLoader loaderWith(Map<String, byte[]> bytecodes, URL[] classpath) {
        BytecodeClassLoader newLoader = new BytecodeClassLoader(classpath);
        newLoader.setBytecodes(bytecodes);
        return newLoader;
    }

    public static BytecodeClassLoader loaderWith(Map<String, byte[]> bytecodes, ClassLoader parentClassLoader) {
        return loaderWith(bytecodes, new URL[]{}, parentClassLoader);
    }

    public static BytecodeClassLoader loaderWith(Map<String, byte[]> bytecodes, URL[] classpath, ClassLoader parentClassLoader) {
        BytecodeClassLoader newLoader = new BytecodeClassLoader(classpath, parentClassLoader);
        newLoader.setBytecodes(bytecodes);
        return newLoader;
    }

    private static Map<String, byte[]> bytecodes(String qualifiedName, String sourceContent) {
        Map<String, String> sources = MetaMap.newHashMap(qualifiedName, sourceContent);
        return bytecodes(sources);
    }

    private static Map<String, byte[]> bytecodes(Map<String, String> qualifiedNameAndContent) {
        return new DynamicClassCompiler().javaBytecodeFor(qualifiedNameAndContent);
    }
}
