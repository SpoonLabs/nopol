package fr.inria.lille.infinitel.loop;

import static java.lang.String.format;

import java.util.List;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtStatement;
import spoon.reflect.factory.Factory;
import fr.inria.lille.commons.collections.ListLibrary;
import fr.inria.lille.commons.spoon.SpoonLibrary;

public class IterationAuditor {

	public static IterationAuditor newInstance(Number threshold) {
		int instanceNumber = allInstances().size();
		IterationAuditor newInstance = new IterationAuditor(threshold, instanceNumber);
		allInstances().add(newInstance);
		return newInstance;
	}
	
	public static IterationAuditor instance(int instanceID) {
		return allInstances().get(instanceID);
	}
	
	private static List<IterationAuditor> allInstances() {
		if (allInstances == null) {
			allInstances = ListLibrary.newArrayList();
		}
		return allInstances;
	}
	
	public String canonicalInstanceName() {
		return getClass().getCanonicalName() + format(".instance(%d)", instanceID());
	}
	
	public String counterVariableName() {
		return String.format("loopIterations_%d", instanceID());
	}
	
	public CtLocalVariable<Integer> counterCreation(Factory factory) {
		return SpoonLibrary.newLocalVariableDeclaration(factory, "int", counterVariableName(), 0);
	}
	
	public CtExpression<Boolean> loopCondition(Factory factory) {
		String codeSnippet = canonicalInstanceName() + format(".allowsIteration(%s)", counterVariableName());
		return SpoonLibrary.newExpressionFromSnippet(factory, codeSnippet, Boolean.class);
	}
	
	public CtStatement incrementStatement(Factory factory) {
		String codeSnippet = counterVariableName() + " += 1";
		return SpoonLibrary.newStatementFromSnippet(factory, codeSnippet);
	}

	public CtStatement afterLoopStatement(Factory factory) {
		String codeSnippet = canonicalInstanceName() + format(".addRecordOf(%s)", counterVariableName());
		return SpoonLibrary.newStatementFromSnippet(factory, codeSnippet);
	}

	public boolean allowsIteration(int numberOfIterations) {
		return isDisabled() || numberOfIterations < threshold();  
	}
	
	public void addRecordOf(int iterations) {
		iterationsRecord().add(iterations);
	}
	
	public boolean loopReachedThreshold() {
		return iterationsRecord().contains(threshold());
	}

	public List<Integer> iterationsRecord() {
		return iterationsRecord;
	}
	
	public int threshold() {
		return threshold;
	}
	
	public boolean disable() {
		return setDisabled(true);
	}
	
	public boolean enable() {
		resetRecord();
		return setDisabled(false);
	}
	
	public boolean isDisabled() {
		return disabled;
	}
	
	protected Number setThreshold(Number number) {
		int oldValue = threshold;
		threshold = number.intValue();
		return oldValue;
	}
	
	protected void resetRecord() {
		iterationsRecord = ListLibrary.newLinkedList();
	}
	
	private int instanceID() {
		return instanceID;
	}
	
	private boolean setDisabled(boolean value) {
		boolean oldValue = disabled;
		disabled = value;
		return value != oldValue;
	}
	
	private IterationAuditor(Number threshold, int instanceNumber) {
		instanceID = instanceNumber;
		resetRecord();
		enable();
		setThreshold(threshold);
	}
	
	private int threshold;
	private int instanceID;
	private boolean disabled;
	private List<Integer> iterationsRecord;
	
	/** XXX This causes memory leaks **/
	private static List<IterationAuditor> allInstances;
}
