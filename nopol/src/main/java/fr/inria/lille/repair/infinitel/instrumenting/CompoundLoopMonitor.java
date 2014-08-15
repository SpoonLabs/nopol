package fr.inria.lille.repair.infinitel.instrumenting;

import static java.lang.String.format;

import java.util.Collection;
import java.util.Map;

import fr.inria.lille.commons.collections.Bag;
import fr.inria.lille.commons.collections.ListLibrary;
import fr.inria.lille.commons.collections.SetLibrary;
import fr.inria.lille.commons.trace.RuntimeValues;
import fr.inria.lille.repair.infinitel.loop.While;

public class CompoundLoopMonitor {
	
	public CompoundLoopMonitor(Number threshold, Map<While, LoopMonitor> submonitors, Map<While, RuntimeValues> runtimeValues) {
		this.threshold = threshold.intValue();
		this.runtimeValues = runtimeValues;
		this.submonitors = submonitors;
	}

	public Collection<While> allLoops() {
		return submonitors().keySet();
	}
	
	public Collection<While> loopsWithBreak() {
		Collection<While> loops = ListLibrary.newArrayList();
		for (While loop : allLoops()) {
			if (loop.hasBreaks()) {
				loops.add(loop);
			}
		}
		return loops;
	}
	
	public Collection<While> loopsWithReturn() {
		Collection<While> loops = ListLibrary.newArrayList();
		for (While loop : allLoops()) {
			if (loop.hasReturns()) {
				loops.add(loop);
			}
		}
		return loops;
	}
	
	public Collection<While> loopsWithBreakAndReturn() {
		Collection<While> loops = ListLibrary.newArrayList();
		for (While loop : loopsWithReturn()) {
			if (loop.hasBreaks()) {
				loops.add(loop);
			}
		}
		return loops;
	}
	
	public Collection<While> loopsWithoutBodyExit() {
		Collection<While> loops = ListLibrary.newArrayList();
		for (While loop : allLoops()) {
			if (! (loop.hasBodyExit())) {
				loops.add(loop);
			}
		}
		return loops;
	}
	
	public Collection<While> loopsReachingThreshold() {
		Collection<While> loops = SetLibrary.newHashSet();	
		for (While loop : allLoops()) {
			if (monitorOf(loop).thresholdWasReached()) {
				loops.add(loop);
			}
		}
		return loops;
	}
	
	public Integer lastRecordIn(While loop) {
		return monitorOf(loop).lastRecordedValue();
	}
	
	public int numberOfRecordsIn(While loop) {
		return monitorOf(loop).numberOfRecords();
	}
	
	public int numberOfBreakExitsIn(While loop) {
		return monitorOf(loop).numberOfBreakExits();
	}
	
	public int numberOfReturnExitsIn(While loop) {
		return monitorOf(loop).numberOfReturnExits();
	}
	
	public Bag<Integer> exitRecordsOf(While loop) {
		return monitorOf(loop).exitRecords();
	}
	
	public Bag<Integer> breakRecordsOf(While loop) {
		return monitorOf(loop).breakRecords();
	}
	
	public Bag<Integer> returnRecordsOf(While loop) {
		return monitorOf(loop).returnRecords();
	}
	
	public int topRecordIn(While loop) {
		return monitorOf(loop).topRecord();
	}
	
	public Number thresholdOf(While loop) {
		return monitorOf(loop).threshold();
	}
	
	public Number setThresholdOf(While loop, Number newThreshold) {
		return monitorOf(loop).setThreshold(newThreshold);
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
	
	public RuntimeValues runtimeValuesOf(While loop) {
		return runtimeValues().get(loop);
	}
	
	public int threshold() {
		return threshold;
	}
	
	private LoopMonitor monitorOf(While loop) {
		return submonitors().get(loop);
	}
	
	private Map<While, LoopMonitor> submonitors() {
		return submonitors;
	}
	
	private Map<While, RuntimeValues> runtimeValues() {
		return runtimeValues;
	}
	
	@Override
	public String toString() {
		return format("CompoundLoopMonitor[threshold=%d][%d loops]", threshold(), submonitors().size());
	}
	
	private int threshold;
	private Map<While, LoopMonitor> submonitors;
	private Map<While, RuntimeValues> runtimeValues;
}