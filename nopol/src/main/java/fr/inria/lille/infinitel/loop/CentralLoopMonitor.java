package fr.inria.lille.infinitel.loop;

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

public class CentralLoopMonitor extends AbstractProcessor<CtWhile> {

	public CentralLoopMonitor(Number threshold) {
		this.threshold = threshold.intValue();
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
		LoopIterationCounter auditor = LoopIterationCounter.newInstance(threshold());
		LoopInstrumenter.instrument(loopStatement, auditor);
		counters().put(position, auditor);
	}
	
	public Integer lastRecordIn(SourcePosition loopPosition) {
		return counterOf(loopPosition).lastRecordedValue();
	}
	
	public int numberOfRecords(SourcePosition loopPosition) {
		return counterOf(loopPosition).numberOfRecords();
	}
	
	public void disableAll() {
		for (SourcePosition position : counters().keySet()) {
			disable(position);
		}
	}
	
	public boolean disable(SourcePosition position) {
		return counterOf(position).disable();
	}
	
	public void enableAll() {
		for (SourcePosition position : counters().keySet()) {
			enable(position);
		}
	}
	
	public boolean enable(SourcePosition position) {
		return counterOf(position).enable();
	}
	
	public Number setThresholdOf(SourcePosition position, Number newThreshold) {
		return counterOf(position).setThreshold(newThreshold);
	}
	
	public Collection<SourcePosition> loopsAboveThreshold() {
		Collection<SourcePosition> positions = SetLibrary.newHashSet();	
		for (Entry<SourcePosition, LoopIterationCounter> entry : counters().entrySet()) {
			if (entry.getValue().thresholdWasReached()) {
				positions.add(entry.getKey());
			}
		}
		return positions;
	}
	
	public int threshold() {
		return threshold;
	}
	
	private LoopIterationCounter counterOf(SourcePosition position) {
		return counters().get(position);
	}
	
	private Map<SourcePosition, LoopIterationCounter> counters() {
		return counters;
	}
	
	private int threshold;
	private Map<SourcePosition, LoopIterationCounter> counters;
}