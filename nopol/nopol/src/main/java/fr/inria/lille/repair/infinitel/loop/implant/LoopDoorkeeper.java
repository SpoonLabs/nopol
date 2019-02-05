package fr.inria.lille.repair.infinitel.loop.implant;

import java.util.Map;

import static xxl.java.container.classic.MetaMap.getIfAbsent;
import static xxl.java.container.classic.MetaMap.newHashMap;

public class LoopDoorkeeper {

    public LoopDoorkeeper(Number threshold) {
        this.threshold = threshold.intValue();
        thresholds = newHashMap();
    }

    public boolean canEnterLoop(boolean originalCondition, int completedIterations, int invocation) {
        return originalCondition && completedIterations < thresholdFor(invocation);
    }

    public int thresholdFor(int invocation) {
        return getIfAbsent(thresholds(), invocation, threshold());
    }

    public int setThreshold(int invocation, int threshold) {
        int oldValue = thresholdFor(invocation);
        thresholds().put(invocation, threshold);
        return oldValue;
    }

    public int threshold() {
        return threshold;
    }

    private Map<Integer, Integer> thresholds() {
        return thresholds;
    }

    private int threshold;
    private Map<Integer, Integer> thresholds;
}
