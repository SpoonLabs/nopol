package fr.inria.lille.repair.nopol;

/**
 * Created by bdanglot on 9/22/16.
 */
public class NoFailingTestCaseException extends RuntimeException {

	private static final long serialVersionUID = 4880652092960903042L;

	public final String header;

	public NoFailingTestCaseException(String message, String header) {
		super(message);
		this.header = header;
	}
}
