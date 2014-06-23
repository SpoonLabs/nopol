package fr.inria.lille.commons.smt;

import static fr.inria.lille.commons.synthesis.smt.SMTLib.smtlib;
import static fr.inria.lille.commons.synthesis.smt.SMTLibParser.declarationFrom;
import static fr.inria.lille.commons.synthesis.smt.SMTLibParser.expressionFrom;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

import fr.inria.lille.commons.synthesis.smt.ObjectToExpr;
import fr.inria.lille.commons.synthesis.smt.SMTLib;

public class SMTLibTest {

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Test
	public void solutionForAProblemWithUniqueSolution() {
		SMTLib smtlib = new SMTLib();
		
		ISymbol x = smtlib.symbolFor("x");
		ISymbol y = smtlib.symbolFor("y");
		
		IExpr twoY = smtlib.expression(smtlib.multiplication(), smtlib.numeral("2"), y);
		IExpr xPlusTwoY = smtlib.expression(smtlib.addition(), x, twoY);
		IExpr equals20 = smtlib.expression(smtlib.equals(), xPlusTwoY, smtlib.numeral("20"));
		ICommand firstAssertion = smtlib.assertion(equals20);

		IExpr xMinusY = smtlib.expression(smtlib.substraction(), x, y);
		IExpr equals2 = smtlib.expression(smtlib.equals(), smtlib.numeral("2"), xMinusY);
		ICommand secondAssertion = smtlib.assertion(equals2);
		
		ICommand variableX = smtlib.constant(x, smtlib.intSort());
		ICommand variableY = smtlib.constant(y, smtlib.intSort());
		
		List<ICommand> variables = Arrays.asList(variableX, variableY);
		List<ICommand> assertions = Arrays.asList(firstAssertion, secondAssertion);
		
		IScript script = smtlib.scriptFrom(smtlib.logicQfLia(), variables, (List) Arrays.asList(), assertions);
		Map<String, String> values = smtlib.satisfyingValuesFor((List) Arrays.asList(x, y), script);
		
		assertEquals(2, values.size());
		assertTrue(values.containsKey("x"));
		assertEquals("8", values.get("x"));
		assertTrue(values.containsKey("y"));
		assertEquals("6", values.get("y"));
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	@Test
	public void noSlutionForAProblemWithoutSolution() {
		SMTLib smtlib = new SMTLib();
		
		ISymbol x = smtlib.symbolFor("x");
		ISymbol y = smtlib.symbolFor("y");
		
		IExpr twoY = smtlib.expression(smtlib.multiplication(), smtlib.numeral("2"), y);
		IExpr xPlusTwoY = smtlib.expression(smtlib.addition(), x, twoY);
		IExpr equals20 = smtlib.expression(smtlib.equals(), xPlusTwoY, smtlib.numeral("20"));
		ICommand firstAssertion = smtlib.assertion(equals20);

		IExpr xMinusY = smtlib.expression(smtlib.substraction(), x, y);
		IExpr equals3 = smtlib.expression(smtlib.equals(), smtlib.numeral("3"), xMinusY);
		ICommand secondAssertion = smtlib.assertion(equals3);
		
		ICommand variableX = smtlib.constant(x, smtlib.intSort());
		ICommand variableY = smtlib.constant(y, smtlib.intSort());
		
		List<ICommand> variables = Arrays.asList(variableX, variableY);
		List<ICommand> assertions = Arrays.asList(firstAssertion, secondAssertion);
		
		IScript script = smtlib.scriptFrom(smtlib.logicQfLia(), variables, (List) Arrays.asList(), assertions);
		Map<String, String> values = smtlib.satisfyingValuesFor((List) Arrays.asList(x, y), script);
		
		assertEquals(0, values.size());
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	@Test
	public void problemWithNegativeIntegers() {
		SMTLib smtlib = new SMTLib();
		
		ISymbol x = smtlib.symbolFor("x");
		ISymbol y = smtlib.symbolFor("y");
		
		IExpr YminusX = smtlib.expression(smtlib.substraction(), y, x);
		IExpr equalsMinus6 = smtlib.expression(smtlib.equals(), YminusX, ObjectToExpr.asIExpr(-6));
		ICommand firstAssertion = smtlib.assertion(equalsMinus6);

		IExpr minus2x = smtlib.expression(smtlib.multiplication(), x, ObjectToExpr.asIExpr(-2));
		IExpr minusYminus4 = smtlib.expression(smtlib.substraction(), ObjectToExpr.asIExpr(-4), y);
		IExpr areEqual = smtlib.expression(smtlib.equals(), minus2x, minusYminus4);
		ICommand secondAssertion = smtlib.assertion(areEqual);
		
		ICommand variableX = smtlib.constant(x, smtlib.intSort());
		ICommand variableY = smtlib.constant(y, smtlib.intSort());
		
		List<ICommand> variables = Arrays.asList(variableX, variableY);
		List<ICommand> assertions = Arrays.asList(firstAssertion, secondAssertion);
		
		IScript script = smtlib.scriptFrom(smtlib.logicQfLia(), variables, (List) Arrays.asList(), assertions);
		Map<String, String> values = smtlib.satisfyingValuesFor((List) Arrays.asList(x, y), script);
		
		assertEquals(2, values.size());
		assertTrue(values.containsKey("x"));
		assertEquals("( - 2 )", values.get("x"));
		assertTrue(values.containsKey("y"));
		assertEquals("( - 8 )", values.get("y"));
	}
	
	@Test
	public void symbols() {
		SMTLib smtlib = new SMTLib();
		assertEquals("not", smtlib.not().toString());
		assertEquals("and", smtlib.and().toString());
		assertEquals("or", smtlib.or().toString());
		assertEquals("=>", smtlib.implies().toString());
		assertEquals("ite", smtlib.ifThenElse().toString());
		assertEquals("=", smtlib.equals().toString());
		assertEquals("<", smtlib.lessThan().toString());
		assertEquals("<=", smtlib.lessOrEqualThan().toString());
		assertEquals("distinct", smtlib.distinct().toString());
		assertEquals("+", smtlib.addition().toString());
		assertEquals("-", smtlib.substraction().toString());
		assertEquals("*", smtlib.multiplication().toString());
		assertEquals("AUFLIA", smtlib.logicAuflia().toString());
		assertEquals("AUFLIRA", smtlib.logicAuflira().toString());
		assertEquals("AUFNIRA", smtlib.logicAufnira().toString());
		assertEquals("LRA", smtlib.logicLra().toString());
		assertEquals("QF_UF", smtlib.logicQfUf().toString());
		assertEquals("QF_LIA", smtlib.logicQfLia().toString());
		assertEquals("QF_LRA", smtlib.logicQfLra().toString());
		assertEquals("QF_NIA", smtlib.logicQfNia().toString());
		assertEquals("true", smtlib.booleanTrue().toString());
		assertEquals("false", smtlib.booleanFalse().toString());
		assertEquals("Bool", smtlib.boolSort().toString());
		assertEquals("Int", smtlib.intSort().toString());
		assertEquals("Real", smtlib.numberSort().toString());
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
		IKeyword keyword = smtlib().keyword(":asnfoain901f1_:>");
		assertEquals(":asnfoain901f1_:>", keyword.toString());
	}
	
	@Test
	public void expression() throws ParserException {
		IFcnExpr expression = smtlib().expression(smtlib().addition(), smtlib().symbolFor("a"), smtlib().symbolFor("b"));
		IFcnExpr parsedExpr = (IFcnExpr) expressionFrom("(+ a b)");
		assertEquals(parsedExpr.head(), expression.head());
		assertEquals(parsedExpr.args(), expression.args());
	}
	
	@Test
	public void declaration() throws ParserException {
		IDeclaration declaration = smtlib().declaration("X^2", smtlib().numberSort());
		IDeclaration parsedDeclaration = declarationFrom("(X^2 Real)");
		assertEquals(parsedDeclaration.sort().toString(), declaration.sort().toString());
		assertEquals(parsedDeclaration.parameter(), declaration.parameter());
	}
	
	@Test
	public void exists() throws ParserException {
		ISymbol xSymbol = smtlib().symbolFor("x");
		ISymbol ySymbol = smtlib().symbolFor("y");
		IDeclaration x = smtlib().declaration(xSymbol, smtlib().boolSort());
		IDeclaration y = smtlib().declaration(ySymbol, smtlib().intSort());
		IExpr yLessThan3 = smtlib().expression(smtlib().lessThan(), ySymbol, smtlib().numeral("3"));
		IExpr predicate = smtlib().expression(smtlib().equals(), xSymbol, yLessThan3);
		IExists exists = smtlib().exists(Arrays.asList(x, y), predicate);
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
		IDeclaration x = smtlib().declaration(xSymbol, smtlib().boolSort());
		IDeclaration y = smtlib().declaration(ySymbol, smtlib().intSort());
		IExpr yLessThan3 = smtlib().expression(smtlib().lessThan(), ySymbol, smtlib().numeral("3"));
		IExpr predicate = smtlib().expression(smtlib().equals(), xSymbol, yLessThan3);
		IForall forall = smtlib().forall(Arrays.asList(x, y), predicate);
		assertEquals(2, forall.parameters().size());
		assertEquals("x", forall.parameters().get(0).parameter().value());
		assertEquals("Bool", forall.parameters().get(0).sort().toString());
		assertEquals("y", forall.parameters().get(1).parameter().value());
		assertEquals("Int", forall.parameters().get(1).sort().toString());
		assertEquals("(= x (< y 3))", forall.expr().toString());
	}
	
	@Test
	public void constant() {
		Ideclare_fun constant = smtlib().constant("N0", smtlib().numberSort());
		assertEquals(0, constant.argSorts().size());
		assertEquals("Real", constant.resultSort().toString());
		assertEquals("N0", constant.symbol().value());
	}
	
	@Test
	public void functionDeclaration() {
		List<ISort> argsSort = Arrays.asList(smtlib().boolSort(), smtlib().intSort(), smtlib().intSort());
		Ideclare_fun functionDeclaration = smtlib().functionDeclaration(smtlib().symbolFor("?:"), argsSort, smtlib().intSort());
		assertEquals(3, functionDeclaration.argSorts().size());
		assertEquals("Bool", functionDeclaration.argSorts().get(0).toString());
		assertEquals("Int", functionDeclaration.argSorts().get(1).toString());
		assertEquals("Int", functionDeclaration.argSorts().get(2).toString());
		assertEquals("Int", functionDeclaration.resultSort().toString());
		assertEquals("?:", functionDeclaration.symbol().value());
	}
	
	@Test
	public void functionDefinition() {
		ISymbol xSymbol = smtlib().symbolFor("x");
		ISymbol ySymbol = smtlib().symbolFor("y");
		IDeclaration x = smtlib().declaration(xSymbol, smtlib().numberSort());
		IDeclaration y = smtlib().declaration(ySymbol, smtlib().intSort());
		IExpr definition = smtlib().expression(smtlib().symbolFor("<"), xSymbol, ySymbol);
		Idefine_fun functionDefinition = smtlib().functionDefinition(smtlib().symbolFor("lt"), Arrays.asList(x, y), smtlib().boolSort(), definition);
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
	
	/**/
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
		objectConversion("Real", "2300000000000000000", 2.3E18);
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
