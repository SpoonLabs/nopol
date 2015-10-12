package xxl.java.library;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

public class FileLibrary {

    public static String currentAbsolutePath() {
        return openFrom(".").getAbsolutePath();
    }

    public static boolean ensurePathIsValid(String path) {
        return openFrom(path).exists();
    }

    public static boolean isValidPath(String path) {
        return new File(path).exists();
    }

    public static File openFrom(String path) {
        File file = new File(path);
        if (!file.exists()) {
            fail("File does not exist in: '" + path + "'");
        }
        return file;
    }

    public static boolean isSameFile(File aFile, File otherFile) {
        try {
            return aFile.getCanonicalPath().equals(otherFile.getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static File fileFrom(String path) {
        File file = openFrom(path);
        if (!file.isFile()) {
            fail("This is not a file: '" + path + "'");
        }
        return file;
    }

    public static File directoryFrom(String path) {
        File file = openFrom(path);
        if (!file.isDirectory()) {
            fail("This is not a directory: '" + path + "'");
        }
        return file;
    }

    public static Collection<File> filesMatchingNameIn(String directoryPath, String regexToMatch) {
        File directory = directoryFrom(directoryPath);
        Collection<File> matchingFiles = new LinkedList<File>();
        for (File file : directory.listFiles()) {
            if (file.getName().matches(regexToMatch)) {
                matchingFiles.add(file);
            }
        }
        return matchingFiles;
    }

    public static void deleteFiles(Collection<File> files) {
        for (File file : files) {
            deleteFile(file);
        }
    }

    public static void deleteFile(File file) {
        deleteFile(file.getAbsolutePath());
    }

    public static void deleteFile(String path) {
        File file = openFrom(path);
        if (file.isFile()) {
            log(String.format("Warning: deleting file '%s'", file.getAbsolutePath()));
            file.delete();
        } else {
            deleteDirectory(path);
        }
    }

    public static void deleteDirectory(String path) {
        File directory = directoryFrom(path);
        log(String.format("Warning: deleting directory '%s'", directory.getAbsolutePath()));
        deleteFiles(Arrays.asList(directory.listFiles()));
        directory.delete();
    }

    public static URL urlFrom(String path) {
        URL url = null;
        try {
            url = openFrom(path).toURI().toURL();
        } catch (MalformedURLException e) {
            fail("Illegal name for '" + path + "' while converting to URL");
        }
        return url;
    }

    public static URI uriFrom(String scheme) {
        return URI.create(scheme);
    }

    public static URL resource(String path) {
        /* How method "AnyClass.getResource(filePath)" works:
		 * Suppose the fully qualified name of class "AnyClass" is "any.package.AnyClass".
		 * 	- If "filePath" starts with '/', then the path is relative to the CLASSPATH. That is "any/../path"
		 * 	- Otherwise, the path is relative to the location of "AnyClass". That is "any/package/path"
		 */
        URL resource = FileLibrary.class.getResource(path);
        if (resource == null) {
            fail("Unable to find resource in: '" + path + "'");
        }
        return resource;
    }

    private static void log(String message) {
        System.err.println(message);
    }

    private static void fail(String message) {
        throw new IllegalArgumentException(message);
    }
}
