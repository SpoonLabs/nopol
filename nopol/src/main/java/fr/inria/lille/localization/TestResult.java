package fr.inria.lille.localization;

import xxl.java.junit.TestCase;

/**
 * Created by bdanglot on 10/3/16.
 */
public interface TestResult {

	TestCase getTestCase();

	boolean isSuccessful();

}
