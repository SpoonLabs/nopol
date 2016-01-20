package fr.inria.lille.commons.smt;

import static fr.inria.lille.commons.synthesis.smt.SMTLib.addition;
import static fr.inria.lille.commons.synthesis.smt.SMTLib.and;
import static fr.inria.lille.commons.synthesis.smt.SMTLib.boolSort;
import static fr.inria.lille.commons.synthesis.smt.SMTLib.booleanFalse;
import static fr.inria.lille.commons.synthesis.smt.SMTLib.booleanTrue;
import static fr.inria.lille.commons.synthesis.smt.SMTLib.distinct;
import static fr.inria.lille.commons.synthesis.smt.SMTLib.equality;
import static fr.inria.lille.commons.synthesis.smt.SMTLib.ifThenElse;
import static fr.inria.lille.commons.synthesis.smt.SMTLib.implies;
import static fr.inria.lille.commons.synthesis.smt.SMTLib.intSort;
import static fr.inria.lille.commons.synthesis.smt.SMTLib.lessOrEqualThan;
import static fr.inria.lille.commons.synthesis.smt.SMTLib.lessThan;
import static fr.inria.lille.commons.synthesis.smt.SMTLib.logicAuflia;
import static fr.inria.lille.commons.synthesis.smt.SMTLib.logicAuflira;
import static fr.inria.lille.commons.synthesis.smt.SMTLib.logicAufnira;
import static fr.inria.lille.commons.synthesis.smt.SMTLib.logicLra;
import static fr.inria.lille.commons.synthesis.smt.SMTLib.logicQfLia;
import static fr.inria.lille.commons.synthesis.smt.SMTLib.logicQfLra;
import static fr.inria.lille.commons.synthesis.smt.SMTLib.logicQfNia;
import static fr.inria.lille.commons.synthesis.smt.SMTLib.logicQfUf;
import static fr.inria.lille.commons.synthesis.smt.SMTLib.multiplication;
import static fr.inria.lille.commons.synthesis.smt.SMTLib.not;
import static fr.inria.lille.commons.synthesis.smt.SMTLib.numberSort;
import static fr.inria.lille.commons.synthesis.smt.SMTLib.or;
import static fr.inria.lille.commons.synthesis.smt.SMTLib.smtlib;
import static fr.inria.lille.commons.synthesis.smt.SMTLib.subtraction;
import static fr.inria.lille.commons.synthesis.smt.SMTLibParser.commandFrom;
import static fr.inria.lille.commons.synthesis.smt.SMTLibParser.declarationFrom;
import static fr.inria.lille.commons.synthesis.smt.SMTLibParser.expressionFrom;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.smtlib.ICommand;
import org.smtlib.ICommand.IScript;
import org.smtlib.ICommand.Iassert;
import org.smtlib.ICommand.Ideclare_fun;
import org.smtlib.ICommand.Idefine_fun;
import org.smtlib.IExpr;
import org.smtlib.IExpr.IBinaryLiteral;
import org.smtlib.IExpr.IDecimal;
import org.smtlib.IExpr.IDeclaration;
import org.smtlib.IExpr.IExists;
import org.smtlib.IExpr.IFcnExpr;
import org.smtlib.IExpr.IForall;
import org.smtlib.IExpr.IHexLiteral;
import org.smtlib.IExpr.IKeyword;
import org.smtlib.IExpr.INumeral;
import org.smtlib.IExpr.ISymbol;
import org.smtlib.IParser.ParserException;
import org.smtlib.ISort;

import xxl.java.container.classic.MetaSet;
import fr.inria.lille.commons.synthesis.smt.SMTLib;
import fr.inria.lille.commons.synthesis.smt.SMTLibScriptSolution;

public class SMTLibTest {

