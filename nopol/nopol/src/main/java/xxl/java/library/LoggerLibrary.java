package xxl.java.library;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxl.java.container.classic.MetaMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static xxl.java.library.JavaLibrary.lineSeparator;
import static xxl.java.library.StringLibrary.join;
import static xxl.java.library.StringLibrary.toStringList;


public class LoggerLibrary {

    public static void logDebug(Object object, Collection<String> lines) {
        logDebug(loggerFor(object), lines);
    }

    public static void logInfo(Object object, Collection<String> lines) {
        logInfo(loggerFor(object), lines);
    }

    public static void logWarning(Object object, Collection<String> lines) {
        logWarning(loggerFor(object), lines);
    }

    public static void logError(Object object, Collection<String> lines) {
        logError(loggerFor(object), lines);
    }

    public static void logDebug(Object object, String... lines) {
        logDebug(loggerFor(object), lines);
    }

    public static void logInfo(Object object, String... lines) {
        logInfo(loggerFor(object), lines);
    }

    public static void logWarning(Object object, String... lines) {
        logWarning(loggerFor(object), lines);
    }

    public static void logError(Object object, String... lines) {
        logError(loggerFor(object), lines);
    }

    public static void logDebug(Object object, String message) {
        logDebug(loggerFor(object), message);
    }

    public static void logInfo(Object object, String message) {
        logInfo(loggerFor(object), message);
    }

    public static void logWarning(Object object, String message) {
        logWarning(loggerFor(object), message);
    }

    public static void logError(Object object, String message) {
        logError(loggerFor(object), message);
    }

    public static void logDebug(Logger logger, String... lines) {
        logDebug(logger, asList(lines));
    }

    public static void logInfo(Logger logger, String... lines) {
        logInfo(logger, asList(lines));
    }

    public static void logWarning(Logger logger, String... lines) {
        logWarning(logger, asList(lines));
    }

    public static void logError(Logger logger, String... lines) {
        logError(logger, asList(lines));
    }

    public static void logDebug(Logger logger, Collection<String> lines) {
        logDebug(logger, join(lines, lineSeparator()));
    }

    public static void logInfo(Logger logger, Collection<String> lines) {
        logInfo(logger, join(lines, lineSeparator()));
    }

    public static void logWarning(Logger logger, Collection<String> lines) {
        logWarning(logger, join(lines, lineSeparator()));
    }

    public static void logError(Logger logger, Collection<String> lines) {
        logError(logger, join(lines, lineSeparator()));
    }

    public synchronized static void logDebugLn(Logger logger, String message) {
        logDebug(logger, message + lineSeparator());
    }

    public synchronized static void logInfoLn(Logger logger, String message) {
        logInfo(logger, message + lineSeparator());
    }

    public synchronized static void logWarningLn(Logger logger, String message) {
        logWarning(logger, message + lineSeparator());
    }

    public synchronized static void logErrorLn(Logger logger, String message) {
        logError(logger, message + lineSeparator());
    }

    public synchronized static void logDebug(Logger logger, String message) {
        logger.debug(message);
    }

    public synchronized static void logInfo(Logger logger, String message) {
        logger.info(message);
    }

    public synchronized static void logWarning(Logger logger, String message) {
        logger.warn(message);
    }

    public synchronized static void logError(Logger logger, String message) {
        logger.error(message);
    }

    public static void logCollection(Object object, Collection<? extends Object> elements) {
        logCollection(loggerFor(object), elements);
    }

    public static void logCollection(Logger logger, Collection<? extends Object> elements) {
        logCollection(logger, "", elements);
    }

    public static void logCollection(Object object, String title, Collection<? extends Object> elements) {
        logCollection(loggerFor(object), title, elements);
    }

    public static void logCollection(Logger logger, String title, Collection<? extends Object> elements) {
        List<String> stringList = toStringList(elements);
        if (title.length() > 0) {
            stringList.add(0, title);
        }
        logDebug(logger, stringList);
    }

    public synchronized static Logger loggerFor(Object object) {
        return loggerFor(object.getClass());
    }

    public synchronized static Logger loggerFor(Class<?> aClass) {
        if (!loggers().containsKey(aClass)) {
            loggers().put(aClass, newLoggerFor(aClass));
        }
        return loggers().get(aClass);
    }

    private synchronized static Map<Class<?>, Logger> loggers() {
        if (loggers == null) {
            loggers = MetaMap.newHashMap();
        }
        return loggers;
    }

    private static Logger newLoggerFor(Class<?> aClass) {
        return LoggerFactory.getLogger(aClass);
    }

    private static Map<Class<?>, Logger> loggers;
}
