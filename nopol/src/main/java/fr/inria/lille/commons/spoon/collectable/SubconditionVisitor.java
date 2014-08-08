package fr.inria.lille.commons.spoon.collectable;

import java.util.Collection;

import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.CtAbstractVisitor;
import fr.inria.lille.commons.collections.SetLibrary;

public class SubconditionVisitor extends CtAbstractVisitor {

	public SubconditionVisitor(CtExpression<Boolean> expression) {
		this.expression = expression;
	}

	@Override
	public <T> void visitCtBinaryOperator(CtBinaryOperator<T> operator) {
		addFrom(operator);
		scan(operator.getLeftHandOperand());
		scan(operator.getRightHandOperand());
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
			subexpressions = SetLibrary.newHashSet();
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
