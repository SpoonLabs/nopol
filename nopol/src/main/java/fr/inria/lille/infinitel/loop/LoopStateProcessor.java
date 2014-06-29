package fr.inria.lille.infinitel.loop;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtWhile;
import spoon.reflect.factory.Factory;
import fr.inria.lille.commons.spoon.SpoonLibrary;
import fr.inria.lille.commons.trace.IterationRuntimeValuesProcessor;
import fr.inria.lille.commons.trace.RuntimeValuesProcessor;

public class LoopStateProcessor {
	
	public void process(CtWhile loop, IterationAuditor auditor) {
		CtIf newIf = newIf(loop, auditor);
		setNewLoopingCondition(loop);
		setNewWhileBody(loop, newIf);
		traceReachableVariables(newIf, auditor);
		declareIterationCounter(loop, auditor);
	}

	private CtIf newIf(CtWhile loop, IterationAuditor auditor) {
		Factory factory = loop.getFactory();
		CtStatement body = loop.getBody();
		CtBlock<CtStatement> elseBranch = SpoonLibrary.newBlock(factory, auditor.afterLoopStatement(factory), SpoonLibrary.newBreak(factory));
		CtExpression<Boolean> newIfCondition = SpoonLibrary.newConjunctionExpression(factory, auditor.loopCondition(factory), loop.getLoopingExpression());
		CtIf newIf = SpoonLibrary.newIf(factory, newIfCondition, body, elseBranch);
		body.insertBefore(auditor.incrementStatement(factory));
		return newIf;
	}
	
	private void setNewWhileBody(CtWhile loop, CtIf newIf) {
		Factory factory = loop.getFactory();
		CtBlock<CtStatement> newWhileBlock = SpoonLibrary.newBlock(factory, newIf);
		newWhileBlock.setParent(loop);
		loop.setBody(newWhileBlock);
		newIf.setParent(newWhileBlock);
	}
	
	private void setNewLoopingCondition(CtWhile loop) {
		Factory factory = loop.getFactory();
		CtLiteral<Boolean> trueCondition = SpoonLibrary.newLiteral(factory, true);
		trueCondition.setParent(loop);
		loop.setLoopingExpression(trueCondition);
	}
	
	private void traceReachableVariables(CtIf newIf, IterationAuditor auditor) {
		String iterationCounterName = auditor.counterVariableName();
		RuntimeValuesProcessor<CtIf> valuesProcessor = new IterationRuntimeValuesProcessor<CtIf>(iterationCounterName);
		valuesProcessor.process(newIf);
	}
	
	private void declareIterationCounter(CtWhile loop, IterationAuditor auditor) {
		Factory factory = loop.getFactory();
		CtLocalVariable<Integer> counterCreation = auditor.counterCreation(factory);
		counterCreation.setParent(loop.getParent());
		loop.insertBefore(counterCreation);
	}
}
