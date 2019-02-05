package fr.inria.lille.commons.spoon.collectable;

import spoon.reflect.code.*;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtCatchVariableReference;
import spoon.reflect.visitor.CtScanner;
import xxl.java.container.classic.MetaSet;

import java.util.Collection;

import static fr.inria.lille.commons.spoon.util.SpoonElementLibrary.hasChildrenOfType;

public class SubconditionVisitor extends CtScanner {

    public SubconditionVisitor(CtExpression<Boolean> expression) {
        this.expression = expression;
    }

    @Override
    public <T> void visitCtBinaryOperator(CtBinaryOperator<T> operator) {
        addFrom(operator);
        scan(operator.getLeftHandOperand());
        if (!operator.getKind().equals(BinaryOperatorKind.INSTANCEOF)) {
            scan(operator.getRightHandOperand());
        }
    }

    @Override
    public <T> void visitCtUnaryOperator(CtUnaryOperator<T> operator) {
        addFrom(operator);
        scan(operator.getOperand());
    }

    @Override
    public <T> void visitCtLiteral(CtLiteral<T> literal) {
        addFrom(literal);
    }

    @Override
    public <T> void visitCtArrayRead(CtArrayRead<T> arrayRead) {
        addFrom(arrayRead);
    }

    @Override
    public <T> void visitCtArrayWrite(CtArrayWrite<T> arrayWrite) {
        addFrom(arrayWrite);
    }

    @Override
    public <T> void visitCtVariableRead(CtVariableRead<T> variableRead) {
        visitCtVariableAccess(variableRead);
    }

    @Override
    public <T> void visitCtVariableWrite(CtVariableWrite<T> variableWrite) {
        visitCtVariableAccess(variableWrite);
    }

    public <T> void visitCtVariableAccess(CtVariableAccess<T> variableAccess) {
        addFrom(variableAccess);
    }

    @Override
    public <T> void visitCtInvocation(CtInvocation<T> invocation) {
        for (CtExpression<?> argument : invocation.getArguments()) {
            scan(argument);
        }
    }

    @Override
    public <T> void visitCtCatchVariableReference(
            CtCatchVariableReference<T> localVariable) {
        /* Ignore catch variable */
    }

    @Override
    public <T> void visitCtThisAccess(CtThisAccess<T> thisAccess) {
		/* this method has to be implemented by the non abstract class */
    }

    @Override
    public <T, A extends T> void visitCtAssignment(CtAssignment<T, A> assignment) {
        scan(assignment.getAssigned());
        scan(assignment.getAssignment());
    }

    public Collection<String> subexpressions() {
        if (subexpressions == null) {
            subexpressions = MetaSet.newHashSet();
            scan(expression());
        }
        return subexpressions;
    }

    private void addFrom(CtElement element) {
        if (!(hasChildrenOfType(element, CtAssignment.class) || hasChildrenOfType(element, CtInvocation.class))) {
            subexpressions().add(element.toString());
        }
    }

    private CtExpression<Boolean> expression() {
        return expression;
    }

    private CtExpression<Boolean> expression;
    private Collection<String> subexpressions;
}
