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
import static fr.inria.lille.commons.synthesis.smt.SMTLibEqualVisitor.addIfNotContained;
import static fr.inria.lille.commons.synthesis.smt.SMTLibEqualVisitor.areEquals;
import static fr.inria.lille.commons.synthesis.smt.SMTLibEqualVisitor.contains;
import static fr.inria.lille.commons.synthesis.smt.SMTLibEqualVisitor.haveSameElements;
import static fr.inria.lille.commons.synthesis.smt.SMTLibParser.binaryFrom;
import static fr.inria.lille.commons.synthesis.smt.SMTLibParser.commandFrom;
import static fr.inria.lille.commons.synthesis.smt.SMTLibParser.decimalFrom;
import static fr.inria.lille.commons.synthesis.smt.SMTLibParser.declarationFrom;
import static fr.inria.lille.commons.synthesis.smt.SMTLibParser.expressionFrom;
import static fr.inria.lille.commons.synthesis.smt.SMTLibParser.hexFrom;
import static fr.inria.lille.commons.synthesis.smt.SMTLibParser.keywordFrom;
import static fr.inria.lille.commons.synthesis.smt.SMTLibParser.numeralFrom;
import static fr.inria.lille.commons.synthesis.smt.SMTLibParser.sortFrom;
import static fr.inria.lille.commons.synthesis.smt.SMTLibParser.symbolFrom;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;
import org.smtlib.IAccept;
import org.smtlib.ICommand.Iassert;
import org.smtlib.ICommand.Ideclare_fun;
import org.smtlib.ICommand.Idefine_fun;
import org.smtlib.IExpr;
import org.smtlib.IExpr.IBinaryLiteral;
import org.smtlib.IExpr.IDecimal;
import org.smtlib.IExpr.IDeclaration;
import org.smtlib.IExpr.IFcnExpr;
import org.smtlib.IExpr.IHexLiteral;
import org.smtlib.IExpr.IKeyword;
import org.smtlib.IExpr.INumeral;
import org.smtlib.IExpr.ISymbol;
import org.smtlib.IParser.ParserException;

import xxl.java.container.classic.MetaList;

public class SMTLibEqualVisitorTest {

	@Test
	public void equalSymbols() throws ParserException {
		assertTrue(check(not(), symbolFrom("not")));
		assertTrue(check(and(), symbolFrom("and")));
		assertTrue(check(or(), symbolFrom("or")));
		assertTrue(check(implies(), symbolFrom("=>")));
		assertTrue(check(ifThenElse(), symbolFrom("ite")));
		assertTrue(check(equality(), symbolFrom("=")));
		assertTrue(check(lessThan(), symbolFrom("<")));
		assertTrue(check(lessOrEqualThan(), symbolFrom("<=")));
		assertTrue(check(distinct(), symbolFrom("distinct")));
		assertTrue(check(addition(), symbolFrom("+")));
		assertTrue(check(subtraction(), symbolFrom("-")));
		assertTrue(check(multiplication(), symbolFrom("*")));
		assertTrue(check(logicAuflia(), symbolFrom("AUFLIA")));
		assertTrue(check(logicAuflira(), symbolFrom("AUFLIRA")));
		assertTrue(check(logicAufnira(), symbolFrom("AUFNIRA")));
		assertTrue(check(logicLra(), symbolFrom("LRA")));
		assertTrue(check(logicQfUf(), symbolFrom("QF_UF")));
		assertTrue(check(logicQfLia(), symbolFrom("QF_LIA")));
		assertTrue(check(logicQfLra(), symbolFrom("QF_LRA")));
		assertTrue(check(logicQfNia(), symbolFrom("QF_NIA")));
		assertTrue(check(booleanTrue(), symbolFrom("true")));
		assertTrue(check(booleanFalse(), symbolFrom("false")));
		assertTrue(check(boolSort(), sortFrom("Bool")));
		assertTrue(check(intSort(), sortFrom("Int")));
		assertTrue(check(numberSort(), sortFrom("Real")));
	}
	
