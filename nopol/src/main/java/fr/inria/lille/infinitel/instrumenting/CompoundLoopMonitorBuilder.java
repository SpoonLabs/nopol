package fr.inria.lille.infinitel.instrumenting;

import java.util.Map;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtWhile;
import spoon.reflect.declaration.CtMethod;
import fr.inria.lille.commons.collections.MapLibrary;
import fr.inria.lille.commons.spoon.util.SpoonReferenceLibrary;
import fr.inria.lille.commons.spoon.util.SpoonStatementLibrary;
import fr.inria.lille.commons.trace.RuntimeValues;
import fr.inria.lille.infinitel.loop.While;

public class CompoundLoopMonitorBuilder extends AbstractProcessor<CtWhile> {

	public CompoundLoopMonitorBuilder(int threshold) {
		this.threshold = threshold;
		submonitors = MapLibrary.newHashMap();
		runtimeValues = MapLibrary.newHashMap();
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
		RuntimeValues newRuntimeValues = RuntimeValues.newInstance();
		submonitors().put(loop, loopMonitor);
		runtimeValues().put(loop, newRuntimeValues);
		LoopInstrumenter.instrument(loopMonitor, newRuntimeValues);
	}
	
	public CompoundLoopMonitor result() {
		if (newMonitor == null) {
			newMonitor = new CompoundLoopMonitor(threshold(), submonitors(), runtimeValues());
		}
		return newMonitor;
	}
	
	private int threshold() {
		return threshold;
	}
	
	private Map<While, LoopMonitor> submonitors() {
		return submonitors;
	}
	
	private Map<While, RuntimeValues> runtimeValues() {
		return runtimeValues;
	}
	
	private int threshold;
	private CompoundLoopMonitor newMonitor;
	private Map<While, LoopMonitor> submonitors;
	private Map<While, RuntimeValues> runtimeValues;
}
