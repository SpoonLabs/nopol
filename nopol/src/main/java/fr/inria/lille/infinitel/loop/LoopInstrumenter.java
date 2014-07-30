package fr.inria.lille.infinitel.loop;

import static fr.inria.lille.commons.spoon.SpoonLibrary.newBlock;
import static fr.inria.lille.commons.spoon.SpoonLibrary.newBreak;
import static fr.inria.lille.commons.spoon.SpoonLibrary.newConjunctionExpression;
import static fr.inria.lille.commons.spoon.SpoonLibrary.newExpressionFromSnippet;
import static fr.inria.lille.commons.spoon.SpoonLibrary.newIf;
import static fr.inria.lille.commons.spoon.SpoonLibrary.newLiteral;
import static fr.inria.lille.commons.spoon.SpoonLibrary.newLocalVariableDeclaration;
import static fr.inria.lille.commons.spoon.SpoonLibrary.newStatementFromSnippet;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtWhile;
import spoon.reflect.factory.Factory;
import fr.inria.lille.commons.trace.IterationRuntimeValues;
import fr.inria.lille.commons.trace.IterationRuntimeValuesProcessor;
import fr.inria.lille.commons.trace.RuntimeValuesProcessor;
import fr.inria.lille.infinitel.loop.counters.LoopEntrancesCounter;

public class LoopInstrumenter {
	
	public static void instrument(CtWhile loop, LoopEntrancesCounter counter) {
		CtIf newIf = newLoopBodyIf(loop, counter);
		setNewLoopingCondition(loop);
		setNewWhileBody(loop, newIf);
		traceReachableVariables(newIf, counter);
		declareIterationCounter(loop, counter);
	}

	private static CtIf newLoopBodyIf(CtWhile loop, LoopEntrancesCounter counter) {
		Factory factory = loop.getFactory();
		CtStatement body = loop.getBody();
		CtExpression<Boolean>  counterCheck = newExpressionFromSnippet(factory, counter.loopConditionInvocation(), Boolean.class); 
		CtExpression<Boolean> newIfCondition = newConjunctionExpression(factory, loop.getLoopingExpression(), counterCheck);
		CtIf newIf = newIf(factory, newIfCondition, body, newBreak(factory));
		return newIf;
	}
	
	private static void setNewWhileBody(CtWhile loop, CtIf newIf) {
		Factory factory = loop.getFactory();
		CtBlock<CtStatement> newWhileBlock = newBlock(factory, newIf);
		newWhileBlock.setParent(loop);
		loop.setBody(newWhileBlock);
		newIf.setParent(newWhileBlock);
	}
	
	private static void setNewLoopingCondition(CtWhile loop) {
		Factory factory = loop.getFactory();
		CtLiteral<Boolean> trueCondition = newLiteral(factory, true);
		trueCondition.setParent(loop);
		loop.setLoopingExpression(trueCondition);
	}
	
	private static void declareIterationCounter(CtWhile loop, LoopEntrancesCounter counter) {
		Factory factory = loop.getFactory();
		CtLocalVariable<Integer> counterCreation = newLocalVariableDeclaration(factory, "int", counter.variableName(), counter.initializationInvocation());
		counterCreation.setParent(loop.getParent());
		loop.insertBefore(counterCreation);
	}
	
	private static void traceReachableVariables(CtIf newIf, LoopEntrancesCounter counter) {
		Factory factory = newIf.getFactory();
		RuntimeValuesProcessor<CtStatement> valuesProcessor = new IterationRuntimeValuesProcessor<CtStatement>(counter.variableName());
		CtStatement meaninglessStatement = newStatementFromSnippet(factory, "\"\".length()");
		CtIf ifWithTraceStatements = newIfWithTraceStatements(factory, counter, meaninglessStatement);
		ifWithTraceStatements.setParent(newIf.getParent());
		newIf.insertBefore(ifWithTraceStatements);
		valuesProcessor.process(meaninglessStatement);
	}
	
	private static CtIf newIfWithTraceStatements(Factory factory, LoopEntrancesCounter counter, CtStatement thenStatement) {
		CtExpression<Boolean> counterIsEnabled = newExpressionFromSnippet(factory, counter.isEnabledInquiry(), Boolean.class);
		CtExpression<Boolean> collectorIsEnabled = newExpressionFromSnippet(factory, IterationRuntimeValues.instance().isEnabledInquiry(), Boolean.class);
		return newIf(factory, newConjunctionExpression(factory, collectorIsEnabled, counterIsEnabled), newBlock(factory, thenStatement));
	}
}
