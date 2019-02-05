package fr.inria.lille.repair.synthesis.collect.spoon;


import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.common.Candidates;
import fr.inria.lille.repair.expression.access.Literal;
import fr.inria.lille.repair.expression.factory.AccessFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtMethod;

public class DynamothConstantCollector extends AbstractProcessor<CtLiteral> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Candidates candidates;
    private final String buggyMethod;
    private final NopolContext nopolContext;

    public DynamothConstantCollector(Candidates candidates, String buggyMethod, NopolContext nopolContext) {
        this.nopolContext = nopolContext;
        this.candidates = candidates;
        this.buggyMethod = buggyMethod;
    }

    @Override
    public boolean isToBeProcessed(CtLiteral candidate) {
        CtMethod parent = candidate.getParent(CtMethod.class);
        Object value = candidate.getValue();
        if (parent == null) {
            return false;
        } else if (value instanceof Boolean || value == null) {
            return false;
        }
        return (this.buggyMethod == null || parent.getSimpleName().equals(this.buggyMethod)) &&
                Number.class.isAssignableFrom(value.getClass());
    }

    @Override
    public void process(CtLiteral ctLiteral) {
        Literal constant = AccessFactory.literal(ctLiteral.getValue(), nopolContext);
        if (candidates.add(constant)) {
            logger.debug("[data] " + constant);
        }
    }
}
