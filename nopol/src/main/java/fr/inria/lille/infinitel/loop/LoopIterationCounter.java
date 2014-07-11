package fr.inria.lille.infinitel.loop;

import static java.lang.String.format;

import java.util.List;

import fr.inria.lille.commons.classes.Toggle;
import fr.inria.lille.commons.collections.ListLibrary;

public class LoopIterationCounter extends Toggle {

	public static LoopIterationCounter newInstance(Number threshold) {
		int instanceNumber = allInstances().size();
		LoopIterationCounter newInstance = new LoopIterationCounter(threshold, instanceNumber);
		allInstances().add(newInstance);
		return newInstance;
	}
	
	public static LoopIterationCounter instance(int instanceID) {
		return allInstances().get(instanceID);
	}
	
	@Override
	public void reset() {
		lastRecordedValue = null;
		thresholdWasReached = false;
		setNumberOfRecords(0);
	}
	
	public String loopConditionInvocation() {
		return  getClass().getCanonicalName() + format(".instance(%d).allowsIteration(%s)", instanceID(), variableName());
	}
	
	public String afterLoopInvocation() {
		return getClass().getCanonicalName() + format(".instance(%d).recordValue(%s)", instanceID(), variableName());
	}
	
	public String variableName() {
		return format("loopIterationCounterVariable_%d", instanceID());
	}
	
	public boolean allowsIteration(int numberOfIterations) {
		return isDisabled() || numberOfIterations < threshold();
	}
	
	public void recordValue(Number numberOfIterations) {
		int intValue = numberOfIterations.intValue();
		lastRecordedValue = intValue;
		setNumberOfRecords(numberOfRecords() + 1);
		thresholdWasReached = thresholdWasReached() || intValue == threshold();
	}
	
	public boolean thresholdWasReached() {
		return thresholdWasReached;
	}

	private LoopIterationCounter(Number threshold, int instanceNumber) {
		super();
		instanceID = instanceNumber;
		setThreshold(threshold);
	}
	
	public Integer lastRecordedValue() {
		return lastRecordedValue;
	}
	
	public int threshold() {
		return threshold;
	}
	
	public int numberOfRecords() {
		return numberOfRecords;
	}
	
	protected Number setThreshold(Number number) {
		int oldValue = threshold;
		threshold = number.intValue();
		return oldValue;
	}
	
	private int instanceID() {
		return instanceID;
	}
	
	private void setNumberOfRecords(int number) {
		numberOfRecords = number;
	}
	
	private static List<LoopIterationCounter> allInstances() {
		if (allInstances == null) {
			allInstances = ListLibrary.newArrayList();
		}
		return allInstances;
	}
	
	private int threshold;
	private int instanceID;
	private int numberOfRecords;
	private Integer lastRecordedValue;
	private boolean thresholdWasReached;
	
	/** XXX This causes memory leaks **/
	private static List<LoopIterationCounter> allInstances;
}
