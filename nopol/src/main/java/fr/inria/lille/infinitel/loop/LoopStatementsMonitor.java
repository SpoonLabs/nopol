package fr.inria.lille.infinitel.loop;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtWhile;
import spoon.reflect.cu.SourcePosition;
import fr.inria.lille.commons.collections.MapLibrary;
import fr.inria.lille.commons.collections.SetLibrary;

public class LoopStatementsMonitor extends AbstractProcessor<CtWhile> {

	public LoopStatementsMonitor(Number threshold) {
		this.threshold = threshold.intValue();
		auditors = MapLibrary.newHashMap();
	}

	@Override
	public void process(CtWhile loopStatement) {
		SourcePosition position = loopStatement.getPosition();
		IterationAuditor auditor = IterationAuditor.newInstance(position, threshold());
		auditor.process(loopStatement);
		auditors().put(position, auditor);
		enable(position);
	}
	
	public Collection<SourcePosition> loopsAboveThreshold() {
		Collection<SourcePosition> positions = SetLibrary.newHashSet();	
		for (Entry<SourcePosition, IterationAuditor> entry : auditors().entrySet()) {
			if (entry.getValue().loopReachedThreshold()) {
				positions.add(entry.getKey());
			}
		}
		return positions;
	}
	
	public void disableAll() {
		for (SourcePosition position : auditors().keySet()) {
			disable(position);
		}
	}
	
	public boolean disable(SourcePosition position) {
		return auditorOf(position).disable();
	}
	
	public void enableAll() {
		for (SourcePosition position : auditors().keySet()) {
			enable(position);
		}
	}
	
	public boolean enable(SourcePosition position) {
		return auditorOf(position).enable();
	}
	
	public boolean isEnabled(SourcePosition position) {
		return auditorOf(position).isDisabled();
	}
	
	public Number setThresholdOf(SourcePosition position, Number newThreshold) {
		return auditorOf(position).setThreshold(newThreshold);
	}
	
	public List<Integer> iterationRecordOf(SourcePosition position) {
		return auditorOf(position).iterationsRecord();
	}
	
	public int threshold() {
		return threshold;
	}
	
	private IterationAuditor auditorOf(SourcePosition position) {
		return auditors().get(position);
	}
	
	private Map<SourcePosition, IterationAuditor> auditors() {
		return auditors;
	}
	
	private int threshold;
	private Map<SourcePosition, IterationAuditor> auditors;
}