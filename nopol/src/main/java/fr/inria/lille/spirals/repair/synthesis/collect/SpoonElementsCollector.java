package fr.inria.lille.spirals.repair.synthesis.collect;

import com.sun.jdi.*;
import fr.inria.lille.repair.common.config.Config;
import fr.inria.lille.spirals.repair.commons.Candidates;
import fr.inria.lille.spirals.repair.expression.Expression;
import fr.inria.lille.spirals.repair.expression.factory.AccessFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtTypedElement;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by Thomas Durieux on 24/03/15.
 */
public class SpoonElementsCollector {


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Set<CtTypedElement> elements;

    private final Config config;

    public SpoonElementsCollector(Set<CtTypedElement> elements, Config config) {
        this.elements = elements;
        this.config = config;
    }

    public Candidates collect(ThreadReference threadRef) {
        Candidates candidates = new Candidates();
        Iterator<CtTypedElement> it = elements.iterator();
        try {
            StackFrame stackFrame = threadRef.frame(0);
            while (it.hasNext()) {
                CtElement ctElement = it.next();
                if (ctElement instanceof CtLiteral) {
                    CtLiteral ctLiteral = (CtLiteral) ctElement;
                    Object value = ctLiteral.getValue();
                    Class type = null;
                    if (value != null) {
                        type = value.getClass();
                    } else {
                        continue;
                    }
                    Expression expression = AccessFactory.literal(value, config);
                    logger.debug("[data] " + expression + "=" + expression.getValue());
                    candidates.add(expression);
                } else if (ctElement instanceof CtVariableAccess) {
                    CtVariableAccess ctVariableAccess = (CtVariableAccess) ctElement;
                    LocalVariable localVariable = stackFrame.visibleVariableByName(ctVariableAccess.toString());
                    if (localVariable == null) {
                        continue;
                    }
                    Value value = stackFrame.getValue(localVariable);
                    Expression expression = AccessFactory.variable(localVariable.name(), value, config);
                    logger.debug("[data] " + expression + "=" + expression.getValue());
                    candidates.add(expression);
                }
            }
        } catch (IncompatibleThreadStateException e) {
            e.printStackTrace();
        } catch (AbsentInformationException e) {
            e.printStackTrace();
        }
        return candidates;
    }
}
