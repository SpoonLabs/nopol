package fr.inria.lille.localization;

/**
 * Created by bdanglot on 10/3/16.
 */
public class TestResult {

	private final String name;
	private final boolean successful;

	public TestResult(String name, boolean successful) {
		this.name = name;
		this.successful = successful;
	}
}
