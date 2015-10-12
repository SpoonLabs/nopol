package fr.inria.lille.repair.nopol.spoon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;
import xxl.java.library.FileLibrary;

public abstract class InitialisationProcessor extends AbstractProcessor<CtStatement> {

    public abstract CtElement processInitialisation(CtStatement statement, String newValue);

    public InitialisationProcessor(CtStatement target, String defaultValue) {
        this.target = target;
        this.defaultValue = defaultValue;
    }

    @Override
    public boolean isToBeProcessed(CtStatement statement) {
        if (statement.getPosition() != null) {
            return (statement.getPosition().getLine() == target().getPosition().getLine()) &&
                    (statement.getPosition().getColumn() == target().getPosition().getColumn()) &&
                    (FileLibrary.isSameFile(target().getPosition().getFile(), statement.getPosition().getFile()));
        }
        return false;
    }

    @Override
    public void process(CtStatement statement) {
        processInitialisation(statement, defaultValue());
    }

    public String defaultValue() {
        return defaultValue;
    }

    public void setDefaultCondition(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    protected CtStatement target() {
        return target;
    }

    private CtStatement target;
    private String defaultValue;
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
}