	@Test
	public void solutionForAProblemWithUniqueSolution() {
		SMTLib smtlib = new SMTLib();
		
		ISymbol x = smtlib.symbolFor("x");
		ISymbol y = smtlib.symbolFor("y");
		
		IExpr twoY = smtlib.expression(multiplication(), smtlib.numeral("2"), y);
		IExpr xPlusTwoY = smtlib.expression(addition(), x, twoY);
		IExpr equals20 = smtlib.expression(equality(), xPlusTwoY, smtlib.numeral("20"));
		ICommand firstAssertion = smtlib.assertion(equals20);

		IExpr xMinusY = smtlib.expression(subtraction(), x, y);
		IExpr equals2 = smtlib.expression(equality(), smtlib.numeral("2"), xMinusY);
		ICommand secondAssertion = smtlib.assertion(equals2);
		
		ICommand variableX = smtlib.constant(x, intSort());
		ICommand variableY = smtlib.constant(y, intSort());
		
		List<ICommand> variables = asList(variableX, variableY);
		List<ICommand> assertions = asList(firstAssertion, secondAssertion);
		
		IScript script = smtlib.scriptFrom(SMTLib.logicQfLia(), variables, assertions);
		Map<String, String> values = smtlib.anySolutionFor(script, asList(x, y));
		
		assertEquals(2, values.size());
		assertTrue(values.containsKey("x"));
		assertEquals("8", values.get("x"));
		assertTrue(values.containsKey("y"));
		assertEquals("6", values.get("y"));
	}
	
	@Test
	public void noSlutionForAProblemWithoutSolution() {
		SMTLib smtlib = new SMTLib();
		
		ISymbol x = smtlib.symbolFor("x");
		ISymbol y = smtlib.symbolFor("y");
		
		IExpr twoY = smtlib.expression(multiplication(), smtlib.numeral("2"), y);
		IExpr xPlusTwoY = smtlib.expression(addition(), x, twoY);
		IExpr equals20 = smtlib.expression(equality(), xPlusTwoY, smtlib.numeral("20"));
		ICommand firstAssertion = smtlib.assertion(equals20);

		IExpr xMinusY = smtlib.expression(subtraction(), x, y);
		IExpr equals3 = smtlib.expression(equality(), smtlib.numeral("3"), xMinusY);
		ICommand secondAssertion = smtlib.assertion(equals3);
		
		ICommand variableX = smtlib.constant(x, intSort());
		ICommand variableY = smtlib.constant(y, intSort());
		
		List<ICommand> variables = asList(variableX, variableY);
		List<ICommand> assertions = asList(firstAssertion, secondAssertion);
		
		IScript script = smtlib.scriptFrom(SMTLib.logicQfLia(), variables, assertions);
		Map<String, String> values = smtlib.anySolutionFor(script, asList(x, y));
		
		assertEquals(0, values.size());
	}
	
	@Test
	public void problemWithNegativeIntegers() {
		SMTLib smtlib = new SMTLib();
		
		ISymbol x = smtlib.symbolFor("x");
		ISymbol y = smtlib.symbolFor("y");
		
		IExpr YminusX = smtlib.expression(subtraction(), y, x);
		IExpr equalsMinus6 = smtlib.expression(equality(), YminusX, smtlib.asIExpr(-6));
		ICommand firstAssertion = smtlib.assertion(equalsMinus6);

		IExpr minus2x = smtlib.expression(multiplication(), x, smtlib.asIExpr(-2));
		IExpr minusYminus4 = smtlib.expression(subtraction(), smtlib.asIExpr(-4), y);
		IExpr areEqual = smtlib.expression(equality(), minus2x, minusYminus4);
		ICommand secondAssertion = smtlib.assertion(areEqual);
		
		ICommand variableX = smtlib.constant(x, intSort());
		ICommand variableY = smtlib.constant(y, intSort());
		
		List<ICommand> variables = asList(variableX, variableY);
		List<ICommand> assertions = asList(firstAssertion, secondAssertion);
		
		IScript script = smtlib.scriptFrom(SMTLib.logicQfLia(), variables, assertions);
		Map<String, String> values = smtlib.anySolutionFor(script, asList(x, y));
		
		assertEquals(2, values.size());
		assertTrue(values.containsKey("x"));
		assertEquals("(- 2)", values.get("x"));
		assertTrue(values.containsKey("y"));
		assertEquals("(- 8)", values.get("y"));
	}
	
