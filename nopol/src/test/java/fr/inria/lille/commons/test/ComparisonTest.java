package fr.inria.lille.commons.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import fr.inria.lille.commons.synthesis.expression.Expression;
import fr.inria.lille.commons.synthesis.expression.ValuedExpression;
import fr.inria.lille.commons.synthesis.operator.BinaryOperator;
import fr.inria.lille.commons.synthesis.operator.Operator;
import fr.inria.lille.commons.synthesis.operator.Parameter;
import fr.inria.lille.commons.synthesis.operator.TernaryOperator;
import fr.inria.lille.commons.synthesis.operator.UnaryOperator;
import fr.inria.lille.commons.synthesis.smt.SMTLib;
import fr.inria.lille.commons.synthesis.smt.locationVariables.OperatorLocationVariable;
import fr.inria.lille.commons.synthesis.smt.locationVariables.ParameterLocationVariable;
import fr.inria.lille.commons.synthesis.smt.locationVariables.ValuedExpressionLocationVariable;

public class ComparisonTest {

	@Test
	public void equalParameters() {
		mustBeEqual(Parameter.anObject(), Parameter.anObject());
		mustBeEqual(Parameter.aBoolean(), Parameter.aBoolean());
		mustBeEqual(Parameter.aNumber(), Parameter.aNumber());
		mustBeEqual(Parameter.aDouble(), Parameter.aDouble());
		mustBeEqual(Parameter.anInteger(), Parameter.anInteger());
		mustBeEqual(Parameter.aCharacter(), Parameter.aCharacter());
		mustBeEqual(Parameter.aString(), Parameter.aString());
	}
	
	@Test
	public void differentParameters() {
		mustBeDifferent(Parameter.anObject(), Parameter.aBoolean());
		mustBeDifferent(Parameter.aBoolean(), Parameter.aNumber());
		mustBeDifferent(Parameter.aNumber(), Parameter.aDouble());
		mustBeDifferent(Parameter.aDouble(), Parameter.anInteger());
		mustBeDifferent(Parameter.anInteger(), Parameter.aCharacter());
		mustBeDifferent(Parameter.aCharacter(), Parameter.aString());
		mustBeDifferent(Parameter.aString(), Parameter.anObject());
	}
	
	@Test
	public void equalExpressions() {
		mustBeEqual(new Expression<Boolean>(Boolean.class, "true"), new Expression<Boolean>(Boolean.class, "true"));
		mustBeEqual(new Expression<Integer>(Integer.class, "131"), new Expression<Integer>(Integer.class, "131"));
	}
	
	@Test
	public void differentExpressions() {
		mustBeDifferent(new Expression<Boolean>(Boolean.class, "true"), new Expression<Boolean>(Boolean.class, "false"));
		mustBeDifferent(new Expression<Boolean>(Boolean.class, "true"), new Expression<Integer>(Integer.class, "true"));
	}
	
	@Test
	public void equalValuedExpressions() {
		mustBeEqual(new ValuedExpression<String>(String.class, "nnn", "aString"), new ValuedExpression<String>(String.class, "nnn", "aString"));
		mustBeEqual(new ValuedExpression<Short>(Short.class, "mmm", (short) 10), new ValuedExpression<Short>(Short.class, "mmm", (short) 10));
	}
	
	@Test
	public void differentValuedExpressions() {
		mustBeDifferent(new ValuedExpression<String>(String.class, "nnn", "aString"), new ValuedExpression<String>(String.class, "nnn", "otherString"));
		mustBeDifferent(new ValuedExpression<String>(String.class, "nnn", "aString"), new ValuedExpression<String>(String.class, "---", "aString"));
		mustBeDifferent(new ValuedExpression<Float>(Float.class, "mmm", (float) 2.0), new ValuedExpression<Double>(Double.class, "mmm", 2.0));
		mustBeDifferent(new ValuedExpression<Number>(Number.class, "mmm", 2.0), new ValuedExpression<Double>(Double.class, "mmm", 2.0));
	}
	