	@Test
	public void distinctSymbols() throws ParserException {
		assertFalse(check(numberSort(), symbolFrom("not")));
		assertFalse(check(not(), symbolFrom("and")));
		assertFalse(check(and(), symbolFrom("or")));
		assertFalse(check(or(), symbolFrom("=>")));
		assertFalse(check(implies(), symbolFrom("ite")));
		assertFalse(check(ifThenElse(), symbolFrom("=")));
		assertFalse(check(equality(), symbolFrom("<")));
		assertFalse(check(lessThan(), symbolFrom("<=")));
		assertFalse(check(lessOrEqualThan(), symbolFrom("distinct")));
		assertFalse(check(distinct(), symbolFrom("+")));
		assertFalse(check(addition(), symbolFrom("-")));
		assertFalse(check(subtraction(), symbolFrom("*")));
		assertFalse(check(multiplication(), symbolFrom("AUFLIA")));
		assertFalse(check(logicAuflia(), symbolFrom("AUFLIRA")));
		assertFalse(check(logicAuflira(), symbolFrom("AUFNIRA")));
		assertFalse(check(logicAufnira(), symbolFrom("LRA")));
		assertFalse(check(logicLra(), symbolFrom("QF_UF")));
		assertFalse(check(logicQfUf(), symbolFrom("QF_LIA")));
		assertFalse(check(logicQfLia(), symbolFrom("QF_LRA")));
		assertFalse(check(logicQfLra(), symbolFrom("QF_NIA")));
		assertFalse(check(logicQfNia(), symbolFrom("true")));
		assertFalse(check(booleanTrue(), symbolFrom("false")));
		assertFalse(check(booleanFalse(), symbolFrom("Bool")));
		assertFalse(check(boolSort(), sortFrom("Int")));
		assertFalse(check(intSort(), sortFrom("Real")));
		assertFalse(check(numberSort(), sortFrom("Bool")));
		assertFalse(check(boolSort(), symbolFrom("Bool")));
		assertFalse(check(intSort(), symbolFrom("Int")));
		assertFalse(check(numberSort(), symbolFrom("Real")));
	}
	
	@Test
	public void binary() throws ParserException {
		IBinaryLiteral binary = smtlib().binary("01");
		assertTrue(check(binary, binaryFrom("#b01")));
		assertFalse(check(binary, binaryFrom("#b11")));
	}
	
	@Test
	public void hex() throws ParserException {
		IHexLiteral hex = smtlib().hex("A01f8");
		assertTrue(check(hex, hexFrom("#xA01f8")));
		assertFalse(check(hex, hexFrom("#xA0f18")));
	}
	
	@Test
	public void numeral() throws ParserException {
		INumeral numeral;
		numeral = smtlib().numeral("18274");
		assertTrue(check(numeral, numeralFrom("18274")));
		assertFalse(check(numeral, numeralFrom("18273")));
		
		numeral = smtlib().numeral("1827499999999999999");
		assertTrue(check(numeral, numeralFrom("1827499999999999999")));
		assertFalse(check(numeral, numeralFrom("182749999999")));
	}
	
	@Test
	public void decimal() throws ParserException {
		IDecimal decimal;
		decimal = smtlib().decimal("18274.0");
		assertTrue(check(decimal, decimalFrom("18274.0")));
		assertFalse(check(decimal, decimalFrom("18274.1")));
		
		decimal = smtlib().decimal("18274999999124184999.01214141414239");
		assertTrue(check(decimal, decimalFrom("18274999999124184999.01214141414239")));
		assertFalse(check(decimal, decimalFrom("18274999.01214141414239")));
	}
	