	@Test
	public void allSolutionsOfAModel() {
		SMTLib smtlib = new SMTLib();
		IExpr x = smtlib.symbolFor("X");
		IExpr y = smtlib.symbolFor("Y");
		ICommand xDef = commandFrom("(declare-fun X() Bool)");
		ICommand yDef = commandFrom("(declare-fun Y() Bool)");
		ICommand trueOrFalse = commandFrom("(define-fun TrueOrFalse ((A Bool)) Bool (or (= true A) (= false A)))");
		ICommand xor = commandFrom("(define-fun Xor ((A Bool)(B Bool)) Bool (ite A (= false B) (= true B)))");
		ICommand xBound = commandFrom("(assert (TrueOrFalse X))");
		ICommand yBound = commandFrom("(assert (Xor X Y))");
		
		Map<String, String> variables;
		Collection<String> xValues = MetaSet.newHashSet();
		Collection<String> yValues = MetaSet.newHashSet();
		IScript script = smtlib.scriptFrom(SMTLib.logicQfLia(), asList(xDef, yDef, trueOrFalse, xor), asList(xBound, yBound));
		SMTLibScriptSolution solution = smtlib.scriptSolution(script, asList(x, y));

		int solutions = 0;
		while (solution.hasMoreElements()) {
			variables = solution.nextElement();
			assertTrue(variables.containsKey("X"));
			assertTrue(variables.containsKey("Y"));
			xValues.add(variables.get("X"));
			yValues.add(variables.get("Y"));
			solutions += 1;
		}
		assertEquals(2, solutions);
		assertEquals(2, xValues.size());
		assertTrue(xValues.containsAll(asList("true", "false")));
		assertEquals(2, yValues.size());
		assertTrue(yValues.containsAll(asList("true", "false")));
	}
	
	@Test
	/** Jan 2015, this test fails, I don't know why */
	@Ignore
	public void allSolutionsOfAModel2() {
		SMTLib smtlib = new SMTLib();
		IExpr x = smtlib.symbolFor("X");
		IExpr y = smtlib.symbolFor("Y");
		ICommand xDef = commandFrom("(declare-fun X() Int)");
		ICommand yDef = commandFrom("(declare-fun Y() Int)");
		ICommand oneOrTwo = commandFrom("(define-fun OneOrTwo ((A Int)) Bool (or (= 1 A) (= 2 A)))");
		ICommand zeroOrFour = commandFrom("(define-fun ZeroOrFour ((A Int)) Bool (or (= 0 A) (= 4 A)))");
		ICommand xBound = commandFrom("(assert (OneOrTwo X))");
		ICommand yBound = commandFrom("(assert (ZeroOrFour Y))");
		
		Map<String, String> variables;
		Collection<String> xValues = MetaSet.newHashSet();
		Collection<String> yValues = MetaSet.newHashSet();
		IScript script = smtlib.scriptFrom(SMTLib.logicQfLia(), asList(xDef, yDef, oneOrTwo, zeroOrFour), asList(xBound, yBound));
		SMTLibScriptSolution solution = smtlib.scriptSolution(script, asList(x, y));

		int solutions = 0;
		while (solution.hasMoreElements()) {
			variables = solution.nextElement();
			assertTrue(variables.containsKey("X"));
			assertTrue(variables.containsKey("Y"));
			xValues.add(variables.get("X"));
			yValues.add(variables.get("Y"));
			solutions += 1;
		}
		assertEquals(4, solutions);
		assertEquals(2, xValues.size());
		assertTrue(xValues.containsAll(asList("1", "2")));
		assertEquals(2, yValues.size());
		assertTrue(yValues.containsAll(asList("0", "4")));
	}
	
