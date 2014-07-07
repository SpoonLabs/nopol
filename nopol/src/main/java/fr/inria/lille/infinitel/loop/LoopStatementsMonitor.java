package fr.inria.lille.infinitel.loop;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtWhile;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtMethod;
import fr.inria.lille.commons.collections.MapLibrary;
import fr.inria.lille.commons.collections.SetLibrary;
import fr.inria.lille.commons.spoon.SpoonLibrary;

public class LoopStatementsMonitor extends AbstractProcessor<CtWhile> {

	public LoopStatementsMonitor(Number threshold) {
		this.threshold = threshold.intValue();
		auditors = MapLibrary.newHashMap();
		stateProcessor = new LoopStateProcessor();
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
		IterationAuditor auditor = IterationAuditor.newInstance(position, threshold());
		stateProcessor().process(loopStatement, auditor);
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
	
	public Number setThresholdOf(SourcePosition position, Number newThreshold) {
		return auditorOf(position).setThreshold(newThreshold);
	}
	
	public void resetRecordOf(SourcePosition position) {
		auditorOf(position).resetRecord();
	}
	
	public List<Integer> iterationRecordOf(SourcePosition position) {
		return auditorOf(position).iterationsRecord();
	}
	
	public int threshold() {
		return threshold;
	}
	
	private LoopStateProcessor stateProcessor() {
		return stateProcessor;
	}
	
	public IterationAuditor auditorOf(SourcePosition position) {
		return auditors().get(position);
	}
	
	private Map<SourcePosition, IterationAuditor> auditors() {
		return auditors;
	}
	
	private int threshold;
	private LoopStateProcessor stateProcessor;
	private Map<SourcePosition, IterationAuditor> auditors;
}