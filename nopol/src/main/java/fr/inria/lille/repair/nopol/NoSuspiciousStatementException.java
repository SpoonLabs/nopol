package fr.inria.lille.repair.nopol;

/**
 * Created by bdanglot on 9/22/16.
 */
public class NoSuspiciousStatementException extends RuntimeException {

	private static final long serialVersionUID = -3418853838019683700L;

	public final String header;

	public NoSuspiciousStatementException(String message, String header) {
		super(message);
		this.header = header;
	}
}
