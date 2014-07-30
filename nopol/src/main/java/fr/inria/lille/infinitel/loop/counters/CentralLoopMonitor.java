package fr.inria.lille.infinitel.loop.counters;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtWhile;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtMethod;
import fr.inria.lille.commons.collections.MapLibrary;
import fr.inria.lille.commons.collections.SetLibrary;
import fr.inria.lille.commons.spoon.SpoonLibrary;
import fr.inria.lille.infinitel.loop.LoopInstrumenter;

public class CentralLoopMonitor extends AbstractProcessor<CtWhile> {

	public CentralLoopMonitor(Number threshold, LoopCounterFactory counterFactory) {
		this.threshold = threshold.intValue();
		this.counterFactory = counterFactory;
		counters = MapLibrary.newHashMap();
	}
	
	@Override
	public boolean isToBeProcessed(CtWhile loopStatement) {
		CtMethod<?> correspondingMethod = loopStatement.getParent(CtMethod.class);
		if (SpoonLibrary.isLastStatementOfMethod(loopStatement)) {
			return SpoonLibrary.isVoidType(correspondingMethod.getType());
		}
		return true;
	}

	@Override
	public void process(CtWhile loopStatement) {
		SourcePosition position = loopStatement.getPosition();
		LoopEntrancesCounter auditor = counterFactory().newCounter(threshold());
		LoopInstrumenter.instrument(loopStatement, auditor);
		counters().put(position, auditor);
	}
	
	public Collection<SourcePosition> allLoops() {
		return counters().keySet();
	}
	
	public Collection<SourcePosition> loopsReachingThreshold() {
		Collection<SourcePosition> positions = SetLibrary.newHashSet();	
		for (Entry<SourcePosition, LoopEntrancesCounter> entry : counters().entrySet()) {
			if (entry.getValue().thresholdWasReached()) {
				positions.add(entry.getKey());
			}
		}
		return positions;
	}
	
	public Integer lastRecordIn(SourcePosition loopPosition) {
		return counterOf(loopPosition).lastRecordedValue();
	}
	
	public int numberOfRecords(SourcePosition loopPosition) {
		return counterOf(loopPosition).numberOfRecords();
	}
	
	public int topRecordIn(SourcePosition loopPosition) {
		return counterOf(loopPosition).topRecord();
	}
	
	public Number thresholdOf(SourcePosition position) {
		return counterOf(position).threshold();
	}
	
	public Number setThresholdOf(SourcePosition position, Number newThreshold) {
		return counterOf(position).setThreshold(newThreshold);
	}
	
	public void disableAll() {
		disable(allLoops());
	}
	
	public void disable(Collection<SourcePosition> loopPositions) {
		for (SourcePosition position : loopPositions) {
			disable(position);
		}
	}
	
	public boolean disable(SourcePosition position) {
		return counterOf(position).disable();
	}
	
	public void enableAll() {
		enable(allLoops());
	}
	
	public void enable(Collection<SourcePosition> loopPositions) {
		for (SourcePosition position : loopPositions) {
			enable(position);
		}
	}
	
	public boolean enable(SourcePosition position) {
		return counterOf(position).enable();
	}
	
	public LoopEntrancesCounter counterOf(SourcePosition position) {
		return counters().get(position);
	}
	
	public int threshold() {
		return threshold;
	}
	
	private Map<SourcePosition, LoopEntrancesCounter> counters() {
		return counters;
	}
	
	private LoopCounterFactory counterFactory() {
		return counterFactory;
	}
	
	private int threshold;
	private LoopCounterFactory counterFactory;
	private Map<SourcePosition, LoopEntrancesCounter> counters;
}