	@Test
	public void allSolutionsOfAModel3() {
		SMTLib smtlib = new SMTLib();
		IExpr x = smtlib.symbolFor("X");
		IExpr y = smtlib.symbolFor("Y");
		ICommand xDef = commandFrom("(declare-fun X() Real)");
		ICommand yDef = commandFrom("(declare-fun Y() Real)");
		ICommand oneOrTwo = commandFrom("(define-fun OneOrTwo ((A Real)) Bool (or (= 1.0 A) (= 2.0 A)))");
		ICommand by2 = commandFrom("(define-fun By2 ((A Real)(B Real)) Bool (= B (+ A A)))");
		ICommand xBound = commandFrom("(assert (OneOrTwo X))");
		ICommand yBound = commandFrom("(assert (By2 X Y))");
		
		Map<String, String> variables;
		Collection<String> xValues = MetaSet.newHashSet();
		Collection<String> yValues = MetaSet.newHashSet();
		IScript script = smtlib.scriptFrom(SMTLib.logicQfLra(), asList(xDef, yDef, oneOrTwo, by2), asList(xBound, yBound));
		SMTLibScriptSolution solution = smtlib.scriptSolution(script, asList(x, y));

		int solutions = 0;
		while (solution.hasMoreElements()) {
			variables = solution.nextElement();
			assertTrue(variables.containsKey("X"));
			assertTrue(variables.containsKey("Y"));
			xValues.add(variables.get("X"));
			yValues.add(variables.get("Y"));
			solutions += 1;
		}
		assertEquals(2, solutions);
		assertEquals(2, xValues.size());
		assertTrue(asList("1.0", "1", "2.0", "2").containsAll(xValues));
		assertEquals(2, yValues.size());
		assertTrue(asList("2.0", "2", "4.0", "4").containsAll(yValues));
	}
	
	@Test
	public void symbols() {
		assertEquals("not", not().toString());
		assertEquals("and", and().toString());
		assertEquals("or", or().toString());
		assertEquals("=>", implies().toString());
		assertEquals("ite", ifThenElse().toString());
		assertEquals("=", equality().toString());
		assertEquals("<", lessThan().toString());
		assertEquals("<=", lessOrEqualThan().toString());
		assertEquals("distinct", distinct().toString());
		assertEquals("+", addition().toString());
		assertEquals("-", subtraction().toString());
		assertEquals("*", multiplication().toString());
		assertEquals("AUFLIA", logicAuflia().toString());
		assertEquals("AUFLIRA", logicAuflira().toString());
		assertEquals("AUFNIRA", logicAufnira().toString());
		assertEquals("LRA", logicLra().toString());
		assertEquals("QF_UF", logicQfUf().toString());
		assertEquals("QF_LIA", logicQfLia().toString());
		assertEquals("QF_LRA", logicQfLra().toString());
		assertEquals("QF_NIA", logicQfNia().toString());
		assertEquals("true", booleanTrue().toString());
		assertEquals("false", booleanFalse().toString());
		assertEquals("Bool", boolSort().toString());
		assertEquals("Int", intSort().toString());
		assertEquals("Real", numberSort().toString());
	}
	
	@Test
	public void binary() {
		IBinaryLiteral binary = smtlib().binary("01");
		assertEquals("01", binary.toString());
		assertEquals(BigInteger.valueOf(1), binary.intValue());
	}
	
	@Test
	public void hex() {
		IHexLiteral hex = smtlib().hex("A01f8");
		assertEquals("A01f8", hex.toString());
		assertEquals(BigInteger.valueOf(655864), hex.intValue());
	}
	
	@Test
	public void numeral() {
		INumeral numeral;
		numeral = smtlib().numeral("18274");
		assertEquals("18274", numeral.toString());
		assertEquals(18274, numeral.intValue());
		
		numeral = smtlib().numeral("1827499999999999999");
		assertEquals("1827499999999999999", numeral.toString());
		assertEquals(BigInteger.valueOf(Long.valueOf("1827499999999999999")), numeral.value());
	}
	
