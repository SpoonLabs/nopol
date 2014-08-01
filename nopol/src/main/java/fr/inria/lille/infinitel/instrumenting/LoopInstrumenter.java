package fr.inria.lille.infinitel.instrumenting;

import static fr.inria.lille.commons.spoon.SpoonLibrary.newBlock;
import static fr.inria.lille.commons.spoon.SpoonLibrary.newBreak;
import static fr.inria.lille.commons.spoon.SpoonLibrary.newConjunctionExpression;
import static fr.inria.lille.commons.spoon.SpoonLibrary.newExpressionFromSnippet;
import static fr.inria.lille.commons.spoon.SpoonLibrary.newIf;
import static fr.inria.lille.commons.spoon.SpoonLibrary.newLiteral;
import static fr.inria.lille.commons.spoon.SpoonLibrary.newLocalVariableDeclaration;
import static fr.inria.lille.commons.spoon.SpoonLibrary.newStatementFromSnippet;
import static fr.inria.lille.commons.spoon.SpoonLibrary.setLoopBody;
import static fr.inria.lille.commons.spoon.SpoonLibrary.setLoopingCondition;
import static java.lang.String.format;

import java.util.List;

import spoon.reflect.code.CtBreak;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtWhile;
import spoon.reflect.factory.Factory;
import fr.inria.lille.commons.trace.IterationRuntimeValues;
import fr.inria.lille.commons.trace.IterationRuntimeValuesProcessor;
import fr.inria.lille.commons.trace.RuntimeValuesProcessor;

public class LoopInstrumenter {
	
	public static void instrument(LoopMonitor loopMonitor) {
		String counterName = "loopEntrancesCounter_" + loopMonitor.instanceID();
		CtWhile astLoop = loopMonitor.loop().astLoop();
		Factory factory = astLoop.getFactory();
		CtIf newIf = loopBodyWrapper(factory, astLoop);
		appendMonitoredEvaluation(factory, newIf, loopMonitor, counterName);
		appendMonitoredExit(factory, loopMonitor, counterName);
		traceReachableVariables(factory, newIf, loopMonitor, counterName);
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
		CtStatement monitoredExit = newStatementFromSnippet(astLoop.getFactory(), loopMonitor.invocationOnLoopExit(counterName));
		astLoop.insertAfter(monitoredExit);
		appendMonitoredReturnExit(factory, loopMonitor, monitoredExit);
		appendMonitoredBreakExit(factory, loopMonitor);
	}
	
	private static void appendMonitoredReturnExit(Factory factory, LoopMonitor loopMonitor, CtStatement monitoredExit) {
		CtStatement invocationOnLoopReturn = newStatementFromSnippet(factory, loopMonitor.invocationOnLoopReturn());
		for (CtReturn<?> returnStatement : loopMonitor.loop().returnStatements()) {
			returnStatement.insertBefore(monitoredExit);
			returnStatement.insertBefore(invocationOnLoopReturn);
		}
	}

	private static void appendMonitoredBreakExit(Factory factory, LoopMonitor loopMonitor) {
		CtStatement invocationOnLoopBreak = newStatementFromSnippet(factory, loopMonitor.invocationOnLoopBreak());
		for (CtBreak breakStatement : loopMonitor.loop().breakStatements()) {
			breakStatement.insertBefore(invocationOnLoopBreak);
		}
	}

	private static void declareEntrancesCounter(Factory factory, CtWhile loop, LoopMonitor loopMonitor, String counterName) {
		CtLocalVariable<Integer> counterCreation = newLocalVariableDeclaration(factory, "int", counterName, 0, loop.getParent());
		loop.insertBefore(counterCreation);
	}
	
	private static void traceReachableVariables(Factory factory, CtIf newIf, LoopMonitor loopMonitor, String counterName) {
		RuntimeValuesProcessor<CtStatement> valuesProcessor = new IterationRuntimeValuesProcessor<CtStatement>(counterName);
		List<CtStatement> collectionStatements = valuesProcessor.valueCollectionStatements(newIf);
		CtExpression<Boolean> counterIsEnabled = newExpressionFromSnippet(factory, loopMonitor.isEnabledInquiry(), Boolean.class);
		CtExpression<Boolean> collectorIsEnabled = newExpressionFromSnippet(factory, IterationRuntimeValues.instance().isEnabledInquiry(), Boolean.class);
		CtIf tracingIf = newIf(factory, newConjunctionExpression(factory, collectorIsEnabled, counterIsEnabled), newBlock(factory, collectionStatements));
		newIf.insertBefore(tracingIf);
	}
	
	private static void appendCounterIncrementation(CtIf newIf, LoopMonitor loopMonitor, String counterName) {
		CtStatement increment = newStatementFromSnippet(newIf.getFactory(), format("%s += 1", counterName));
		newIf.getThenStatement().insertBefore(increment);
	}
}
