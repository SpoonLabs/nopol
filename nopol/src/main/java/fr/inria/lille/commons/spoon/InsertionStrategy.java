package fr.inria.lille.commons.spoon;

import java.util.Collection;

import spoon.reflect.code.CtStatement;

public abstract class InsertionStrategy {

	public void insertNewStatements(CtStatement statement, Collection<? extends CtStatement> newStatements) {
		for (CtStatement newStatement : newStatements) {
			insertNewStatement(statement, newStatement);
		}
	}
	
	public abstract void insertNewStatement(CtStatement statement, CtStatement newStatement);
}