	@SuppressWarnings("rawtypes")
	@Test
	public void equalOperators() {
		mustBeEqual(BinaryOperator.addition(), BinaryOperator.addition());
		mustBeEqual(BinaryOperator.and(), BinaryOperator.and());
		mustBeEqual(BinaryOperator.lessOrEqualThan(), BinaryOperator.lessOrEqualThan());
		mustBeEqual(BinaryOperator.lessThan(), BinaryOperator.lessThan());
		mustBeEqual(BinaryOperator.multiplication(), BinaryOperator.multiplication());
		mustBeEqual(BinaryOperator.numberDistinction(), BinaryOperator.numberDistinction());
		mustBeEqual(BinaryOperator.numberEquality(), BinaryOperator.numberEquality());
		mustBeEqual(BinaryOperator.or(), BinaryOperator.or());
		mustBeEqual(BinaryOperator.substraction(), BinaryOperator.substraction());
		mustBeEqual(UnaryOperator.not(), UnaryOperator.not());
		mustBeEqual(TernaryOperator.ifThenElse(), TernaryOperator.ifThenElse());
		
		Operator anOperator;
		Operator otherOperator;
		
		anOperator= new BinaryOperator<>(Float.class, "//", SMTLib.smtlib().symbolFor("::"), Parameter.anInteger(), Parameter.aString());
		otherOperator= new BinaryOperator<>(Float.class, "//", SMTLib.smtlib().symbolFor("::"), Parameter.anInteger(), Parameter.aString());
		mustBeEqual(anOperator, otherOperator);
		
		anOperator = new UnaryOperator<>(Character.class, ",.,", SMTLib.smtlib().symbolFor("[]"), Parameter.anObject());
		otherOperator = new UnaryOperator<>(Character.class, ",.,", SMTLib.smtlib().symbolFor("[]"), Parameter.anObject());
		mustBeEqual(anOperator, otherOperator);
		
		anOperator = new TernaryOperator<>(Object.class, "0-0", "-0-", SMTLib.smtlib().symbolFor("pop"), Parameter.aBoolean(), Parameter.anInteger(), Parameter.aNumber());
		otherOperator = new TernaryOperator<>(Object.class, "0-0", "-0-", SMTLib.smtlib().symbolFor("pop"), Parameter.aBoolean(), Parameter.anInteger(), Parameter.aNumber());
		mustBeEqual(anOperator, otherOperator);
	}
	
