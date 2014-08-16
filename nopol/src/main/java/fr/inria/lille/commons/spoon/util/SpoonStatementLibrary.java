package fr.inria.lille.commons.spoon.util;

import static fr.inria.lille.commons.spoon.util.SpoonElementLibrary.isBlock;
import static fr.inria.lille.commons.spoon.util.SpoonElementLibrary.isMethod;
import static fr.inria.lille.commons.spoon.util.SpoonElementLibrary.isStatement;
import static fr.inria.lille.commons.spoon.util.SpoonModelLibrary.newBlock;
import static fr.inria.lille.commons.spoon.util.SpoonModelLibrary.setParent;
import static xxl.java.extensions.library.ClassLibrary.castTo;
import static xxl.java.extensions.library.ClassLibrary.isInstanceOf;

import java.util.List;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;
import xxl.java.extensions.collection.ListLibrary;

public class SpoonStatementLibrary {

	public static CtBlock<?> asBlock(CtStatement statement) {
		if (isBlock(statement)) {
			return (CtBlock<?>) statement;
		}
		return newBlock(statement.getFactory(), statement);
	}
	
	public static void insertBeforeUnderSameParent(CtStatement toBeInserted, CtStatement insertionPoint) {
		insertBefore(toBeInserted, insertionPoint.getParent(), insertionPoint);
	}
	
	public static void insertBefore(CtStatement toBeInserted, CtElement newParent, CtStatement insertionPoint) {
		insertionPoint.insertBefore(toBeInserted);
		setParent(newParent, toBeInserted);
	}
	
	public static void insertAfterUnderSameParent(CtStatement toBeInserted, CtStatement insertionPoint) {
		insertAfter(toBeInserted, insertionPoint.getParent(), insertionPoint);
	}
	
	public static void insertAfter(CtStatement toBeInserted, CtElement newParent, CtStatement insertionPoint) {
		insertionPoint.insertAfter(toBeInserted);
		setParent(newParent, toBeInserted);
	}
	
	public static boolean isLastStatementOfMethod(CtStatement statement) {
		CtElement statementParent = statement.getParent();
		if (! isBlock(statementParent)) {
			return isLastStatementOfMethod((CtStatement) statementParent);
		}
		CtBlock<?> block = (CtBlock<?>) statementParent;
		if (isLastStatementOf(block, statement)) {
			CtElement blockParent = block.getParent();
			if (isStatement(blockParent)) {
				return isLastStatementOfMethod((CtStatement) blockParent);
			} else {
				return isMethod(blockParent);
			}
		}
		return false;
	}
	
	public static boolean isLastStatementOf(CtBlock<?> block, CtStatement statement) {
		List<CtStatement> statements = block.getStatements();
		CtStatement lastStatement = ListLibrary.last(statements);
		return lastStatement == statement;
	}
	
	public static CtStatement statementOf(CtCodeElement codeElement) {
		Class<CtStatement> statementClass = CtStatement.class;
		if (isInstanceOf(statementClass, codeElement)) {
			return castTo(statementClass, codeElement);
		}
		return codeElement.getParent(statementClass);
	}
}
