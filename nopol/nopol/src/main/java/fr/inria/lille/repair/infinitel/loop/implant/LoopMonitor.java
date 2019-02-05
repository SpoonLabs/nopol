package fr.inria.lille.repair.infinitel.loop.implant;

import fr.inria.lille.repair.infinitel.loop.While;
import xxl.java.container.classic.MetaList;
import xxl.java.container.various.Bag;
import xxl.java.support.GlobalToggle;

import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.asList;

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
        errorRecords().clear();
        breakRecords().clear();
        returnRecords().clear();
    }

    @Override
    protected String globallyAccessibleName() {
        return format("%s.instance(%d)", getClass().getName(), instanceID());
    }

    public String invocationOnMonitoringEnd(String counterName) {
        return invocationMessageFor("recordExit", asList(int.class), asList(counterName));
    }

    public String invocationOnLoopConditionEvaluation(String originalCondition, String counterName) {
        return invocationMessageFor("canEnterLoop", asList(boolean.class, int.class), asList(originalCondition, counterName));
    }

    public String invocationOnLoopError(String counterName) {
        return invocationMessageFor("recordErrorExit", asList(int.class), asList(counterName));
    }

    public String invocationOnLoopBreak(String counterName) {
        return invocationMessageFor("recordBreakExit", asList(int.class), asList(counterName));
    }

    public String invocationOnLoopReturn(String counterName) {
        return invocationMessageFor("recordReturnExit", asList(int.class), asList(counterName));
    }

    public boolean canEnterLoop(boolean originalCondition, int completedIterations) {
        boolean canEnterLoop = doorkeeper().canEnterLoop(originalCondition, completedIterations, invocationNumber());
        if (originalCondition && !canEnterLoop && infiniteInvocation() == null) {
            setInfiniteInvocation(invocationNumber());
        }
        return canEnterLoop;
    }

    public void recordExit(int loopEntrances) {
        exitRecords().add(loopEntrances);
    }

    public void recordBreakExit(int loopEntrances) {
        breakRecords().add(loopEntrances);
    }

    public void recordErrorExit(int loopEntrances) {
        errorRecords().add(loopEntrances);
        recordExit(loopEntrances);
    }

    public void recordReturnExit(int loopEntrances) {
        returnRecords().add(loopEntrances);
        recordExit(loopEntrances);
    }

    private int invocationNumber() {
        return exitRecords().size() + 1;
    }

    public LoopStatistics asExportable() {
        LoopStatistics stats = new LoopStatistics();
        stats.setExitRecords(exitRecords().copy());
        stats.setErrorRecords(errorRecords().copy());
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
        errorRecords = Bag.newHashBag();
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

    private Bag<Integer> errorRecords() {
        return errorRecords;
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
    private Bag<Integer> errorRecords;
    private Bag<Integer> breakRecords;
    private Bag<Integer> returnRecords;
    private LoopDoorkeeper doorkeeper;
    private Integer infiniteInvocation;

    private static List<LoopMonitor> allInstances;
}
