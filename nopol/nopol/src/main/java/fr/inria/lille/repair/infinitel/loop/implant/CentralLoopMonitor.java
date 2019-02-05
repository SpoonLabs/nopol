package fr.inria.lille.repair.infinitel.loop.implant;

import fr.inria.lille.commons.trace.RuntimeValues;
import fr.inria.lille.repair.infinitel.loop.While;

import java.util.Collection;
import java.util.Map;

import static java.lang.String.format;

public class CentralLoopMonitor {

    public CentralLoopMonitor(Number threshold, Map<While, LoopMonitor> submonitors, Map<While, RuntimeValues<Boolean>> runtimeValues) {
        this.threshold = threshold.intValue();
        this.runtimeValues = runtimeValues;
        this.submonitors = submonitors;
    }

    public int threshold() {
        return threshold;
    }

    public Collection<While> allLoops() {
        return submonitors().keySet();
    }

    public int thresholdOf(While loop, int invocation) {
        return monitorOf(loop).thresholdFor(invocation);
    }

    public int setThresholdOf(While loop, int newThreshold, int invocation) {
        return monitorOf(loop).setThreshold(invocation, newThreshold);
    }

    public LoopStatistics statisticsIn(While loop) {
        return monitorOf(loop).asExportable();
    }

    public void disableAll() {
        disable(allLoops());
    }

    public void disable(Collection<While> loops) {
        for (While loop : loops) {
            disable(loop);
        }
    }

    public boolean disable(While loop) {
        return monitorOf(loop).disable();
    }

    public void enableAll() {
        enable(allLoops());
    }

    public void enable(Collection<While> loops) {
        for (While loop : loops) {
            enable(loop);
        }
    }

    public boolean enable(While loop) {
        return monitorOf(loop).enable();
    }

    public boolean enableTracing(While loop) {
        return runtimeValuesOf(loop).enable();
    }

    public boolean disableTracing(While loop) {
        return runtimeValuesOf(loop).disable();
    }

    protected LoopMonitor monitorOf(While loop) {
        return submonitors().get(loop);
    }

    protected RuntimeValues<Boolean> runtimeValuesOf(While loop) {
        return runtimeValues().get(loop);
    }

    private Map<While, LoopMonitor> submonitors() {
        return submonitors;
    }

    private Map<While, RuntimeValues<Boolean>> runtimeValues() {
        return runtimeValues;
    }

    @Override
    public String toString() {
        return format("CompoundLoopMonitor[threshold: %d][%d loops]", threshold(), submonitors().size());
    }

    private int threshold;
    private Map<While, LoopMonitor> submonitors;
    private Map<While, RuntimeValues<Boolean>> runtimeValues;
}