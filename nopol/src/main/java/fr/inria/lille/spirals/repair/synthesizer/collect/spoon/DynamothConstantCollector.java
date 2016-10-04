package fr.inria.lille.spirals.repair.synthesizer.collect.spoon;


import fr.inria.lille.repair.common.config.Config;
import fr.inria.lille.spirals.repair.commons.Candidates;
import fr.inria.lille.spirals.repair.expression.access.Literal;
import fr.inria.lille.spirals.repair.expression.factory.AccessFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtThrow;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;

public class DynamothConstantCollector extends DefaultConstantCollector {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Candidates candidates;
    private final String buggyMethod;
    private final Config config;


    public DynamothConstantCollector(Candidates candidates, String buggyMethod, Config config) {
        super();
        this.config = config;
        this.candidates = candidates;
        this.buggyMethod = buggyMethod;
    }

    @Override
    public boolean isToBeProcessed(CtLiteral candidate) {
        CtMethod parent = candidate.getParent(CtMethod.class);
        if (parent == null) {
            return false;
        }
        if (buggyMethod != null) {
            return parent.getSimpleName().equals(buggyMethod);
        }
        return true;
    }

    @Override
    public void process(CtLiteral ctLiteral) {
        if (ctLiteral.getValue() instanceof Boolean) {
            return;
        } else if (ctLiteral.getValue() instanceof Number) {
            if (ctLiteral.getValue().equals(1) ||
                    ctLiteral.getValue().equals(0)) {
                return;
            }
        }
        CtElement parent = ctLiteral.getParent(CtLocalVariable.class);
        if (parent != null) {
            return;
        }
        parent = ctLiteral.getParent(CtAssignment.class);
        if (parent != null) {
            return;
        }
        parent = ctLiteral.getParent(CtField.class);
        if (parent != null) {
            return;
        }
        parent = ctLiteral.getParent(CtThrow.class);
        if (parent != null) {
            return;
        }
        Class type = null;
        if (ctLiteral.getValue() != null) {
            type = ctLiteral.getValue().getClass();
        }
        Object value = ctLiteral.getValue();
        if (value == null) {
            return;
        }
        if (Number.class.isAssignableFrom(value.getClass())) {
            Literal constant = AccessFactory.literal(ctLiteral.getValue(), config);
            if (candidates.add(constant)) {
                logger.debug("[data] " + constant);
            }
        }
    }
}