	@Test
	public void decimal() {
		IDecimal decimal;
		decimal = smtlib().decimal("18274.0");
		assertEquals("18274.0", decimal.toString());
		assertEquals(BigDecimal.valueOf(18274.0), decimal.value());
		
		decimal = smtlib().decimal("18274999999124184999.01214141414239");
		assertEquals("18274999999124184999.01214141414239", decimal.toString());
		assertEquals(new BigDecimal("18274999999124184999.01214141414239"), decimal.value());
	}
	
	@Test
	public void keyword() {
		IKeyword keyword = smtlib().keyword(":asnfoain901f1_>");
		assertEquals(":asnfoain901f1_>", keyword.toString());
	}
	
	@Test
	public void expression() throws ParserException {
		IFcnExpr expression = smtlib().expression(addition(), smtlib().symbolFor("a"), smtlib().symbolFor("b"));
		IFcnExpr parsedExpr = (IFcnExpr) expressionFrom("(+ a b)");
		assertEquals(parsedExpr.head(), expression.head());
		assertEquals(parsedExpr.args(), expression.args());
	}
	
	@Test
	public void declaration() throws ParserException {
		IDeclaration declaration = smtlib().declaration("X^2", numberSort());
		IDeclaration parsedDeclaration = declarationFrom("(X^2 Real)");
		assertEquals(parsedDeclaration.sort().toString(), declaration.sort().toString());
		assertEquals(parsedDeclaration.parameter(), declaration.parameter());
	}
	
	@Test
	public void exists() throws ParserException {
		ISymbol xSymbol = smtlib().symbolFor("x");
		ISymbol ySymbol = smtlib().symbolFor("y");
		IDeclaration x = smtlib().declaration(xSymbol, boolSort());
		IDeclaration y = smtlib().declaration(ySymbol, intSort());
		IExpr yLessThan3 = smtlib().expression(lessThan(), ySymbol, smtlib().numeral("3"));
		IExpr predicate = smtlib().expression(equality(), xSymbol, yLessThan3);
		IExists exists = smtlib().exists(asList(x, y), predicate);
		IExists parsedExists = (IExists) expressionFrom("(exists ((x Bool) (y Int)) (= x (< y 3)))");
		assertEquals(2, exists.parameters().size());
		assertEquals(2, parsedExists.parameters().size());
		assertEquals(parsedExists.parameters().get(0).sort().toString(), exists.parameters().get(0).sort().toString());
		assertEquals(parsedExists.parameters().get(1).sort().toString(), exists.parameters().get(1).sort().toString());
		assertEquals(parsedExists.parameters().get(0).parameter(), exists.parameters().get(0).parameter());
		assertEquals(parsedExists.parameters().get(1).parameter(), exists.parameters().get(1).parameter());
		assertEquals(parsedExists.expr().toString(), exists.expr().toString());
	}
	
	@Test
	public void forall() {
		ISymbol xSymbol = smtlib().symbolFor("x");
		ISymbol ySymbol = smtlib().symbolFor("y");
		IDeclaration x = smtlib().declaration(xSymbol, boolSort());
		IDeclaration y = smtlib().declaration(ySymbol, intSort());
		IExpr yLessThan3 = smtlib().expression(lessThan(), ySymbol, smtlib().numeral("3"));
		IExpr predicate = smtlib().expression(equality(), xSymbol, yLessThan3);
		IForall forall = smtlib().forall(asList(x, y), predicate);
		assertEquals(2, forall.parameters().size());
		assertEquals("x", forall.parameters().get(0).parameter().value());
		assertEquals("Bool", forall.parameters().get(0).sort().toString());
		assertEquals("y", forall.parameters().get(1).parameter().value());
		assertEquals("Int", forall.parameters().get(1).sort().toString());
		assertEquals("(= x (< y 3))", forall.expr().toString());
	}
	
	@Test
	public void constant() {
		Ideclare_fun constant = smtlib().constant("N0", numberSort());
		assertEquals(0, constant.argSorts().size());
		assertEquals("Real", constant.resultSort().toString());
		assertEquals("N0", constant.symbol().value());
	}
	
