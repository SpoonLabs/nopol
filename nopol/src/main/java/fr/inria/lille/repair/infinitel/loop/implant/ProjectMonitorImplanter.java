package fr.inria.lille.repair.infinitel.loop.implant;

import java.util.Map;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtWhile;
import spoon.reflect.declaration.CtMethod;
import xxl.java.container.classic.MetaMap;
import fr.inria.lille.commons.spoon.SpoonedProject;
import fr.inria.lille.commons.spoon.util.SpoonReferenceLibrary;
import fr.inria.lille.commons.spoon.util.SpoonStatementLibrary;
import fr.inria.lille.commons.trace.RuntimeValues;
import fr.inria.lille.repair.ProjectReference;
import fr.inria.lille.repair.infinitel.InfinitelConfiguration;
import fr.inria.lille.repair.infinitel.loop.While;

public class ProjectMonitorImplanter extends AbstractProcessor<CtWhile> {

	public static MonitoringTestExecutor implanted(ProjectReference project, InfinitelConfiguration configuration) {
		ProjectMonitorImplanter implanter = new ProjectMonitorImplanter(configuration.iterationsThreshold());
		SpoonedProject spoonedProject = new SpoonedProject(project.sourceFile(), project.classpath());
		spoonedProject.process(implanter);
		ClassLoader classLoader = spoonedProject.dumpedToClassLoader();
		CentralLoopMonitor loopMonitor = implanter.implant();
		MonitoringTestExecutor testExecutor = new MonitoringTestExecutor(classLoader, loopMonitor);
		return testExecutor;
	}
	
	public ProjectMonitorImplanter(int threshold) {
		this.threshold = threshold;
		submonitors = MetaMap.newHashMap();
		runtimeValues = MetaMap.newHashMap();
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
		RuntimeValues<Boolean> newRuntimeValues = RuntimeValues.newInstance();
		submonitors().put(loop, loopMonitor);
		runtimeValues().put(loop, newRuntimeValues);
		LoopInstrumenter.instrument(loopMonitor, newRuntimeValues);
	}
	
	public CentralLoopMonitor implant() {
		CentralLoopMonitor monitor = new CentralLoopMonitor(threshold(), submonitors(), runtimeValues());
		return monitor;
	}
	
	private int threshold() {
		return threshold;
	}
	
	private Map<While, LoopMonitor> submonitors() {
		return submonitors;
	}
	
	private Map<While, RuntimeValues<Boolean>> runtimeValues() {
		return runtimeValues;
	}
	
	private int threshold;
	private Map<While, LoopMonitor> submonitors;
	private Map<While, RuntimeValues<Boolean>> runtimeValues;
}
