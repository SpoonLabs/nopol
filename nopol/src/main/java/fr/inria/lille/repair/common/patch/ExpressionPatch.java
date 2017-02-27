package fr.inria.lille.repair.common.patch;

import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.common.synth.StatementType;
import fr.inria.lille.repair.nopol.SourceLocation;
import fr.inria.lille.diff.PatchGenerator;
import fr.inria.lille.repair.expression.Expression;
import spoon.reflect.factory.Factory;

import java.io.File;

public class ExpressionPatch implements Patch {
    private static final long serialVersionUID = -157430722893779258L;
    private final Expression expression;
    private final SourceLocation location;
    private final StatementType type;

    public ExpressionPatch(final Expression expression, final SourceLocation location, final StatementType type) {
        this.expression = expression;
        this.location = location;
        this.type = type;
    }

    /**
     * @see Patch#asString()
     */
    @Override
    public String asString() {
        return expression.asPatch();
    }

    /**
     * @return the containingClassName
     */
    @Override
    public String getRootClassName() {
        return location.getRootClassName();
    }

    /**
     * @see Patch#getFile(File)
     */
    @Override
    public File getFile(final File sourceFolder) {
        return location.getSourceFile(sourceFolder);
    }

    /**
     * @see Patch#getLineNumber()
     */
    @Override
    public int getLineNumber() {
        return location.getLineNumber();
    }

    @Override
    public StatementType getType() {
        return type;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("%s:%d: %s %s", location.getContainingClassName(), getLineNumber(), type, expression.toString());
    }

    @Override
    public SourceLocation getSourceLocation() {
        return this.location;
    }

    @Override
    public String toDiff(Factory spoon, NopolContext nopolContext) {
        return new PatchGenerator(this, spoon, nopolContext).getPatch();
    }

}
