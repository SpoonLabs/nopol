package fr.inria.lille.infinitel.instrumenting;

import static java.lang.String.format;

import java.util.List;
import java.util.Map;

import fr.inria.lille.commons.classes.GlobalToggle;
import fr.inria.lille.commons.collections.ListLibrary;
import fr.inria.lille.commons.collections.MapLibrary;
import fr.inria.lille.infinitel.loop.While;

public class LoopMonitor extends GlobalToggle {

	protected static LoopMonitor newInstance(While loop, Number threshold) {
		int instanceNumber = allInstances().size();
		LoopMonitor newInstance = new LoopMonitor(loop, threshold, instanceNumber);
		allInstances().add(newInstance);
		return newInstance;
	}
	
	public static LoopMonitor instance(int instanceID) {
		return allInstances().get(instanceID);
	}
	
	@Override
	protected void reset() {
		setTopRecord(0);
		setBreakExits(0);
		setReturnExits(0);
		setNumberOfRecords(0);
		setLastRecordedValue(null);
		recordFrequencies().clear();
	}
	
	@Override
	protected String instanceName() {
		return format("instance(%d)", instanceID());
	}

	public String invocationOnLoopConditionEvaluation(String counterName) {
		return  globallyAccessibleName() + format(".canEnterLoop(%s)", counterName);
	}
	
	public String invocationOnLoopBreak() {
		return globallyAccessibleName() + ".recordBreakExit()";
	}
	
	public String invocationOnLoopReturn() {
		return globallyAccessibleName() + ".recordReturnExit()";
	}
	
	public String invocationOnLoopExit(String counterName) {
		return globallyAccessibleName() + format(".recordEntrancesToExit(%s)", counterName);
	}

	public boolean canEnterLoop(int completedIterations) {
		return completedIterations < threshold();
	}
	
	public void recordEntrancesToExit(int loopEntrances) {
		synchronized (this) {
			if (loopEntrances > topRecord()) {
				setTopRecord(loopEntrances);
			}
			setLastRecordedValue(loopEntrances);
			setNumberOfRecords(numberOfRecords() + 1);
			recordFrequencies().put(loopEntrances, MapLibrary.getPutIfAbsent(recordFrequencies(), loopEntrances, 0) + 1);
		}
	}
	
	public void recordBreakExit() {
		synchronized (this) {
			setBreakExits(breakExits() + 1);
		}
	}
	
	public void recordReturnExit() {
		synchronized (this) {
			setReturnExits(returnExits() + 1);
		}
	}
	
	protected boolean thresholdWasReached() {
		return topRecord() == threshold();
	}

	protected LoopMonitor(While loop, Number threshold, int instanceID) {
		super();
		setThreshold(threshold);
		this.loop = loop;
		this.instanceID = instanceID;
		recordFrequencies = MapLibrary.newHashMap();
		LoopInstrumenter.instrument(this);
	}
	
	protected While loop() {
		return loop;
	}
	
	protected int threshold() {
		return threshold;
	}
	
	protected int topRecord() {
		return topRecord;
	}
	
	protected int instanceID() {
		return instanceID;
	}
	
	protected int breakExits() {
		return breakExits;
	}
	
	protected int returnExits() {
		return returnExits;
	}
	
	protected int numberOfRecords() {
		return numberOfRecords;
	}
	
	protected Integer lastRecordedValue() {
		return lastRecordedValue;
	}
	
	protected Map<Integer, Integer> recordFrequencies() {
		return recordFrequencies;
	}
	
	protected Number setThreshold(Number number) {
		int oldValue = threshold;
		threshold = number.intValue();
		return oldValue;
	}

	private void setTopRecord(int number) {
		topRecord = number;
	}
	
	private void setNumberOfRecords(int number) {
		numberOfRecords = number;
	}
	
	private void setBreakExits(int number) {
		breakExits = number;
	}
	
	private void setReturnExits(int number) {
		returnExits = number;
	}
	
	private void setLastRecordedValue(Integer record) {
		lastRecordedValue = record;
	}
	
	private static List<LoopMonitor> allInstances() {
		if (allInstances == null) {
			allInstances = ListLibrary.newArrayList();
		}
		return allInstances;
	}

	@Override
	public String toString() {
		return format("LoopMonitor(%s)", loop.toString());
	}
	
	private While loop;
	private int instanceID;
	private int threshold;
	private int topRecord;
	private int breakExits;
	private int returnExits;
	private int numberOfRecords;
	private Integer lastRecordedValue;
	private Map<Integer, Integer> recordFrequencies;
	
	/** XXX This causes memory leaks **/
	private static List<LoopMonitor> allInstances;
}
