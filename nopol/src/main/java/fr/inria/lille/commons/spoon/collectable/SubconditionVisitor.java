package fr.inria.lille.commons.spoon.collectable;

import java.util.Collection;

import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.CtAbstractVisitor;
import xxl.java.container.classic.MetaSet;

public class SubconditionVisitor extends CtAbstractVisitor {

	public SubconditionVisitor(CtExpression<Boolean> expression) {
		this.expression = expression;
	}

	@Override
	public <T> void visitCtBinaryOperator(CtBinaryOperator<T> operator) {
		addFrom(operator);
		scan(operator.getLeftHandOperand());
		if (! operator.getKind().equals(BinaryOperatorKind.INSTANCEOF)) {
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
	public <T> void visitCtInvocation(CtInvocation<T> invocation) {
		addFrom(invocation);
	}

	@Override
	public <T> void visitCtThisAccess(CtThisAccess<T> thisAccess) {
		/* this method has to be implemented by the non abstract class */
	}
	
	public Collection<String> subexpressions() {
		if (subexpressions == null) {
			subexpressions = MetaSet.newHashSet();
			scan(expression());
		}
		return subexpressions;
	}
	
	private void addFrom(CtElement element) {
		subexpressions().add(element.toString());
	}
	
	private CtExpression<Boolean> expression() {
		return expression;
	}
	
	private CtExpression<Boolean> expression;
	private Collection<String> subexpressions;
}
