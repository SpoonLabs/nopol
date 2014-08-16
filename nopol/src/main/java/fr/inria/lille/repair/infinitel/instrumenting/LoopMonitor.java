package fr.inria.lille.repair.infinitel.instrumenting;

import static java.lang.String.format;

import java.util.List;

import xxl.java.extensions.collection.Bag;
import xxl.java.extensions.collection.ListLibrary;
import xxl.java.extensions.support.GlobalToggle;
import fr.inria.lille.repair.infinitel.loop.While;

public class LoopMonitor extends GlobalToggle {

	protected static LoopMonitor newInstance(While loop, Number threshold) {
		int instanceNumber = numberOfInstances();
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
		setLastRecordedValue(null);
		exitRecords().clear();
		breakRecords().clear();
		returnRecords().clear();
	}
	
	@Override
	protected String globallyAccessibleName() {
		return format("%s.instance(%d)", getClass().getName(), instanceID());
	}

	public String invocationOnLoopConditionEvaluation(String counterName) {
		return  globallyAccessibleName() + format(".canEnterLoop(%s)", counterName);
	}
	
	public String invocationOnLoopBreak(String counterName) {
		return globallyAccessibleName() + format(".recordBreakExit(%s)", counterName);
	}
	
	public String invocationOnLoopReturn(String counterName) {
		return globallyAccessibleName() + format(".recordReturnExit(%s)", counterName);
	}
	
	public String invocationOnFirstStatementAfterLoop(String counterName) {
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
			exitRecords().add(loopEntrances);
		}
	}
	
	public void recordBreakExit(int loopEntrances) {
		synchronized (this) {
			breakRecords().add(loopEntrances);
		}
	}
	
	public void recordReturnExit(int loopEntrances) {
		synchronized (this) {
			returnRecords().add(loopEntrances);
			recordEntrancesToExit(loopEntrances);
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
		exitRecords = Bag.newHashBag();
		breakRecords = Bag.newHashBag();
		returnRecords = Bag.newHashBag();
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
	
	protected Integer instanceID() {
		return instanceID;
	}
	
	protected int numberOfRecords() {
		return exitRecords().size();
	}
	
	protected int numberOfBreakExits() {
		return breakRecords().size();
	}
	
	protected int numberOfReturnExits() {
		return returnRecords().size();
	}
	
	protected Integer lastRecordedValue() {
		return lastRecordedValue;
	}
	
	protected Bag<Integer> exitRecords() {
		return exitRecords;
	}
	
	protected Bag<Integer> breakRecords() {
		return breakRecords;
	}
	
	protected Bag<Integer> returnRecords() {
		return returnRecords;
	}
	
	protected Number setThreshold(Number number) {
		int oldValue = threshold;
		threshold = number.intValue();
		return oldValue;
	}

	private void setTopRecord(int number) {
		topRecord = number;
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
	
	private static int numberOfInstances() {
		return allInstances().size();
	}

	@Override
	public String toString() {
		return format("LoopMonitor(%s)", loop.toString());
	}
	
	private While loop;
	private int instanceID;
	private int threshold;
	private int topRecord;
	private Integer lastRecordedValue;
	private Bag<Integer> exitRecords;
	private Bag<Integer> breakRecords;
	private Bag<Integer> returnRecords;
	
	/** XXX This causes memory leaks **/
	private static List<LoopMonitor> allInstances;
}