	@Test
	public void keyword() throws ParserException {
		IKeyword keyword = smtlib().keyword(":asnfoain901f1_>");
		assertTrue(check(keyword, keywordFrom(":asnfoain901f1_>")));
		assertFalse(check(keyword, keywordFrom(":asnfn901f1_>")));
	}
	
	@Test
	public void expression() throws ParserException {
		IFcnExpr expression = smtlib().expression(addition(), smtlib().symbolFor("a"), smtlib().symbolFor("b"));
		IFcnExpr parsedExpr;
		
		parsedExpr = (IFcnExpr) expressionFrom("(+ a b)");
		assertTrue(check(expression, parsedExpr));
		
		parsedExpr = (IFcnExpr) expressionFrom("(+ a b c)");
		assertFalse(check(expression, parsedExpr));
		
		parsedExpr = (IFcnExpr) expressionFrom("(* a b)");
		assertFalse(check(expression, parsedExpr));
		
		parsedExpr = (IFcnExpr) expressionFrom("(+ a c)");
		assertFalse(check(expression, parsedExpr));
	}
	
	@Test
	public void declaration() throws ParserException {
		IDeclaration declaration = smtlib().declaration("X^2", numberSort());
		IDeclaration parsedDeclaration;
		
		parsedDeclaration = declarationFrom("(X^2 Real)");
		assertTrue(check(declaration, parsedDeclaration));
		
		parsedDeclaration = declarationFrom("(X^2 Int)");
		assertFalse(check(declaration, parsedDeclaration));
		
		parsedDeclaration = declarationFrom("(Y^2 Real)");
		assertFalse(check(declaration, parsedDeclaration));
	}
	
	@Test
	public void exists() throws ParserException {
		IExpr exists = expressionFrom("(exists ((x Bool) (y Int)) (= x (< y 3)))");
		IExpr otherExists;
		
		otherExists = expressionFrom("(exists ((x Bool) (y Int)) (= x (< y 3)))");
		assertTrue(check(exists, otherExists));
		
		otherExists = expressionFrom("(forall ((x Bool) (y Int)) (= x (< y 3)))");
		assertFalse(check(exists, otherExists));
		
		otherExists = expressionFrom("(exists ((h Bool) (y Int)) (= x (< y 3)))");
		assertFalse(check(exists, otherExists));
		
		otherExists = expressionFrom("(exists ((h Bool) (y Int)) (= h (< y 3)))");
		assertFalse(check(exists, otherExists));
		
		otherExists = expressionFrom("(exists ((x Bool) (y Real)) (= x (< y 3)))");
		assertFalse(check(exists, otherExists));
		
		otherExists = expressionFrom("(exists ((x Bool) (y Int)) (distinct x (< y 3)))");
		assertFalse(check(exists, otherExists));
		
		otherExists = expressionFrom("(exists ((x Bool) (y Int)) (= x (< y 4)))");
		assertFalse(check(exists, otherExists));
	}
	
	@Test
	public void forall() throws ParserException {
		IExpr forall = expressionFrom("(forall ((x Bool) (y Int)) (= x (< y 3)))");
		IExpr otherForall;
		
		otherForall = expressionFrom("(forall ((x Bool) (y Int)) (= x (< y 3)))");
		assertTrue(check(forall, otherForall));
		
		otherForall = expressionFrom("(exists ((x Bool) (y Int)) (= x (< y 3)))");
		assertFalse(check(forall, otherForall));
		
		otherForall = expressionFrom("(forall ((h Bool) (y Int)) (= x (< y 3)))");
		assertFalse(check(forall, otherForall));
		
		otherForall = expressionFrom("(forall ((h Bool) (y Int)) (= h (< y 3)))");
		assertFalse(check(forall, otherForall));
		
		otherForall = expressionFrom("(forall ((x Bool) (y Real)) (= x (< y 3)))");
		assertFalse(check(forall, otherForall));
		
		otherForall = expressionFrom("(forall ((x Bool) (y Int)) (distinct x (< y 3)))");
		assertFalse(check(forall, otherForall));
		
		otherForall = expressionFrom("(forall ((x Bool) (y Int)) (= x (< y 4)))");
		assertFalse(check(forall, otherForall));
	}
	
