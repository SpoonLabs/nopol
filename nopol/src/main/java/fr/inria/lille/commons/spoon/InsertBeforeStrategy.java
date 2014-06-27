package fr.inria.lille.commons.spoon;

import spoon.reflect.code.CtStatement;

public class InsertBeforeStrategy extends InsertionStrategy {

	@Override
	public void insertNewStatement(CtStatement statement, CtStatement newStatement) {
		statement.insertBefore(newStatement);
	}
}
