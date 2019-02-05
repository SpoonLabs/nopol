package fr.inria.lille.repair.synthesis.collect;

import com.sun.jdi.*;
import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.common.Candidates;
import fr.inria.lille.repair.expression.Expression;
import fr.inria.lille.repair.expression.factory.AccessFactory;
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

    private final NopolContext nopolContext;

    public SpoonElementsCollector(Set<CtTypedElement> elements, NopolContext nopolContext) {
        this.elements = elements;
        this.nopolContext = nopolContext;
    }

    /**
     * Collect the runtime value of literal and variable
     * @param threadRef the reference to the runtime environment
     * @return
     */
    public Candidates collect(ThreadReference threadRef) {
        Candidates candidates = new Candidates();
        Iterator<CtTypedElement> it = elements.iterator();
        try {
            StackFrame stackFrame = threadRef.frame(0);
            while (it.hasNext()) {
                CtElement ctElement = it.next();
                try {
                    if (ctElement instanceof CtLiteral) {
                        CtLiteral ctLiteral = (CtLiteral) ctElement;
                        Object value = ctLiteral.getValue();
                        if (value == null) {
                            continue;
                        }
                        Expression expression = AccessFactory.literal(value, nopolContext);
                        logger.debug("[data] " + expression + "=" + expression.getValue());
                        candidates.add(expression);
                    } else if (ctElement instanceof CtVariableAccess) {
                        CtVariableAccess ctVariableAccess = (CtVariableAccess) ctElement;
                        LocalVariable localVariable = stackFrame.visibleVariableByName(ctVariableAccess.toString());
                        if (localVariable == null) {
                            continue;
                        }
                        Value value = stackFrame.getValue(localVariable);
                        Expression expression = AccessFactory.variable(localVariable.name(), value, nopolContext);
                        logger.debug("[data] " + expression + "=" + expression.getValue());
                        candidates.add(expression);
                    }
                } catch (Exception e) {
                    logger.debug("Unable to collect the runtime value for " + ctElement, e);
                }
            }
        } catch (Exception e) {
            logger.debug("Unable to access the stack frame", e);
        }
        return candidates;
    }
}
