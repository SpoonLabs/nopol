package fr.inria.lille.infinitel.instrumenting;

import java.util.Collection;
import java.util.Map;

import fr.inria.lille.commons.collections.SetLibrary;
import fr.inria.lille.infinitel.loop.While;

public class CompoundLoopMonitor {

	public CompoundLoopMonitor(Number threshold, Map<While, LoopMonitor> submonitors) {
		this.threshold = threshold.intValue();
		this.submonitors = submonitors;
	}

	public Collection<While> allLoops() {
		return submonitors().keySet();
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
	
	public int numberOfRecords(While loop) {
		return monitorOf(loop).numberOfRecords();
	}
	
	public int topRecordIn(While loop) {
		return monitorOf(loop).topRecord();
	}
	
	public Number thresholdOf(While loop) {
		return monitorOf(loop).threshold();
	}
	
	public Map<Integer, Integer> recordFrequenciesOf(While loop) {
		return monitorOf(loop).recordFrequencies();
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
	
	public int threshold() {
		return threshold;
	}
	
	private LoopMonitor monitorOf(While loop) {
		return submonitors().get(loop);
	}
	
	private Map<While, LoopMonitor> submonitors() {
		return submonitors;
	}
	
	private int threshold;
	private Map<While, LoopMonitor> submonitors;
}