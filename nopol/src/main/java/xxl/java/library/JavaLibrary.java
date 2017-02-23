package xxl.java.library;

import xxl.java.container.classic.MetaList;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.asList;

public class JavaLibrary {

    public static String asClasspath(URL[] urls) {
        Collection<String> paths = new LinkedList<String>();
        for (URL url : urls) {
            paths.add(url.getPath());
        }
        return StringLibrary.join(paths, classpathSeparator());
    }

    public static String[] asFilePath(URL[] urls) {
        int length = urls.length;
        String[] filePath = new String[length];
        for (int i = 0; i < length; i += 1) {
            filePath[i] = urls[i].getFile();
        }
        return filePath;
    }

    public static String simpleClassName(String qualifiedClassName) {
        String simpleClassName = qualifiedClassName;
        int lastPackageSeparator = qualifiedClassName.lastIndexOf('.');
        if (lastPackageSeparator > -1) {
            simpleClassName = qualifiedClassName.substring(lastPackageSeparator + 1);
        }
        int lastNestingSeparator = simpleClassName.lastIndexOf('$');
        if (lastNestingSeparator > -1) {
            simpleClassName = simpleClassName.substring(lastNestingSeparator + 1);
        }
        return simpleClassName;
    }

    public static String packageName(String qualifiedClassName) {
        int lastPackageSeparator = qualifiedClassName.lastIndexOf('.');
        if (lastPackageSeparator > -1) {
            return qualifiedClassName.substring(0, lastPackageSeparator);
        }
        return "";
    }

    public static String systemClasspath() {
        return property("java.class.path");
    }

    public static URL[] systemClasspathURLs() {
        return classpathFrom(systemClasspath());
    }

    public static void extendSystemClasspathWith(URL[] classpaths) {
        StringBuilder newClasspath = new StringBuilder(systemClasspath());
        for (URL classpath : classpaths) {
            newClasspath.append(classpathSeparator() + classpath.getPath());
        }
        setClasspath(newClasspath.toString());
    }

    public static URL[] extendClasspathWith(String classpath, URL[] destination) {
        List<URL> extended = MetaList.newLinkedList(destination);
        extended.addAll(asList(classpathFrom(classpath)));
        return extended.toArray(new URL[extended.size()]);
    }

    public static URL[] classpathFrom(String classpath) {
        List<String> folderNames = StringLibrary.split(classpath, classpathSeparator());
        URL[] folders = new URL[folderNames.size()];
        int index = 0;
        for (String folderName : folderNames) {
            folders[index] = FileLibrary.urlFrom(folderName);
            index += 1;
        }
        return folders;
    }

    public static void setClasspath(String newClasspath) {
        setProperty("java.class.path", newClasspath);
    }

    public static void extendSystemClassLoaderClasspathWith(URL[] classpaths) {
        extendClassLoaderClasspathWith((URLClassLoader) ClassLoader.getSystemClassLoader(), classpaths);
    }

    public static void extendClassLoaderClasspathWith(URLClassLoader classLoader, URL[] classpaths) {
        Method addURL = ClassLibrary.method("addURL", URLClassLoader.class, URL.class);
        if (addURL != null) {
            try {
                for (URL classpath : classpaths) {
                    ClassLibrary.invokeTrespassing(addURL, classLoader, classpath);
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to extend classpath on class loader with " + classpaths);
            }
        }
    }

    public static Class<?> classFromClasspath(URL classpath, String qualifiedName) {
        Collection<Class<?>> classes = classesFromClasspath(new URL[]{classpath}, asList(qualifiedName));
        if (classes.isEmpty()) {
            return null;
        }
        return classes.iterator().next();
    }

    public static Collection<Class<?>> classesFromClasspath(URL[] classpath, Collection<String> qualifiedNames) {
        URLClassLoader loader = new URLClassLoader(classpath);
        Collection<Class<?>> classes = loadedClassesFrom(loader, qualifiedNames);
        close(loader);
        return classes;
    }

    public static Collection<Class<?>> loadedClassesFrom(ClassLoader classLoader, Collection<String> qualifiedNames) {
        Collection<Class<?>> classes = MetaList.newArrayList(qualifiedNames.size());
        try {
            for (String qualifiedName : qualifiedNames) {
                classes.add(classLoader.loadClass(qualifiedName));
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(format("Could not load classes %s", qualifiedNames.toString()), e);
        }
        return classes;
    }

    public static void close(URLClassLoader loader) {
        try {
            loader.close();
        } catch (IOException ioe) {
            throw new RuntimeException("Could not close URLClassLoader properly");
        }
    }

    public static Character classpathSeparator() {
        if (javaPathSeparator == null) {
            javaPathSeparator = File.pathSeparatorChar;
        }
        return javaPathSeparator;
    }

    public static Character folderPathSeparator() {
        if (javaFolderSeparator == null) {
            javaFolderSeparator = File.separatorChar;
        }
        return javaFolderSeparator;
    }

    public static String lineSeparator() {
        if (javaLineSeparator == null) {
            javaLineSeparator = property("line.separator");
        }
        return javaLineSeparator;
    }

    private static String property(String key) {
        return System.getProperty(key);
    }

    private static void setProperty(String key, String value) {
        System.setProperty(key, value);
    }

    private static String javaLineSeparator;
    private static Character javaPathSeparator;
    private static Character javaFolderSeparator;
}
