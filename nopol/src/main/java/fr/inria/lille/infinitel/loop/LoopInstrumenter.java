package fr.inria.lille.infinitel.loop;

import static fr.inria.lille.commons.spoon.SpoonLibrary.newBlock;
import static fr.inria.lille.commons.spoon.SpoonLibrary.newBreak;
import static fr.inria.lille.commons.spoon.SpoonLibrary.newConjunctionExpression;
import static fr.inria.lille.commons.spoon.SpoonLibrary.newExpressionFromSnippet;
import static fr.inria.lille.commons.spoon.SpoonLibrary.newIf;
import static fr.inria.lille.commons.spoon.SpoonLibrary.newLiteral;
import static fr.inria.lille.commons.spoon.SpoonLibrary.newLocalVariableDeclaration;
import static fr.inria.lille.commons.spoon.SpoonLibrary.newStatementFromSnippet;
import static java.lang.String.format;
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

public class LoopInstrumenter {
	
	public static void instrument(CtWhile loop, LoopIterationCounter counter) {
		CtIf newIf = newLoopBodyIf(loop, counter);
		setNewLoopingCondition(loop);
		setNewWhileBody(loop, newIf);
		traceReachableVariables(newIf, counter);
		declareIterationCounter(loop, counter);
	}

	private static CtIf newLoopBodyIf(CtWhile loop, LoopIterationCounter counter) {
		Factory factory = loop.getFactory();
		CtStatement body = loop.getBody();
		CtExpression<Boolean>  counterCheck = newExpressionFromSnippet(factory, counter.loopConditionInvocation(), Boolean.class); 
		CtExpression<Boolean> newIfCondition = newConjunctionExpression(factory, counterCheck, loop.getLoopingExpression());
		CtStatement afterLoop = newStatementFromSnippet(factory, counter.afterLoopInvocation());
		CtIf newIf = newIf(factory, newIfCondition, body, newBlock(factory, afterLoop, newBreak(factory)));
		iterationCounterIncremenatation(body, counter);
		return newIf;
	}
	
	private static void iterationCounterIncremenatation(CtStatement block, LoopIterationCounter counter) {
		String incrementSnippet = format("%s += 1", counter.variableName());
		CtStatement increment = newStatementFromSnippet(block.getFactory(), incrementSnippet);
		block.insertBefore(increment);
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
	
	private static void declareIterationCounter(CtWhile loop, LoopIterationCounter counter) {
		Factory factory = loop.getFactory();
		CtLocalVariable<Integer> counterCreation = newLocalVariableDeclaration(factory, "int", counter.variableName(), 0);
		counterCreation.setParent(loop.getParent());
		loop.insertBefore(counterCreation);
	}
	
	private static void traceReachableVariables(CtIf newIf, LoopIterationCounter counter) {
		Factory factory = newIf.getFactory();
		RuntimeValuesProcessor<CtStatement> valuesProcessor = new IterationRuntimeValuesProcessor<CtStatement>(counter.variableName());
		CtStatement meaninglessStatement = newStatementFromSnippet(factory, "\"\".length()");
		CtExpression<Boolean> isEnabled = newExpressionFromSnippet(factory, IterationRuntimeValues.instance().isEnabledCondition(), Boolean.class);
		CtIf ifWithTraceStatements = newIf(factory, isEnabled, newBlock(factory, meaninglessStatement));
		ifWithTraceStatements.setParent(newIf.getParent());
		newIf.insertBefore(ifWithTraceStatements);
		valuesProcessor.process(meaninglessStatement);
	}
}
