package fr.inria.lille.localization;

import fr.inria.lille.repair.nopol.SourceLocation;

/**
 * Created by bdanglot on 10/3/16.
 */
public class StatementSourceLocation extends AbstractStatement {

	private final SourceLocation location;

	public StatementSourceLocation(SourceLocation location) {
		this.location = location;
	}

	public SourceLocation getLocation() {
		return location;
	}
}