	@SuppressWarnings("rawtypes")
	@Test
	public void differentOperators() {
		mustBeDifferent(BinaryOperator.addition(), BinaryOperator.and());
		mustBeDifferent(BinaryOperator.and(), BinaryOperator.lessOrEqualThan());
		mustBeDifferent(BinaryOperator.lessOrEqualThan(), BinaryOperator.lessThan());
		mustBeDifferent(BinaryOperator.lessThan(), BinaryOperator.multiplication());
		mustBeDifferent(BinaryOperator.multiplication(), BinaryOperator.numberDistinction());
		mustBeDifferent(BinaryOperator.numberDistinction(), BinaryOperator.numberEquality());
		mustBeDifferent(BinaryOperator.numberEquality(), BinaryOperator.or());
		mustBeDifferent(BinaryOperator.or(), BinaryOperator.substraction());
		mustBeDifferent(BinaryOperator.substraction(), UnaryOperator.not());
		mustBeDifferent(UnaryOperator.not(), TernaryOperator.ifThenElse());
		mustBeDifferent(TernaryOperator.ifThenElse(), BinaryOperator.addition());
		
		Operator aBinaryOperator;
		Operator aUnaryOperator;
		Operator aTernaryOperator;
		Operator otherOperator;
		
		aBinaryOperator = new BinaryOperator<>(Float.class, "//", SMTLib.smtlib().symbolFor("::"), Parameter.anInteger(), Parameter.aString());
		otherOperator= new BinaryOperator<>(Float.class, "/./", SMTLib.smtlib().symbolFor("::"), Parameter.anInteger(), Parameter.aString());
		mustBeDifferent(aBinaryOperator, otherOperator);
		otherOperator= new BinaryOperator<>(Float.class, "//", SMTLib.smtlib().symbolFor(":;:"), Parameter.anInteger(), Parameter.aString());
		mustBeDifferent(aBinaryOperator, otherOperator);
		otherOperator= new BinaryOperator<>(Float.class, "//", SMTLib.smtlib().symbolFor("::"), Parameter.aNumber(), Parameter.aString());
		mustBeDifferent(aBinaryOperator, otherOperator);
		
		aUnaryOperator = new UnaryOperator<>(Character.class, ",.,", SMTLib.smtlib().symbolFor("[]"), Parameter.anObject());
		otherOperator = new UnaryOperator<>(Character.class, ",,", SMTLib.smtlib().symbolFor("[]"), Parameter.anObject());
		mustBeDifferent(aUnaryOperator, otherOperator);
		otherOperator = new UnaryOperator<>(Character.class, ",.,", SMTLib.smtlib().symbolFor("[{}]"), Parameter.anObject());
		mustBeDifferent(aUnaryOperator, otherOperator);
		otherOperator = new UnaryOperator<>(Character.class, ",.,", SMTLib.smtlib().symbolFor("[]"), Parameter.aString());
		mustBeDifferent(aUnaryOperator, otherOperator);

		aTernaryOperator = new TernaryOperator<>(Object.class, "0-0", "-0-", SMTLib.smtlib().symbolFor("pop"), Parameter.aBoolean(), Parameter.anInteger(), Parameter.aNumber());
		otherOperator = new TernaryOperator<>(Object.class, "00", "-0-", SMTLib.smtlib().symbolFor("pop"), Parameter.aBoolean(), Parameter.anInteger(), Parameter.aNumber());
		mustBeDifferent(aTernaryOperator, otherOperator);
		otherOperator = new TernaryOperator<>(Object.class, "0-0", "0-", SMTLib.smtlib().symbolFor("pop"), Parameter.aBoolean(), Parameter.anInteger(), Parameter.aNumber());
		mustBeDifferent(aTernaryOperator, otherOperator);
		otherOperator = new TernaryOperator<>(Object.class, "0-0", "-0-", SMTLib.smtlib().symbolFor("pip"), Parameter.aBoolean(), Parameter.anInteger(), Parameter.aNumber());
		mustBeDifferent(aTernaryOperator, otherOperator);
		otherOperator = new TernaryOperator<>(Object.class, "0-0", "-0-", SMTLib.smtlib().symbolFor("pop"), Parameter.anObject(), Parameter.anInteger(), Parameter.aNumber());
		mustBeDifferent(aTernaryOperator, otherOperator);
	}

	@Test
	public void equalValuedExpressionLocationVariable() {
		ValuedExpression<Integer> valuedExpression = new ValuedExpression<>(Integer.class, "iii", 23);
		mustBeEqual(new ValuedExpressionLocationVariable<>(valuedExpression, ".a", 2), new ValuedExpressionLocationVariable<>(valuedExpression, ".a", 2));
	}
	
	@Test
	public void differentValuedExpressionLocationVariable() {
		ValuedExpression<Integer> valuedExpression = new ValuedExpression<>(Integer.class, "iii", 23);
		ValuedExpression<Number> otherExpression = new ValuedExpression<>(Number.class, "iii", 23);
		mustBeDifferent(new ValuedExpressionLocationVariable<>(valuedExpression, ".a", 2), new ValuedExpressionLocationVariable<>(valuedExpression, ".", 2));
		mustBeDifferent(new ValuedExpressionLocationVariable<>(valuedExpression, ".a", 2), new ValuedExpressionLocationVariable<>(valuedExpression, ".a", 1));
		mustBeDifferent(new ValuedExpressionLocationVariable<>(valuedExpression, ".a", 2), new ValuedExpressionLocationVariable<>(otherExpression, ".a", 2));
	}
	
	@Test
	public void equalOperatorLocationVariable() {
		OperatorLocationVariable<Number> firstOperator = new OperatorLocationVariable<>(BinaryOperator.addition(), "lk");
		OperatorLocationVariable<Number> secondOperator = new OperatorLocationVariable<>(BinaryOperator.addition(), "lk");
		mustBeEqual(firstOperator, secondOperator);
		mustBeEqual(firstOperator.parameterLocationVariables(), secondOperator.parameterLocationVariables());
	}
	