	@Test
	public void constant() {
		Ideclare_fun constant = (Ideclare_fun) commandFrom("(declare-fun N0 () Int)");
		Ideclare_fun otherConstant;
		
		otherConstant = (Ideclare_fun) commandFrom("(declare-fun N0 () Int)");
		assertTrue(check(constant, otherConstant));
		
		otherConstant = (Ideclare_fun) commandFrom("(declare-fun N () Int)");
		assertFalse(check(constant, otherConstant));
		
		otherConstant = (Ideclare_fun) commandFrom("(declare-fun N0 () Bool)");
		assertFalse(check(constant, otherConstant));
		
		otherConstant = (Ideclare_fun) commandFrom("(declare-fun N0 (Int Bool) Int)");
		assertFalse(check(constant, otherConstant));
	}
	
	@Test
	public void functionDeclaration() {
		Ideclare_fun function = (Ideclare_fun) commandFrom("(declare-fun hhh (Bool Real Int) Int)");
		Ideclare_fun otherFunction;
		
		otherFunction = (Ideclare_fun) commandFrom("(declare-fun hhh (Bool Real Int) Int)");
		assertTrue(check(function, otherFunction));
		
		otherFunction = (Ideclare_fun) commandFrom("(declare-fun ggg (Bool Real Int) Int)");
		assertFalse(check(function, otherFunction));
		
		otherFunction = (Ideclare_fun) commandFrom("(declare-fun hhh (Bool Real Int) Bool)");
		assertFalse(check(function, otherFunction));
		
		otherFunction = (Ideclare_fun) commandFrom("(declare-fun hhh (Bool Real Int Int) Int)");
		assertFalse(check(function, otherFunction));
		
		otherFunction = (Ideclare_fun) commandFrom("(declare-fun hhh (Bool Int Real) Int)");
		assertFalse(check(function, otherFunction));
	}
	
	@Test
	public void functionDefinition() {
		Idefine_fun function = (Idefine_fun) commandFrom("(define-fun fff ((iii Int)(bbb Bool)) Bool (and (distinct true bbb) (distinct iii 2)))");
		Idefine_fun otherFunction;
		
		otherFunction = (Idefine_fun) commandFrom("(define-fun fff ((iii Int)(bbb Bool)) Bool (and (distinct true bbb) (distinct iii 2)))");
		assertTrue(check(function, otherFunction));
		
		otherFunction = (Idefine_fun) commandFrom("(define-fun ggg ((iii Int)(bbb Bool)) Bool (and (distinct true bbb) (distinct iii 2)))");
		assertFalse(check(function, otherFunction));
		
		otherFunction = (Idefine_fun) commandFrom("(define-fun fff ((ccc Int)(bbb Bool)) Bool (and (distinct true bbb) (distinct ccc 2)))");
		assertFalse(check(function, otherFunction));
		
		otherFunction = (Idefine_fun) commandFrom("(define-fun fff ((iii Int)(ddd Bool)) Bool (and (distinct true ddd) (distinct iii 2)))");
		assertFalse(check(function, otherFunction));
		
		otherFunction = (Idefine_fun) commandFrom("(define-fun fff ((iii Int)(bbb Bool)(ccc Real)) Bool (and (distinct true bbb) (distinct iii 2)))");
		assertFalse(check(function, otherFunction));
		
		otherFunction = (Idefine_fun) commandFrom("(define-fun fff ((iii Int)(bbb Bool)) Real (and (distinct true bbb) (distinct iii 2)))");
		assertFalse(check(function, otherFunction));
		
		otherFunction = (Idefine_fun) commandFrom("(define-fun fff ((iii Int)(bbb Bool)) Bool (or (distinct true bbb) (distinct iii 2)))");
		assertFalse(check(function, otherFunction));
		
		otherFunction = (Idefine_fun) commandFrom("(define-fun fff ((iii Int)(bbb Bool)) Bool (! (distinct true bbb)))");
		assertFalse(check(function, otherFunction));
	}
	