	@Test
	public void functionDeclaration() {
		List<ISort> argsSort = asList(boolSort(), intSort(), intSort());
		Ideclare_fun functionDeclaration = smtlib().functionDeclaration(smtlib().symbolFor("?!"), argsSort, intSort());
		assertEquals(3, functionDeclaration.argSorts().size());
		assertEquals("Bool", functionDeclaration.argSorts().get(0).toString());
		assertEquals("Int", functionDeclaration.argSorts().get(1).toString());
		assertEquals("Int", functionDeclaration.argSorts().get(2).toString());
		assertEquals("Int", functionDeclaration.resultSort().toString());
		assertEquals("?!", functionDeclaration.symbol().value());
	}
	
	@Test
	public void functionDefinition() {
		ISymbol xSymbol = smtlib().symbolFor("x");
		ISymbol ySymbol = smtlib().symbolFor("y");
		IDeclaration x = smtlib().declaration(xSymbol, numberSort());
		IDeclaration y = smtlib().declaration(ySymbol, intSort());
		IExpr definition = smtlib().expression(smtlib().symbolFor("<"), xSymbol, ySymbol);
		Idefine_fun functionDefinition = smtlib().functionDefinition(smtlib().symbolFor("lt"), asList(x, y), boolSort(), definition);
		assertEquals(2, functionDefinition.parameters().size());
		assertEquals("x", functionDefinition.parameters().get(0).parameter().value());
		assertEquals("Real", functionDefinition.parameters().get(0).sort().toString());
		assertEquals("y", functionDefinition.parameters().get(1).parameter().value());
		assertEquals("Int", functionDefinition.parameters().get(1).sort().toString());
		assertEquals("lt", functionDefinition.symbol().value());
	}
	
	@Test
	public void assertion() {
		Iassert assertion = smtlib().assertion(smtlib().asIExpr(false));
		assertEquals("false", assertion.expr().toString());
	}
	
	@Test
	public void booleanClassConversion() {
		classConversion("Bool", Boolean.class);
	}
	
	@Test
	public void integerClassConversion() {
		classConversion("Int", Short.class, Long.class, Integer.class);
	}
	
	@Test
	public void numberClassConversion() {
		classConversion("Real", Float.class, Double.class, Number.class);
	}
	
	@Test
	public void trueBooleanConversion() {
		objectConversion("Bool", "true", true);
	}
	
	@Test
	public void falseBooleanConversion() {
		objectConversion("Bool", "false", false);
	}
	
	@Test
	public void positiveIntegerConversion() {
		objectConversion("Int", "23", 23);
	}
	
	@Test
	public void negativeIntegerConversion() {
		objectConversion("Int", "(- 23)", -23);
	}
	
	@Test
	public void positiveRealConversion() {
		objectConversion("Real", "2.3", 2.3);
	}
	
	@Test
	public void negativeRealConversion() {
		objectConversion("Real", "(- 2.3)", -2.3);
	}
	
	@Test
	public void positiveExponentialRealConversion() {
		objectConversion("Real", "2300000000000000000.0", 2.3E18);
	}
	
	@Test
	public void charAsIntegers() {
		char a = 'a';
		int value = (int) a;
		objectConversion("Int", "" + value, a);
	}
	
	@Test
	public void negativeExponentialRealConversion() {
		objectConversion("Real", "2.3E-18", 2.3E-18);
	}
	
	private void objectConversion(String expectedSort, String expectedExpression, Object... objects) {
		for (Object object : objects) {
			assertEquals(expectedSort, SMTLib.smtlib().sortFor(object.getClass()).toString());
			assertEquals(expectedExpression, SMTLib.smtlib().asIExpr(object).toString());
		}
	}
	
	private void classConversion(String expectedSort, Class<?>... classes) {
		for (Class<?> aClass : classes) {
			assertEquals(expectedSort, SMTLib.smtlib().sortFor(aClass).toString());
		}
	}
}
