/**
 * 
 */
package fr.inria.lille.nopol;

/**
 * @author fav
 * 
 */
final class SourceFileNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7534999244555577928L;

	/**
	 * @param message
	 */
	public SourceFileNotFoundException(final String message) {
		super(message);
	}
}
