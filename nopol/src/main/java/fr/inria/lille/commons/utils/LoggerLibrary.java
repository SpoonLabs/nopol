package fr.inria.lille.commons.utils;

import static fr.inria.lille.commons.io.JavaLibrary.lineSeparator;
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
