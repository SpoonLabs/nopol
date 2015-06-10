package fr.inria.lille.commons.synthesis;

import java.util.List;

import fr.inria.lille.commons.synthesis.operator.BinaryOperator;
import fr.inria.lille.commons.synthesis.operator.Operator;
import fr.inria.lille.commons.synthesis.operator.OperatorVisitor;
import fr.inria.lille.commons.synthesis.operator.TernaryOperator;
import fr.inria.lille.commons.synthesis.operator.UnaryOperator;
import fr.inria.lille.commons.synthesis.smt.SMTLib;

public class OperationCodeLine extends CodeLine implements OperatorVisitor<String> {

	public OperationCodeLine(int lineNumber, Operator<?> operator, List<CodeLine> arguments) {
		super(lineNumber, "");
		this.operator = operator;
		this.arguments = arguments;
		content = operator.accept(this);
	}

	public Operator<?> operator() {
		return operator;
	}
	
	public List<CodeLine> arguments() {
		return arguments;
	}
	
	@Override
	public String visitUnaryOperator(UnaryOperator<?, ?> operator) {
		return operator.symbol() + "(" +  subContent(0) + ")";
	}

	@Override
	public String visitBinaryOperator(BinaryOperator<?, ?, ?> operator) {
		return String.format("(%s)%s(%s)", subContent(0), operator.symbol(), subContent(1));
	}

	@Override
	public String visitTernaryOperator(TernaryOperator<?, ?, ?, ?> operator) {
		return String.format("(%s)%s(%s)%s(%s)", subContent(0), operator.firstSymbol(), subContent(1), operator.secondSymbol(), subContent(2));
	}
	
	private String subContent(int index) {
		return arguments().get(index).content();
	}
	
	private Operator<?> operator;
	private List<CodeLine> arguments;
}
