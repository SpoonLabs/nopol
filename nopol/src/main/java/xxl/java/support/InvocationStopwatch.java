package xxl.java.support;

import static java.lang.String.format;
import static xxl.java.library.LoggerLibrary.logInfo;
import static xxl.java.library.LoggerLibrary.loggerFor;

import java.lang.reflect.Method;

import org.slf4j.Logger;

import xxl.java.library.ClassLibrary;

public class InvocationStopwatch {

	public static Object invoke(Method method, Object receiver, Object... arguments) {
		long start = currentTime();
		Object result = ClassLibrary.invoke(method, receiver, arguments);
		long finish = currentTime();
		logInfo(logger(), format("[Invocation to '%s' on '%s': %d ms]", method.toString(), receiver.toString(), finish - start));
		return result;
	}
	
	private static long currentTime() {
		return System.currentTimeMillis();
	}
	
	protected static Logger logger() {
		return loggerFor(InvocationStopwatch.class);
	}
}
