package fr.inria.lille.infinitel.instrumenting;


import static fr.inria.lille.commons.spoon.util.SpoonModelLibrary.newBlock;
import static fr.inria.lille.commons.spoon.util.SpoonModelLibrary.newBreak;
import static fr.inria.lille.commons.spoon.util.SpoonModelLibrary.newConjunctionExpression;
import static fr.inria.lille.commons.spoon.util.SpoonModelLibrary.newExpressionFromSnippet;
import static fr.inria.lille.commons.spoon.util.SpoonModelLibrary.newIf;
import static fr.inria.lille.commons.spoon.util.SpoonModelLibrary.newLiteral;
import static fr.inria.lille.commons.spoon.util.SpoonModelLibrary.newLocalVariableDeclaration;
import static fr.inria.lille.commons.spoon.util.SpoonModelLibrary.newStatementFromSnippet;
import static fr.inria.lille.commons.spoon.util.SpoonModelLibrary.setLoopBody;
import static fr.inria.lille.commons.spoon.util.SpoonModelLibrary.setLoopingCondition;
import static fr.inria.lille.commons.spoon.util.SpoonStatementLibrary.insertAfterUnderSameParent;
import static fr.inria.lille.commons.spoon.util.SpoonStatementLibrary.insertBefore;
import static fr.inria.lille.commons.spoon.util.SpoonStatementLibrary.insertBeforeUnderSameParent;
import static java.lang.String.format;

import java.util.Collection;
import java.util.List;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtBreak;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtWhile;
import spoon.reflect.factory.Factory;
import fr.inria.lille.commons.spoon.collectable.CollectableValueFinder;
import fr.inria.lille.commons.trace.RuntimeValues;
import fr.inria.lille.commons.utils.Singleton;

public class LoopInstrumenter {
	
	public static void instrument(LoopMonitor loopMonitor, RuntimeValues runtimeValues) {
		String counterName = "loopEntrancesCounter_" + loopMonitor.instanceID();
		CtWhile astLoop = loopMonitor.loop().astLoop();
		Factory factory = astLoop.getFactory();
		Collection<String> collectables = collectableFinder().findFromWhile(astLoop);
		CtIf newIf = loopBodyWrapper(factory, astLoop);
		appendMonitoredEvaluation(factory, newIf, loopMonitor, counterName);
		appendMonitoredExit(factory, loopMonitor, counterName);
		traceReachableValues(factory, collectables, runtimeValues, newIf);
		declareEntrancesCounter(factory, astLoop, loopMonitor, counterName);
		appendCounterIncrementation(newIf, loopMonitor, counterName);
	}

	private static CtIf loopBodyWrapper(Factory factory, CtWhile loop) {
		CtIf newIf = newIf(factory, loop.getLoopingExpression(), loop.getBody(), newBreak(factory));
		setLoopingCondition(loop, newLiteral(factory, true));
		setLoopBody(loop, newIf);
		return newIf;
	}
	
	private static void appendMonitoredEvaluation(Factory factory, CtIf newIf, LoopMonitor loopMonitor, String counterName) {
		String codeSnippet = loopMonitor.invocationOnLoopConditionEvaluation(counterName);
		CtExpression<Boolean> monitoredEvaluation = newExpressionFromSnippet(factory, codeSnippet, Boolean.class);
		newIf.setCondition(newConjunctionExpression(factory, newIf.getCondition(), monitoredEvaluation));
	}

	private static void appendMonitoredExit(Factory factory, LoopMonitor loopMonitor, String counterName) {
		CtWhile astLoop = loopMonitor.loop().astLoop();
		CtStatement monitoredExit = newStatementFromSnippet(astLoop.getFactory(), loopMonitor.invocationOnFirstStatementAfterLoop(counterName));
		insertAfterUnderSameParent(monitoredExit, astLoop);
		appendMonitoredReturnExit(factory, loopMonitor, counterName);
		appendMonitoredBreakExit(factory, loopMonitor, counterName);
	}
	
	private static void appendMonitoredReturnExit(Factory factory, LoopMonitor loopMonitor, String counterName) {
		for (CtReturn<?> returnStatement : loopMonitor.loop().returnStatements()) {
			CtStatement invocationOnLoopReturn = newStatementFromSnippet(factory, loopMonitor.invocationOnLoopReturn(counterName));
			insertBeforeUnderSameParent(invocationOnLoopReturn, returnStatement);
		}
	}

	private static void appendMonitoredBreakExit(Factory factory, LoopMonitor loopMonitor, String counterName) {
		for (CtBreak breakStatement : loopMonitor.loop().breakStatements()) {
			CtStatement invocationOnLoopBreak = newStatementFromSnippet(factory, loopMonitor.invocationOnLoopBreak(counterName));
			insertBeforeUnderSameParent(invocationOnLoopBreak, breakStatement);
		}
	}

	private static void traceReachableValues(Factory factory, Collection<String> collectables, RuntimeValues runtimeValues, CtIf newIf) {
		CtBlock<?> tracingStatements = newBlock(factory);
		CtExpression<Boolean> collectorIsEnabled = newExpressionFromSnippet(factory, runtimeValues.isEnabledInquiry(), Boolean.class);
		CtIf tracingIf = newIf(factory, collectorIsEnabled, tracingStatements);
		List<CtStatement> statements = runtimeValues.asCollectionStatements(collectables, tracingStatements);
		tracingStatements.setStatements(statements);
		insertBeforeUnderSameParent(tracingIf, newIf);
	}

	private static void declareEntrancesCounter(Factory factory, CtWhile loop, LoopMonitor loopMonitor, String counterName) {
		CtLocalVariable<Integer> counterCreation = newLocalVariableDeclaration(factory, "int", counterName, 0, loop.getParent());
		insertBeforeUnderSameParent(counterCreation, loop);
	}

	private static void appendCounterIncrementation(CtIf newIf, LoopMonitor loopMonitor, String counterName) {
		CtStatement increment = newStatementFromSnippet(newIf.getFactory(), format("%s += 1", counterName));
		CtStatement then = newIf.getThenStatement();
		insertBefore(increment, then, then);
	}
	
	private static CollectableValueFinder collectableFinder() {
		return Singleton.of(CollectableValueFinder.class);
	}
}