	@SuppressWarnings("rawtypes")
	@Test
	public void differentOperatorLocationVariable() {
		OperatorLocationVariable firstOperator;
		OperatorLocationVariable secondOperator;
		
		firstOperator = new OperatorLocationVariable<>(BinaryOperator.addition(), "lk");
		secondOperator = new OperatorLocationVariable<>(BinaryOperator.substraction(), "lk");
		mustBeDifferent(firstOperator, secondOperator);
		mustBeDifferent(firstOperator.parameterLocationVariables(), secondOperator.parameterLocationVariables());
		
		firstOperator = new OperatorLocationVariable<>(BinaryOperator.addition(), "lk");
		secondOperator = new OperatorLocationVariable<>(BinaryOperator.or(), "lk");
		mustBeDifferent(firstOperator, secondOperator);
		mustBeDifferent(firstOperator.parameterLocationVariables(), secondOperator.parameterLocationVariables());
		
		firstOperator = new OperatorLocationVariable<>(BinaryOperator.addition(), "lk");
		secondOperator = new OperatorLocationVariable<>(BinaryOperator.addition(), "jk");
		mustBeDifferent(firstOperator, secondOperator);
		mustBeDifferent(firstOperator.parameterLocationVariables(), secondOperator.parameterLocationVariables());
	}
	
	@Test
	public void equalParameterLocationVariable() {
		OperatorLocationVariable<Number> firstOperator = new OperatorLocationVariable<>(BinaryOperator.addition(), "lk");
		OperatorLocationVariable<Number> secondOperator = new OperatorLocationVariable<>(BinaryOperator.addition(), "lk");
		ParameterLocationVariable<Boolean> firstParameter = new ParameterLocationVariable<>(Parameter.aBoolean(), "kai", firstOperator);
		ParameterLocationVariable<Boolean> secondParameter = new ParameterLocationVariable<>(Parameter.aBoolean(), "kai", secondOperator);
		mustBeEqual(firstParameter, secondParameter);
	}
	
	@SuppressWarnings("rawtypes")
	@Test
	public void differentParameterLocationVariable() {
		OperatorLocationVariable<Number> firstOperator = new OperatorLocationVariable<>(BinaryOperator.addition(), "lk");
		OperatorLocationVariable<Number> secondOperator = new OperatorLocationVariable<>(BinaryOperator.addition(), "lk");
		OperatorLocationVariable<Number> thirdOperator = new OperatorLocationVariable<>(BinaryOperator.substraction(), "lk");
		ParameterLocationVariable firstParameter;
		ParameterLocationVariable secondParameter;
		
		firstParameter = new ParameterLocationVariable<>(Parameter.aBoolean(), "kai", firstOperator);
		secondParameter = new ParameterLocationVariable<>(Parameter.anObject(), "kai", secondOperator);
		mustBeDifferent(firstParameter, secondParameter);
		
		firstParameter = new ParameterLocationVariable<>(Parameter.aBoolean(), "kai", firstOperator);
		secondParameter = new ParameterLocationVariable<>(Parameter.aBoolean(), "kia", secondOperator);
		mustBeDifferent(firstParameter, secondParameter);
		
		firstParameter = new ParameterLocationVariable<>(Parameter.aBoolean(), "kai", firstOperator);
		secondParameter = new ParameterLocationVariable<>(Parameter.aBoolean(), "kai", thirdOperator);
		mustBeDifferent(firstParameter, secondParameter);
	}
	
	private void mustBeEqual(Object a, Object b) {
		compare(a, b, true);
	}
	
	private void mustBeDifferent(Object a, Object b) {
		compare(a, b, false);
	}
	
	private void compare(Object a, Object b, boolean mustBeEqual) {
		if (mustBeEqual) {
			assertTrue(a.equals(b));
			assertTrue(b.equals(a));
			assertTrue(a.hashCode() == b.hashCode());
		}
		else {
			assertFalse(a.equals(b));
			assertFalse(b.equals(a));
		}
	}
}
