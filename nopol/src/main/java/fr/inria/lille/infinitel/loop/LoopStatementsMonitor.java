package fr.inria.lille.infinitel.loop;

import java.util.Collection;
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
			entry.getValue().resetRecord();
		}
		return positions;
	}
	
	public void disable(SourcePosition position) {
		auditors().get(position).disable();
	}
	
	public void enable(SourcePosition position) {
		auditors().get(position).enable();
	}
	
	public boolean isEnabled(SourcePosition position) {
		return auditorIn(position).isDisabled();
	}
	
	public IterationAuditor auditorIn(SourcePosition position) {
		return auditors().get(position);
	}
	
	private Map<SourcePosition, IterationAuditor> auditors() {
		return auditors;
	}
	
	public int threshold() {
		return threshold;
	}
	
	private int threshold;
	private Map<SourcePosition, IterationAuditor> auditors;
}