package fr.inria.lille.repair.nopol.ifmetric;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtIf;

import java.util.HashMap;

public class IfCollectorProcessor extends AbstractProcessor<CtIf> {

    @Override
    public void process(CtIf element) {
        String className = element.getPosition().getCompilationUnit().getMainType().getSimpleName();
        int line = element.getPosition().getLine();
        IfMetric.getExecutedIf().put(IfPosition.create(className, line), new HashMap<String, IfBranch>());
    }

}
