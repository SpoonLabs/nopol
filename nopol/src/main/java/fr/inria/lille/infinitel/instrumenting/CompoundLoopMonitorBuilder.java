package fr.inria.lille.infinitel.instrumenting;

import java.util.Map;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtWhile;
import spoon.reflect.declaration.CtMethod;
import fr.inria.lille.commons.collections.MapLibrary;
import fr.inria.lille.commons.spoon.util.SpoonReferenceLibrary;
import fr.inria.lille.commons.spoon.util.SpoonStatementLibrary;
import fr.inria.lille.infinitel.loop.While;

public class CompoundLoopMonitorBuilder extends AbstractProcessor<CtWhile> {

	public CompoundLoopMonitorBuilder(int threshold) {
		this.threshold = threshold;
		submonitors = MapLibrary.newHashMap();
	}
	
	@Override
	public boolean isToBeProcessed(CtWhile loopStatement) {
		CtMethod<?> correspondingMethod = loopStatement.getParent(CtMethod.class);
		if (SpoonStatementLibrary.isLastStatementOfMethod(loopStatement)) {
			return SpoonReferenceLibrary.isVoidType(correspondingMethod.getType());
		}
		return true;
	}

	@Override
	public void process(CtWhile loopStatement) {
		While loop = new While(loopStatement);
		LoopMonitor loopMonitor = LoopMonitor.newInstance(loop, threshold());
		submonitors().put(loop, loopMonitor);
	}
	
	public CompoundLoopMonitor result() {
		return new CompoundLoopMonitor(threshold(), submonitors());
	}
	
	private int threshold() {
		return threshold;
	}
	
	private Map<While, LoopMonitor> submonitors() {
		return submonitors;
	}
	
	private int threshold;
	private Map<While, LoopMonitor> submonitors;
}
