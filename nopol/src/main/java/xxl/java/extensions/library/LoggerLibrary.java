package xxl.java.extensions.library;

import static java.util.Arrays.asList;
import static xxl.java.extensions.library.JavaLibrary.lineSeparator;
import static xxl.java.extensions.library.StringLibrary.join;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LoggerLibrary {

	public static void logCollection(Logger logger, Collection<? extends Object> elements) {
		logCollection(logger, "", elements);
	}
	
	public static void logCollection(Logger logger, String title, Collection<? extends Object> elements) {
		List<String> stringList = StringLibrary.toStringList(elements);
		if (title.length() > 0) {
			stringList.add(0, title);
		}
		logDebug(logger, stringList);
	}
	
	public static void logDebug(Logger logger, Collection<String> lines) {
		logDebug(logger, lines.toArray(new String[lines.size()]));
	}
	
	public static void logDebug(Logger logger, String... lines) {
		String message = join(asList(lines), lineSeparator());
		logDebug(logger, message);
	}
	
	public static void logDebug(Logger logger, String message) {
		logger.debug(message);
	}
	
	public static void logWarning(Logger logger, Collection<String> lines) {
		logWarning(logger, lines.toArray(new String[lines.size()]));
	}
	
	public static void logWarning(Logger logger, String... lines) {
		String message = join(asList(lines), lineSeparator());
		logWarning(logger, message);
	}
	
	public static void logWarning(Logger logger, String message) {
		logger.warn(message);
	}
	
	public static Logger newLoggerFor(Class<?> aClass) {
		return LoggerFactory.getLogger(aClass);
	}
	
}