	@Test
	public void assertion() {
		Iassert assertion = (Iassert) commandFrom("(assert (= (+ x y) 0))");
		Iassert otherAssertion;

		otherAssertion = (Iassert) commandFrom("(assert (= (+ x y) 0))");
		assertTrue(check(assertion, otherAssertion));
		
		otherAssertion = (Iassert) commandFrom("(assert (< (+ x y) 0))");
		assertFalse(check(assertion, otherAssertion));
		
		otherAssertion = (Iassert) commandFrom("(assert (= (+ 0 y) 0))");
		assertFalse(check(assertion, otherAssertion));
		
		otherAssertion = (Iassert) commandFrom("(assert (= (- x y) 0))");
		assertFalse(check(assertion, otherAssertion));
		
		otherAssertion = (Iassert) commandFrom("(assert (= (ite true x y) 0))");
		assertFalse(check(assertion, otherAssertion));
		
		otherAssertion = (Iassert) commandFrom("(assert (= (+ x y) 1))");
		assertFalse(check(assertion, otherAssertion));
	}
	
	@Test
	public void containsElement() throws ParserException {
		Collection<IAccept> collection = MetaList.newLinkedList();
		assertFalse(contains(symbolFrom("a"), collection));
		assertFalse(contains(symbolFrom("b"), collection));
		collection.add(symbolFrom("a"));
		assertTrue(contains(symbolFrom("a"), collection));
		assertFalse(contains(symbolFrom("b"), collection));
	}
	
	@Test
	public void collectionsHaveSameElements() throws ParserException {
		Collection<IAccept> collection = MetaList.newLinkedList();
		Collection<IAccept> otherCollection = MetaList.newLinkedList();
		assertTrue(haveSameElements(collection, otherCollection)); 	// {} {}
		collection.add(symbolFrom("a"));
		assertFalse(haveSameElements(collection, otherCollection));	// {a} {}
		otherCollection.add(symbolFrom("b"));
		assertFalse(haveSameElements(collection, otherCollection));	// {a} {b}
		collection.add(symbolFrom("b"));
		otherCollection.add(symbolFrom("a"));
		assertTrue(haveSameElements(collection, otherCollection));	// {a,b} {b,a}
		collection.add(symbolFrom("b"));
		otherCollection.add(symbolFrom("a"));
		assertFalse(haveSameElements(collection, otherCollection));	// {a,b,b} {b,a,a}
		otherCollection.add(symbolFrom("b"));
		collection.add(symbolFrom("a"));
		assertTrue(haveSameElements(collection, otherCollection));	// {a,b,b,a} {b,a,a,b}
	}
	
	@Test
	public void collectionAddIfNotContained() throws ParserException {
		Collection<IAccept> collection = MetaList.newLinkedList();
		assertTrue(collection.isEmpty());
		ISymbol symbol = symbolFrom("a");
		assertTrue(addIfNotContained(symbol, collection));
		assertFalse(collection.isEmpty());
		assertEquals(1, collection.size());
		assertEquals(symbol, collection.toArray()[0]);
		assertFalse(addIfNotContained(symbol, collection));
		assertFalse(collection.isEmpty());
		assertEquals(1, collection.size());
		assertEquals(symbol, collection.toArray()[0]);
		assertFalse(addIfNotContained(symbolFrom("a"), collection));
		assertFalse(collection.isEmpty());
		assertEquals(1, collection.size());
		assertEquals(symbol, collection.toArray()[0]);
	}
	
	private boolean check(IAccept actual, IAccept expected) {
		return areEquals(actual, expected);
	}
}
