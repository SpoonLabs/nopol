package fr.inria.lille.commons.classes;

import static fr.inria.lille.commons.string.StringLibrary.javaNewline;
import static fr.inria.lille.commons.string.StringLibrary.join;
import static java.util.Arrays.asList;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LoggerLibrary {

	public static void logDebug(Logger logger, Collection<String> lines) {
		logDebug(logger, lines.toArray(new String[lines.size()]));
	}
	
	public static void logDebug(Logger logger, String... lines) {
		String message = join(asList(lines), javaNewline());
		logDebug(logger, message);
	}
	
	public static void logDebug(Logger logger, String message) {
		logger.debug(message);
	}
	
	public static Logger newLoggerFor(Class<?> aClass) {
		return LoggerFactory.getLogger(aClass);
	}
	
}
