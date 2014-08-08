package fr.inria.lille.commons.trace;

import static fr.inria.lille.commons.spoon.util.SpoonModelLibrary.newBlock;
import static fr.inria.lille.commons.spoon.util.SpoonModelLibrary.newExpressionFromSnippet;
import static fr.inria.lille.commons.spoon.util.SpoonModelLibrary.newIf;
import static fr.inria.lille.commons.spoon.util.SpoonModelLibrary.newStatementFromSnippet;
import static fr.inria.lille.commons.spoon.util.SpoonStatementLibrary.insertBeforeUnderSameParent;

import java.util.Collection;
import java.util.List;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
import spoon.reflect.factory.Factory;
import fr.inria.lille.commons.collections.ListLibrary;

public class RuntimeValuesInstrumenter {

	public static CtStatement runtimeCollectionBefore(CtStatement insertionPoint, Collection<String> reachableValues, RuntimeValues runtimeValues) {
		Factory factory = insertionPoint.getFactory();
		List<CtStatement> newStatements = ListLibrary.newLinkedList();
		for (String reachableValue : reachableValues) {
			addCollectionStatementFor(newStatements, reachableValue, factory, runtimeValues);
		}
		addCollectionEndedNofitication(newStatements, factory, runtimeValues);
		return collectionWrappingIf(newStatements, runtimeValues, insertionPoint);
	}
	
	private static void addCollectionStatementFor(List<CtStatement> statements, String variableName, Factory factory, RuntimeValues runtimeValues) {
		statements.add(newStatementFromSnippet(factory, runtimeValues.invocationOnCollectionOf(variableName)));
	}
	
	private static void addCollectionEndedNofitication(List<CtStatement> statements, Factory factory, RuntimeValues runtimeValues) {
		statements.add(newStatementFromSnippet(factory, runtimeValues.invocationOnCollectionEnd()));
	}
	
	private static CtIf collectionWrappingIf(List<CtStatement> collectingStatements, RuntimeValues runtimeValues, CtStatement insertionPoint) {
		Factory factory = insertionPoint.getFactory();
		CtStatement newBlock = newBlock(factory, collectingStatements);
		CtExpression<Boolean> isEnabled = newExpressionFromSnippet(factory, runtimeValues.isEnabledInquiry(), Boolean.class);
		CtIf newIf = newIf(factory, isEnabled, newBlock);
		insertBeforeUnderSameParent(newIf, insertionPoint);
		return newIf;
	}
	
}
