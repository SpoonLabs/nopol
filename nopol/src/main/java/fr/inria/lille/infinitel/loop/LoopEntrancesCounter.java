package fr.inria.lille.infinitel.loop;

import static java.lang.String.format;

import java.util.List;

import fr.inria.lille.commons.classes.GlobalToggle;
import fr.inria.lille.commons.collections.ListLibrary;

public class LoopEntrancesCounter extends GlobalToggle {

	public static LoopEntrancesCounter newInstance(Number threshold) {
		int instanceNumber = allInstances().size();
		LoopEntrancesCounter newInstance = new LoopEntrancesCounter(threshold, instanceNumber);
		allInstances().add(newInstance);
		return newInstance;
	}
	
	public static LoopEntrancesCounter instance(int instanceID) {
		return allInstances().get(instanceID);
	}
	
	@Override
	public void reset() {
		setTopRecord(0);
		setNumberOfRecords(0);
		setLastRecordedValue(null);
	}
	
	@Override
	protected String instanceName() {
		return format("instance(%d)", instanceID());
	}

	public String variableName() {
		return format("completedIterationsInLoop_%d", instanceID());
	}
	
	public String initializationInvocation() {
		return globallyAccessibleName() + ".counterInitialization()";
	}
	
	public int counterInitialization() {
		if (isEnabled()) {
			setNumberOfRecords(numberOfRecords() + 1);
			setLastRecordedValue(0);
		}
		return 0;
	}
	
	public String loopConditionInvocation() {
		return  globallyAccessibleName() + format(".canEnterLoop(%s++)", variableName());
	}

	public boolean canEnterLoop(int completedIterations) {
		boolean canEnterLoop = completedIterations < threshold();
		if (isEnabled() && canEnterLoop) {
			recordEntrance(completedIterations + 1);
		}
		return canEnterLoop;
	}
	
	private void recordEntrance(int loopEntrances) {
		setLastRecordedValue(loopEntrances);
		if (loopEntrances > topRecord()) {
			setTopRecord(loopEntrances);
		}
	}
	
	public boolean thresholdWasReached() {
		return topRecord() == threshold();
	}

	private LoopEntrancesCounter(Number threshold, int instanceNumber) {
		super();
		setThreshold(threshold);
		instanceID = instanceNumber;
	}
	
	public Integer lastRecordedValue() {
		return lastRecordedValue;
	}
	
	public int threshold() {
		return threshold;
	}
	
	protected int topRecord() {
		return topRecord;
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
	
	private void setTopRecord(int number) {
		topRecord = number;
	}
	
	private void setNumberOfRecords(int number) {
		numberOfRecords = number;
	}
	
	private void setLastRecordedValue(Integer record) {
		lastRecordedValue = record;
	}
	
	private static List<LoopEntrancesCounter> allInstances() {
		if (allInstances == null) {
			allInstances = ListLibrary.newArrayList();
		}
		return allInstances;
	}
	
	private int threshold;
	private int topRecord;
	private int instanceID;
	private int numberOfRecords;
	private Integer lastRecordedValue;
	
	/** XXX This causes memory leaks **/
	private static List<LoopEntrancesCounter> allInstances;
}
