package fr.inria.lille.repair.infinitel.loop.implant;

import static java.lang.String.format;

import java.util.List;

import xxl.java.container.classic.MetaList;
import xxl.java.container.various.Bag;
import xxl.java.support.GlobalToggle;
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
		setInfiniteInvocation(null);
		exitRecords().clear();
		breakRecords().clear();
		returnRecords().clear();
	}
	
	@Override
	protected String globallyAccessibleName() {
		return format("%s.instance(%d)", getClass().getName(), instanceID());
	}

	public String invocationOnLoopConditionEvaluation(String originalCondition, String counterName) {
		return  globallyAccessibleName() + format(".canEnterLoop(%s, %s)", originalCondition, counterName);
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

	public synchronized boolean canEnterLoop(boolean originalCondition, int completedIterations) {
		boolean canEnterLoop = doorkeeper().canEnterLoop(originalCondition, completedIterations, invocationNumber());
		if (originalCondition && ! canEnterLoop && infiniteInvocation() == null) {
			setInfiniteInvocation(invocationNumber());
		}
		return canEnterLoop;
	}
	
	public synchronized void recordEntrancesToExit(int loopEntrances) {
		exitRecords().add(loopEntrances);
	}
	
	public synchronized void recordBreakExit(int loopEntrances) {
		breakRecords().add(loopEntrances);
	}
	
	public synchronized void recordReturnExit(int loopEntrances) {
		returnRecords().add(loopEntrances);
		recordEntrancesToExit(loopEntrances);
	}
	
	private int invocationNumber() {
		return exitRecords().size() + 1;
	}
	
	public LoopStatistics asExportable() {
		LoopStatistics stats = new LoopStatistics(loop());
		stats.setExitRecords(exitRecords().copy());
		stats.setBreakRecords(breakRecords().copy());
		stats.setReturnRecords(returnRecords().copy());
		stats.setInfiniteInvocation(infiniteInvocation());
		return stats;
	}

	protected LoopMonitor(While loop, Number threshold, int instanceID) {
		super();
		this.loop = loop;
		this.instanceID = instanceID;
		exitRecords = Bag.newHashBag();
		breakRecords = Bag.newHashBag();
		returnRecords = Bag.newHashBag();
		doorkeeper = new LoopDoorkeeper(threshold);
	}
	
	protected While loop() {
		return loop;
	}
	
	protected Integer instanceID() {
		return instanceID;
	}
	
	protected int thresholdFor(int invocation) {
		return doorkeeper().thresholdFor(invocation);
	}

	protected int setThreshold(int invocation, int threshold) {
		return doorkeeper().setThreshold(invocation, threshold);
	}
	
	protected LoopDoorkeeper doorkeeper() {
		return doorkeeper;
	}
	
	private Integer infiniteInvocation() {
		return infiniteInvocation;
	}
	
	private Bag<Integer> exitRecords() {
		return exitRecords;
	}
	
	private Bag<Integer> breakRecords() {
		return breakRecords;
	}
	
	private Bag<Integer> returnRecords() {
		return returnRecords;
	}
	
	private void setInfiniteInvocation(Integer infiniteInvocation) {
		this.infiniteInvocation = infiniteInvocation;
	}
	
	private static List<LoopMonitor> allInstances() {
		if (allInstances == null) {
			allInstances = MetaList.newArrayList();
		}
		return allInstances;
	}
	
	private static int numberOfInstances() {
		return allInstances().size();
	}

	@Override
	public String toString() {
		return format("LoopMonitor(%s)", loop().toString());
	}
	
	private While loop;
	private int instanceID;
	private Bag<Integer> exitRecords;
	private Bag<Integer> breakRecords;
	private Bag<Integer> returnRecords;
	private LoopDoorkeeper doorkeeper;
	private Integer infiniteInvocation;
	
	/** XXX This causes memory leaks **/
	private static List<LoopMonitor> allInstances;
}
