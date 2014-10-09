package fr.inria.lille.commons.trace;

import static fr.inria.lille.commons.spoon.util.SpoonModelLibrary.newBlock;
import static fr.inria.lille.commons.spoon.util.SpoonModelLibrary.newExpressionFromSnippet;
import static fr.inria.lille.commons.spoon.util.SpoonModelLibrary.newIf;
import static fr.inria.lille.commons.spoon.util.SpoonModelLibrary.newStatementFromSnippet;
import static fr.inria.lille.commons.spoon.util.SpoonStatementLibrary.insertBeforeUnderSameParent;

import java.util.List;
import java.util.Map;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
import spoon.reflect.factory.Factory;
import xxl.java.container.classic.MetaList;

public class RuntimeValuesInstrumenter {

	public static <T> CtStatement runtimeCollectionBefore(CtStatement insertionPoint, Map<String, String> inputs, String outputName, RuntimeValues<T> runtimeValues) {
		Factory factory = insertionPoint.getFactory();
		List<CtStatement> newStatements = MetaList.newLinkedList();
		for (String reachableValue : inputs.keySet()) {
			addCollectionStatementFor(newStatements, factory, runtimeValues.invocationOnCollectionOf(reachableValue, inputs.get(reachableValue)));
		}
		addCollectionStatementFor(newStatements, factory, runtimeValues.invocationOnOutputCollection(outputName));
		addCollectionStatementFor(newStatements, factory, runtimeValues.invocationOnCollectionEnd());
		return collectionWrappingIf(newStatements, runtimeValues, insertionPoint);
	}
	
	private static <T> void addCollectionStatementFor(List<CtStatement> statements, Factory factory, String codeSnippet) {
		statements.add(newStatementFromSnippet(factory, codeSnippet));
	}
	
	private static <T> CtIf collectionWrappingIf(List<CtStatement> collectingStatements, RuntimeValues<T> runtimeValues, CtStatement insertionPoint) {
		Factory factory = insertionPoint.getFactory();
		CtStatement newBlock = newBlock(factory, collectingStatements);
		CtExpression<Boolean> isEnabled = newExpressionFromSnippet(factory, runtimeValues.isEnabledInquiry(), Boolean.class);
		CtIf newIf = newIf(factory, isEnabled, newBlock);
		insertBeforeUnderSameParent(newIf, insertionPoint);
		return newIf;
	}
	
}
