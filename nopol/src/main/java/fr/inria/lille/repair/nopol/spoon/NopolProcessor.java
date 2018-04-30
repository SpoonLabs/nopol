package fr.inria.lille.repair.nopol.spoon;

import fr.inria.lille.repair.common.synth.RepairType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtStatement;
import spoon.reflect.cu.position.NoSourcePosition;

public abstract class NopolProcessor extends AbstractProcessor<CtStatement> {

    public NopolProcessor(CtStatement target, RepairType type) {
        this.target = target;
        this.repairType = type;
    }

    @Override
    public boolean isToBeProcessed(CtStatement statement) {
        if (statement.getPosition() instanceof NoSourcePosition || target.getPosition() instanceof NoSourcePosition) {
            return false;
        }
        return target.getPosition().getSourceStart() == statement.getPosition().getSourceStart() && target.getPosition().getSourceEnd() == statement.getPosition().getSourceEnd() && statement.equals(target);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public CtStatement getTarget() {
        return target;
    }

    public RepairType getRepairType() {
        return repairType;
    }

    private RepairType repairType;

    private Class<?> type;
    private String defaultValue;
    private String value;
    private final CtStatement target;
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
}
