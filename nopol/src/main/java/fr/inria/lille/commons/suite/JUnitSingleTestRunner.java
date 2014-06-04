package fr.inria.lille.commons.suite;

import java.util.concurrent.Callable;

import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;

import com.google.common.collect.ComputationException;

public class JUnitSingleTestRunner implements Callable<Result> {

	public JUnitSingleTestRunner(TestCase testCase, RunListener listener) {
		this.testCase = testCase;
		this.listener = listener;
	}

	@Override
	public Result call() throws Exception {
		JUnitCore runner = new JUnitCore();
		runner.addListener(listener);
		Request request = Request.method(compiledTestClass(), testCaseName());
		return runner.run(request);
	}
	
	private Class<?> compiledTestClass() {
		Class<?> compiledClass; 
		try {
			compiledClass = Thread.currentThread().getContextClassLoader().loadClass(testClassName());
		} catch (ClassNotFoundException e) {
			throw new ComputationException(e);
		}
		return compiledClass;
	}
	
	public String testCaseName() {
		return testCase.testName();
	}
	
	public String testClassName() {
		return testCase.className();
	}

	private TestCase testCase;
	private RunListener listener;
}
