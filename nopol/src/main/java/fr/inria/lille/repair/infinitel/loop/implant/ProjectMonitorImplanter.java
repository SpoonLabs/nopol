package fr.inria.lille.repair.infinitel.loop.implant;

import fr.inria.lille.commons.spoon.SpoonedProject;
import fr.inria.lille.commons.spoon.util.SpoonReferenceLibrary;
import fr.inria.lille.commons.spoon.util.SpoonStatementLibrary;
import fr.inria.lille.commons.trace.RuntimeValues;
import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.infinitel.InfinitelConfiguration;
import fr.inria.lille.repair.infinitel.loop.While;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtWhile;
import spoon.reflect.declaration.CtMethod;
import xxl.java.container.classic.MetaMap;

import java.util.Map;

public class ProjectMonitorImplanter extends AbstractProcessor<CtWhile> {

    public static MonitoringTestExecutor implanted(InfinitelConfiguration configuration, NopolContext nopolContext) {
        ProjectMonitorImplanter implanter = new ProjectMonitorImplanter(configuration.iterationsThreshold());
        SpoonedProject spoonedProject = new SpoonedProject(nopolContext.getProjectSources(), nopolContext);
        spoonedProject.process(implanter);
        ClassLoader classLoader = spoonedProject.dumpedToClassLoader();
        MonitoringTestExecutor testExecutor = new MonitoringTestExecutor(classLoader, implanter.implant(), nopolContext);
        return testExecutor;
    }

    public ProjectMonitorImplanter(int threshold) {
        this.threshold = threshold;
        submonitors = MetaMap.newHashMap();
        runtimeValues = MetaMap.newHashMap();
    }

    @Override
    public boolean isToBeProcessed(CtWhile loopStatement) {
        return true;
    }

    @Override
    public void process(CtWhile loopStatement) {
        While loop = newLoop(loopStatement);
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

    public boolean isUnbreakable(CtWhile loopStatement) {
        CtMethod<?> correspondingMethod = loopStatement.getParent(CtMethod.class);
        if (SpoonStatementLibrary.isLastStatementOfMethod(loopStatement)) {
            return !SpoonReferenceLibrary.isVoidType(correspondingMethod.getType());
        }
        return false;
    }

    protected While newLoop(CtWhile loopStatement) {
        While loop = new While(loopStatement);
        if (isUnbreakable(loopStatement)) {
            loop.setUnbreakable();
        }
        return loop;
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
