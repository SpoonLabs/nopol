package fr.inria.lille.infinitel.loop;

import java.util.List;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtWhile;
import spoon.reflect.cu.SourcePosition;
import fr.inria.lille.commons.collections.ListLibrary;
import fr.inria.lille.commons.spoon.SpoonLibrary;

public class IterationAuditor extends AbstractProcessor<CtWhile> {

	public static IterationAuditor newInstance(SourcePosition loopPosition, Number threshold) {
		int instanceNumber = allInstances().size();
		IterationAuditor newInstance = new IterationAuditor(loopPosition, threshold, instanceNumber);
		allInstances().add(newInstance);
		return newInstance;
	}
	
	private static List<IterationAuditor> allInstances() {
		if (allInstances == null) {
			allInstances = ListLibrary.newArrayList();
		}
		return allInstances;
	}
	
	public static IterationAuditor instance(int instanceID) {
		return allInstances().get(instanceID);
	}
	
	private IterationAuditor(SourcePosition position, Number threshold, int instanceNumber) {
		this.loopPosition = position;
		instanceID = instanceNumber;
		resetRecord();
		enable();
		setThreshold(threshold);
	}
	
	@Override
	public boolean isToBeProcessed(CtWhile loopStatement) {
		return auditedLoopPosition().equals(loopStatement.getPosition());
	}
	
	@Override
	public void process(CtWhile loopStatement) {
		CtStatement beforeStatement = beforeLoopStatement(loopStatement);
		CtExpression<Boolean> modifiedExpression = modifiedLoopingExpression(loopStatement);
		CtStatement afterStatement = afterLoopStatement(loopStatement);
		loopStatement.insertBefore(beforeStatement);
		loopStatement.setLoopingExpression(modifiedExpression);
		loopStatement.insertAfter(afterStatement);
	}
	
	private String counterVariableName() {
		return String.format("loopIterations_%d", instanceID());
	}
	
	private CtStatement beforeLoopStatement(CtWhile loopStatement) {
		String codeSnippet = String.format("int %s = -1", counterVariableName());
		return SpoonLibrary.statementFrom(codeSnippet, loopStatement.getParent());
	}
	
	private CtExpression<Boolean> modifiedLoopingExpression(CtWhile loopStatement) {
		String codeSnippet = canonicalName() + String.format(".instance(%d).allowsIteration(++%s)", instanceID(), counterVariableName());
		return SpoonLibrary.composedExpression(codeSnippet, BinaryOperatorKind.AND, loopStatement.getLoopingExpression());
	}

	private CtStatement afterLoopStatement(CtWhile loopStatement) {
		String codeSnippet = canonicalName() + String.format(".instance(%d).addRecordOf(%s)", instanceID(), counterVariableName());
		return SpoonLibrary.statementFrom(codeSnippet, loopStatement.getParent());
	}

	public boolean allowsIteration(int numberOfIterations) {
		return isDisabled() || numberOfIterations < threshold();  
	}
	
	public String canonicalName() {
		return getClass().getCanonicalName();
	}
	
	public void addRecordOf(int iterations) {
		iterationsRecord().add(iterations);
	}
	
	public boolean loopReachedThreshold() {
		return iterationsRecord().contains(threshold());
	}

	public SourcePosition auditedLoopPosition() {
		return loopPosition;
	}
	
	protected void resetRecord() {
		iterationsRecord = ListLibrary.newLinkedList();
	}
	
	public List<Integer> iterationsRecord() {
		return iterationsRecord;
	}
	
	public int threshold() {
		return threshold;
	}
	
	public Number setThreshold(Number number) {
		int oldValue = threshold;
		threshold = number.intValue();
		return oldValue;
	}
	
	private int instanceID() {
		return instanceID;
	}
	
	public boolean disable() {
		resetRecord();
		return setDisabled(true);
	}
	
	public boolean enable() {
		return setDisabled(false);
	}
	
	private boolean setDisabled(boolean value) {
		boolean oldValue = disabled;
		disabled = value;
		return value != oldValue;
	}
	
	public boolean isDisabled() {
		return disabled;
	}
	
	private int threshold;
	private int instanceID;
	private boolean disabled;
	private SourcePosition loopPosition;
	private List<Integer> iterationsRecord;
	
	// XXX This causes memory leaks
	private static List<IterationAuditor> allInstances;
}
