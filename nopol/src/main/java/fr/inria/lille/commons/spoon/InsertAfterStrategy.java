package fr.inria.lille.commons.spoon;

import spoon.reflect.code.CtStatement;

public class InsertAfterStrategy extends InsertionStrategy {

	@Override
	public void insertNewStatement(CtStatement statement, CtStatement newStatement) {
		statement.insertAfter(newStatement);
	}

}
