package fr.inria.lille.repair.nopol.spoon;

import fr.inria.lille.repair.nopol.SourceLocation;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;

/**
 * Created by spirals on 11/06/15.
 */
public class MethodFromLocation extends AbstractProcessor<CtStatement> {
    private final SourceLocation location;
    private String method;

    public MethodFromLocation(SourceLocation location) {
        this.location = location;
    }

    @Override
    public boolean isToBeProcessed(CtStatement candidate) {
        CtClass parent = candidate.getParent(CtClass.class);
        if (parent == null || !parent.getQualifiedName().equals(this.location.getContainingClassName())) {
            return false;
        }
        return parent.getPosition().getLine() == location.getLineNumber();
    }

    @Override
    public void process(CtStatement ctStatement) {
        this.method = ctStatement.getParent(CtMethod.class).getSimpleName();
    }

    public String getMethod() {
        return method;
    }
}
