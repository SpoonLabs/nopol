package fr.inria.lille.commons.synthesis.smt.constraint;

import static fr.inria.lille.commons.synthesis.smt.SMTLib.smtlib;
import static fr.inria.lille.commons.synthesis.smt.SMTLibEqualVisitor.areEquals;
import static fr.inria.lille.commons.synthesis.smt.SMTLibEqualVisitor.haveSameElements;
import static fr.inria.lille.commons.synthesis.smt.SMTLibParser.commandFrom;
import static fr.inria.lille.commons.synthesis.smt.SMTLibParser.declarationsFrom;
import static fr.inria.lille.commons.synthesis.smt.SMTLibParser.expressionFrom;
import static fr.inria.lille.commons.synthesis.smt.SMTLibParser.expressionsFrom;
import static fr.inria.lille.commons.synthesis.smt.SMTLibParser.symbolFrom;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.smtlib.ICommand;
import org.smtlib.IExpr;
import org.smtlib.IExpr.IDeclaration;
import org.smtlib.IParser.ParserException;

import xxl.java.container.classic.MetaList;
import xxl.java.container.classic.MetaMap;
import xxl.java.library.StringLibrary;
import fr.inria.lille.commons.synthesis.expression.Expression;
import fr.inria.lille.commons.synthesis.operator.BinaryOperator;
import fr.inria.lille.commons.synthesis.operator.Operator;
import fr.inria.lille.commons.synthesis.operator.TernaryOperator;
import fr.inria.lille.commons.synthesis.operator.UnaryOperator;
import fr.inria.lille.commons.synthesis.smt.locationVariables.LocationVariable;
import fr.inria.lille.commons.synthesis.smt.locationVariables.LocationVariableContainer;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ConstraintTest {

	@Test
	public void acyclicityConstraint() throws ParserException {
		Constraint acyclicity = new AcyclicityConstraint(smtlib());
		LocationVariableContainer container = locationVariableContainerExample();
		
		List<String> arguments = LocationVariable.expressionsOf(container.operatorsAndParameters());
		checkArguments(acyclicity, container, arguments);
		checkInvocation(acyclicity, container, arguments);

		Map<String, String> types = typesFor(container);
		List<String> declarations = builtDeclarations(arguments, types);
		List<IDeclaration> smtDeclarations = checkParameters(acyclicity, container, declarations);
		
		List<String> definitions = Arrays.asList("(< L@op<0><0> L@op<0>)", "(< L@op<0><1> L@op<0>)", "(< L@op<1><0> L@op<1>)", "(< L@op<1><1> L@op<1>)",
				"(< L@op<2><0> L@op<2>)", "(< L@op<2><1> L@op<2>)", "(< L@op<2><2> L@op<2>)", "(< L@op<3><0> L@op<3>)");
		Collection<IExpr> smtDefinitions = checkDefinitionExpressions(acyclicity, container, definitions);
		
		checkDefinition(acyclicity, container, declarations, smtDeclarations, definitions, smtDefinitions, "and");
	}
	
	@Test
	public void consistencyConstraint() throws ParserException {
		Constraint consistencty = new ConsistencyConstraint(smtlib());
		LocationVariableContainer container = locationVariableContainerExample();
		
		List<String> arguments = LocationVariable.expressionsOf((List) container.operators());
		checkArguments(consistencty, container, arguments);
		checkInvocation(consistencty, container, arguments);

		Map<String, String> types = typesFor(container);
		List<String> declarations = builtDeclarations(arguments, types);
		List<IDeclaration> smtDeclarations = checkParameters(consistencty, container, declarations);
		
		List<String> definitions = Arrays.asList("(distinct L@op<0> L@op<1>)", "(distinct L@op<0> L@op<2>)", "(distinct L@op<0> L@op<3>)",
				"(distinct L@op<1> L@op<2>)", "(distinct L@op<1> L@op<3>)", "(distinct L@op<2> L@op<3>)");
		Collection<IExpr> smtDefinitions = checkDefinitionExpressions(consistencty, container, definitions);
		
		checkDefinition(consistencty, container, declarations, smtDeclarations, definitions, smtDefinitions, "and");
	}
	
	@Test
	public void lineBoundConstraint() throws ParserException {
		Constraint lineBound = new LineBoundConstraint(smtlib());
		LocationVariableContainer container = locationVariableContainerExample();
		
		List<String> arguments = LocationVariable.expressionsOf(container.operatorsParametersAndOutput());
		checkArguments(lineBound, container, arguments);
		checkInvocation(lineBound, container, arguments);

		Map<String, String> types = typesFor(container);
		List<String> declarations = builtDeclarations(arguments, types);
		List<IDeclaration> smtDeclarations = checkParameters(lineBound, container, declarations);
		
		List<String> definitions = Arrays.asList("(and (<= 3 L@op<0>) (<= L@op<0> 6))", "(and (<= 3 L@op<1>) (<= L@op<1> 6))",
				"(and (<= 3 L@op<2>) (<= L@op<2> 6))", "(and (<= 3 L@op<3>) (<= L@op<3> 6))", "(or (= 1 L@op<0><0>) (= 2 L@op<0><0>) (= L@op<2> L@op<0><0>))",
				"(or (= 1 L@op<0><1>) (= 2 L@op<0><1>) (= L@op<2> L@op<0><1>))", "(or (= 0 L@op<1><0>) (= L@op<3> L@op<1><0>))",
				"(or (= 0 L@op<1><1>) (= L@op<3> L@op<1><1>))", "(or (= 0 L@op<2><0>) (= L@op<1> L@op<2><0>) (= L@op<3> L@op<2><0>))",
				"(or (= 1 L@op<2><1>) (= 2 L@op<2><1>) (= L@op<0> L@op<2><1>))", "(or (= 1 L@op<2><2>) (= 2 L@op<2><2>) (= L@op<0> L@op<2><2>))",
				"(or (= 0 L@op<3><0>) (= L@op<1> L@op<3><0>))", "(or (= 0 L@out) (= L@op<1> L@out) (= L@op<3> L@out))");
		Collection<IExpr> smtDefinitions = checkDefinitionExpressions(lineBound, container, definitions);
		
		checkDefinition(lineBound, container, declarations, smtDeclarations, definitions, smtDefinitions, "and");
	}
	
	@Test
	public void libraryConstraint() throws ParserException {
		Constraint library = new LibraryConstraint(smtlib());
		LocationVariableContainer container = locationVariableContainerExample();
		
		List<String> arguments = LocationVariable.subexpressionsOf(container.operatorsAndParameters());
		checkArguments(library, container, arguments);
		checkInvocation(library, container, arguments);

		Map<String, String> types = typesFor(container);
		List<String> declarations = builtDeclarations(arguments, types);
		List<IDeclaration> smtDeclarations = checkParameters(library, container, declarations);
		
		List<String> definitions = Arrays.asList("(= op<0> (+ op<0><0> op<0><1>))", "(= op<1> (or op<1><0> op<1><1>))", 
				"(= op<2> (ite op<2><0> op<2><1> op<2><2>))", "(= op<3> (not op<3><0>))");
		Collection<IExpr> smtDefinitions = checkDefinitionExpressions(library, container, definitions);
		
		checkDefinition(library, container, declarations, smtDeclarations, definitions, smtDefinitions, "and");
	}
	
	@Test
	public void connectivityConstraint() throws ParserException {
		Constraint connectivity = new ConnectivityConstraint(smtlib());
		LocationVariableContainer container = locationVariableContainerExample();
		
		List<String> arguments = LocationVariable.expressionsOf(container.operatorsParametersAndOutput());
		arguments.addAll(LocationVariable.subexpressionsOf((List) container.inputs()));
		arguments.addAll(LocationVariable.subexpressionsOf(container.operatorsParametersAndOutput()));
		checkArguments(connectivity, container, arguments);
		checkInvocation(connectivity, container, arguments);

		Map<String, String> types = typesFor(container);
		List<String> declarations = builtDeclarations(arguments, types);
		List<IDeclaration> smtDeclarations = checkParameters(connectivity, container, declarations);
		
		List<String> definitions = Arrays.asList("(=> (= L@out 0) (= out in<0>))", "(=> (= L@out L@op<1>) (= out op<1>))",
				"(=> (= L@out L@op<3>) (= out op<3>))", "(=> (= L@op<0><0> 1) (= op<0><0> in<1>))", "(=> (= L@op<0><0> 2) (= op<0><0> in<2>))",
				"(=> (= L@op<0><0> L@op<2>) (= op<0><0> op<2>))", "(=> (= L@op<0><1> 1) (= op<0><1> in<1>))", "(=> (= L@op<0><1> 2) (= op<0><1> in<2>))",
				"(=> (= L@op<0><1> L@op<2>) (= op<0><1> op<2>))", "(=> (= L@op<1><0> 0) (= op<1><0> in<0>))", "(=> (= L@op<1><0> L@op<3>) (= op<1><0> op<3>))",
				"(=> (= L@op<1><1> 0) (= op<1><1> in<0>))", "(=> (= L@op<1><1> L@op<3>) (= op<1><1> op<3>))", "(=> (= L@op<2><0> 0) (= op<2><0> in<0>))",
				"(=> (= L@op<2><0> L@op<1>) (= op<2><0> op<1>))", "(=> (= L@op<2><0> L@op<3>) (= op<2><0> op<3>))", "(=> (= L@op<2><1> 1) (= op<2><1> in<1>))",
				"(=> (= L@op<2><1> 2) (= op<2><1> in<2>))", "(=> (= L@op<2><1> L@op<0>) (= op<2><1> op<0>))", "(=> (= L@op<2><2> 1) (= op<2><2> in<1>))",
				"(=> (= L@op<2><2> 2) (= op<2><2> in<2>))", "(=> (= L@op<2><2> L@op<0>) (= op<2><2> op<0>))", "(=> (= L@op<3><0> 0) (= op<3><0> in<0>))",
				"(=> (= L@op<3><0> L@op<1>) (= op<3><0> op<1>))");
		Collection<IExpr> smtDefinitions = checkDefinitionExpressions(connectivity, container, definitions);
		
		checkDefinition(connectivity, container, declarations, smtDeclarations, definitions, smtDefinitions, "and");
	}
	
	@Test
	public void verificationConstraint() throws ParserException {
		Constraint verification = new VerificationConstraint(smtlib());
		LocationVariableContainer container = locationVariableContainerExample();
		
		List<String> arguments = LocationVariable.expressionsOf(container.operatorsParametersAndOutput());
		arguments.addAll(LocationVariable.subexpressionsOf(container.inputsAndOutput()));
		checkArguments(verification, container, arguments);
		checkInvocation(verification, container, arguments);
		
		Map<String, String> types = typesFor(container);
		List<String> declarations = builtDeclarations(arguments, types);
		List<IDeclaration> smtDeclarations = checkParameters(verification, container, declarations);
		
		List<String> existsDeclarations = builtDeclarations(LocationVariable.subexpressionsOf(container.operatorsAndParameters()), types);
		String connectivityInvocation = "(Connectivity L@op<0> L@op<1> L@op<2> L@op<3> L@op<0><0> L@op<0><1> L@op<1><0> " +
				"L@op<1><1> L@op<2><0> L@op<2><1> L@op<2><2> L@op<3><0> L@out in<0> in<1> in<2> op<0> op<1> op<2> op<3> op<0><0> " +
				"op<0><1> op<1><0> op<1><1> op<2><0> op<2><1> op<2><2> op<3><0> out)";
		String libraryInvocation = "(Library op<0> op<1> op<2> op<3> op<0><0> op<0><1> op<1><0> op<1><1> op<2><0> op<2><1> op<2><2> op<3><0>)";
		List<String> definitions = Arrays.asList(String.format("(exists (%s) (and %s %s))", StringLibrary.join(existsDeclarations, ' '), connectivityInvocation, libraryInvocation));
		Collection<IExpr> smtDefinitions = checkDefinitionExpressions(verification, container, definitions);
		
		checkDefinition(verification, container, declarations, smtDeclarations, definitions, smtDefinitions, "");
		arguments.removeAll(asList("in<0>", "in<1>", "in<2>", "out"));
		arguments.addAll(asList("true", "1", "3", "false"));
		Map<String, Object> values = MetaMap.newHashMap(asList("i != null", "i.size()", "i.get(\"n\")", "returnValue"), (List) asList(true, 1, 3, false));
		checkInvocationWithValues(verification, container, values, arguments);
	}
	
	@Test
	public void wellFormedProgramConstraint() throws ParserException {
		Constraint wellFormedProgram = new WellFormedProgramConstraint(smtlib());
		LocationVariableContainer container = locationVariableContainerExample();
		
		List<String> arguments = LocationVariable.expressionsOf(container.operatorsParametersAndOutput());
		checkArguments(wellFormedProgram, container, arguments);
		checkInvocation(wellFormedProgram, container, arguments);

		Map<String, String> types = typesFor(container);
		List<String> declarations = builtDeclarations(arguments, types);
		List<IDeclaration> smtDeclarations = checkParameters(wellFormedProgram, container, declarations);
		
		String acyclicityInvocation = "(Acyclicity L@op<0> L@op<1> L@op<2> L@op<3> L@op<0><0> L@op<0><1> L@op<1><0> L@op<1><1> L@op<2><0> L@op<2><1> L@op<2><2> L@op<3><0>)";
		String consistencyInvocation = "(Consistency L@op<0> L@op<1> L@op<2> L@op<3>)";
		String lineBoundInovcation = "(LineBound L@op<0> L@op<1> L@op<2> L@op<3> L@op<0><0> L@op<0><1> L@op<1><0> L@op<1><1> L@op<2><0> L@op<2><1> L@op<2><2> L@op<3><0> L@out)";
		List<String> definitions = Arrays.asList(acyclicityInvocation, consistencyInvocation, lineBoundInovcation);
		Collection<IExpr> smtDefinitions = checkDefinitionExpressions(wellFormedProgram, container, definitions);
		
		checkDefinition(wellFormedProgram, container, declarations, smtDeclarations, definitions, smtDefinitions, "and");
	}
	
	@Test
	public void invocationToFunctionWithoutParameters() throws ParserException {
		Expression<?> input = new Expression<>(Short.class, "2");
		Expression<?> output = new Expression<>(Short.class, "a");
		LocationVariableContainer containerWithoutOperands = new LocationVariableContainer((List) asList(input), (List) asList(), output);
		Constraint library = new LibraryConstraint(smtlib());
		IExpr actualInvocation = library.invocation(containerWithoutOperands);
		IExpr smtInvocation = symbolFrom("Library");
		assertTrue(areEquals(actualInvocation, smtInvocation));
	}
	
	@Test
	public void invocationWithArgumentsToFunctionWithoutOperands() throws ParserException {
		Expression<?> input = new Expression<>(Short.class, "2");
		Expression<?> output = new Expression<>(Short.class, "a");
		LocationVariableContainer containerWithoutOperands = new LocationVariableContainer((List) asList(input), (List) asList(), output);
		Constraint verification = new VerificationConstraint(smtlib());
		Map<String, Object> values = (Map) MetaMap.newHashMap(asList("2", "a"), asList(2, 3));
		IExpr actualInvocation = verification.invocationWithValues(containerWithoutOperands, values);
		IExpr smtInvocation = expressionFrom(format("(%s L@out 2 3)", verification.nameSymbol()));
		assertTrue(areEquals(actualInvocation, smtInvocation));
	}
	
	@Test
	public void invocationWithArgumentsToFunctionWithoutInputs() throws ParserException {
		Expression<?> output = new Expression<>(Short.class, "a");
		LocationVariableContainer containerWithoutOperands = new LocationVariableContainer((List) asList(), (List) asList(), output);
		Constraint verification = new VerificationConstraint(smtlib());
		IExpr actualInvocation = verification.invocation(containerWithoutOperands);
		IExpr smtInvocation = expressionFrom(format("(%s L@out out)", verification.nameSymbol()));
		assertTrue(areEquals(actualInvocation, smtInvocation));
	}
	
	private LocationVariableContainer locationVariableContainerExample() {
		Expression<Boolean> firstExpression = new Expression<>(Boolean.class, "i != null");
		Expression<Number> secondExpression = new Expression<>(Number.class, "i.size()");
		Expression<Number> thirdExpression = new Expression<>(Number.class, "i.get(\"n\")");
		Collection<Expression<?>> inputs = (List) Arrays.asList(firstExpression, secondExpression, thirdExpression);
		Collection<Operator<?>> operators = (List) Arrays.asList(BinaryOperator.addition(), BinaryOperator.or(), TernaryOperator.ifThenElse(), UnaryOperator.not());
		Expression<?> outputExpression = new Expression<>(Boolean.class, "returnValue");
		return new LocationVariableContainer(inputs, operators, outputExpression);
	}
	
	private List<IExpr> checkArguments(Constraint constraint, LocationVariableContainer container, List<String> arguments) throws ParserException {
		List<IExpr> actualArguments = constraint.invocationArguments(container);
		List<IExpr> smtArguments = expressionsFrom(arguments);
		assertTrue(haveSameElements((Collection) actualArguments, (Collection) smtArguments));
		return smtArguments;
	}
	
	private List<IDeclaration> checkParameters(Constraint constraint, LocationVariableContainer container, List<String> declarations) throws ParserException {
		List<IDeclaration> actualDeclarations = constraint.parameters(container);
		List<IDeclaration> smtDeclarations = declarationsFrom(declarations);
		assertTrue(haveSameElements((Collection) actualDeclarations, (Collection) smtDeclarations));
		return smtDeclarations;
	}
	
	private IExpr checkInvocation(Constraint constraint, LocationVariableContainer container, List<String> arguments) throws ParserException {
		String constraintName = constraint.nameSymbol().value();
		IExpr actualInvocation = constraint.invocation(container);
		String invocation = String.format("(%s %s)", constraintName, StringLibrary.join(arguments, ' '));
		IExpr smtInvocation = expressionFrom(invocation);
		assertTrue(areEquals(actualInvocation, smtInvocation));
		return smtInvocation;
	}
	
	private Collection<IExpr> checkDefinitionExpressions(Constraint constraint, LocationVariableContainer container, Collection<String> definitions) throws ParserException {
		Collection<IExpr> actualDefinitionExpressions = constraint.definitionExpressions(container);
		Collection<IExpr> smtDefinitions = expressionsFrom(definitions);
		assertTrue(haveSameElements((Collection) actualDefinitionExpressions, (Collection) smtDefinitions));
		return smtDefinitions;
	}
	
	private ICommand checkDefinition(Constraint constraint, LocationVariableContainer container, List<String> declarations, List<IDeclaration> smtDeclarations,
			Collection<String> expressions, Collection<IExpr> smtExpressions, String connector) {
		String constraintName = constraint.nameSymbol().value();
		ICommand actualCommand = constraint.definition(container);
		String definition = String.format("(%s %s)", connector, StringLibrary.join(expressions, ' '));
		if (expressions.size() == 1) {
			definition = expressions.toArray(new String[1])[0];
		}
		String command = String.format("(define-fun %s (%s) Bool %s)", constraintName, StringLibrary.join(declarations, ' '), definition);
		ICommand smtCommand = commandFrom(command);
		assertTrue(areEquals(actualCommand, smtCommand));
		return smtCommand;
	}
	
	private IExpr checkInvocationWithValues(Constraint constraint, LocationVariableContainer container, Map<String, Object> values, List<String> arguments) throws ParserException {
		String constraintName = constraint.nameSymbol().value();
		IExpr actualInvocation = constraint.invocationWithValues(container, values);
		String invocation = format("(%s %s)", constraintName, StringLibrary.join(arguments, ' '));
		IExpr smtInvocation = expressionFrom(invocation);
		assertTrue(areEquals(actualInvocation, smtInvocation));
		return smtInvocation;
	}
	
	private Map<String,String> typesFor(LocationVariableContainer container) {
		Map<String, String> types = MetaMap.newHashMap();
		for (LocationVariable<?> locationVariable : container.allVariables()) {
			types.put(locationVariable.expression(), "Int");
			Class<?> type = locationVariable.objectTemplate().type();
			String subexpressionType = "Real";
			if (type.equals(Boolean.class)) {
				subexpressionType = "Bool";
			} else if (type.equals(Integer.class)) {
				subexpressionType = "Int";
			}
			types.put(locationVariable.subexpression(), subexpressionType);
		}
		return types;
	}
 	
	private List<String> builtDeclarations(List<String> expectedArguments, Map<String, String> types) {
		List<String> declarations = MetaList.newArrayList();
		for (String expectedArgument : expectedArguments) {
			declarations.add("(" + expectedArgument + " " + types.get(expectedArgument) + ")");
		}
		return declarations;
	}
}
