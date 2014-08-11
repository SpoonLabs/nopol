package fr.inria.lille.commons.synthesis;

import static fr.inria.lille.commons.utils.LoggerLibrary.logCollection;
import static fr.inria.lille.commons.utils.LoggerLibrary.logDebug;
import static fr.inria.lille.commons.utils.LoggerLibrary.newLoggerFor;
import static java.lang.String.format;
import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.smtlib.ICommand.IScript;
import org.smtlib.IExpr.ISymbol;

import fr.inria.lille.commons.collections.CollectionLibrary;
import fr.inria.lille.commons.collections.ListLibrary;
import fr.inria.lille.commons.collections.MapLibrary;
import fr.inria.lille.commons.synthesis.expression.Expression;
import fr.inria.lille.commons.synthesis.operator.Operator;
import fr.inria.lille.commons.synthesis.smt.SMTLib;
import fr.inria.lille.commons.synthesis.smt.locationVariables.LocationVariable;
import fr.inria.lille.commons.synthesis.smt.locationVariables.LocationVariableContainer;
import fr.inria.lille.commons.synthesis.smt.solver.SolverFactory;
import fr.inria.lille.commons.synthesis.theory.OperatorTheory;
import fr.inria.lille.commons.trace.Specification;
import fr.inria.lille.commons.utils.ClassLibrary;

public class ConstraintBasedSynthesis {
	
	public ConstraintBasedSynthesis() {
		this(SolverFactory.solverLogic());
	}
	
	public ConstraintBasedSynthesis(ISymbol logic) {
		this(logic, MapLibrary.newHashMap(asList("-1", "0", "1"), asList(-1, 0, 1)), SynthesisTheoriesBuilder.theoriesForConstraintBasedSynthesis(logic));
	}
	
	public ConstraintBasedSynthesis(ISymbol logic, Map<String, Integer> constants, Collection<OperatorTheory> theories) {
		this.logic = logic;
		this.theories = theories;
		this.constants = constants;
		scriptBuilder = new SynthesisScriptBuilder();
	}
	
	public <T> CodeGenesis codesSynthesisedFrom(Class<T> outputClass, Collection<Specification<T>> specifications) {
		Collection<Operator<?>> operators = ListLibrary.newLinkedList();
		Expression<?> outputExpression = outputExpressionFor(outputClass);
		Collection<Map<String, Object>> synthesisInputs = synhtesisInputValues(specifications, outputExpression);
		Collection<Expression<?>> inputs = inputExpressions(synthesisInputs, outputExpression); 
		for (OperatorTheory theory : theories()) {
			operators.addAll(theory.operators());
			LocationVariableContainer container = variableContainerFor(outputExpression, operators, inputs);
			Map<String, Integer> toInteger = satisfyingSolution(container, synthesisInputs);
			if (! toInteger.isEmpty()) {
				return successfulGenesis(container, toInteger);
			}
		}
		return unsuccessfulGenesis();
	}
	
	protected Expression<?> outputExpressionFor(Class<?> outputClass) {
		Class<?> smtLibCompatibleClass = compatibleClassFor(outputClass);
		return new Expression(smtLibCompatibleClass, "result");
	}

	private <T> Collection<Map<String, Object>> synhtesisInputValues(Collection<Specification<T>> specifications, Expression<?> outputExpression) {
		logCollection(logger, "Specifications:", specifications);
		Collection<Map<String, Object>> synthesisInputs = ListLibrary.newLinkedList();
		for (Specification<T> specification : specifications) {
			Map<String, Object> newMap = specification.inputs();
			newMap.put(outputExpression.expression(), specification.output());
			newMap.putAll(constants());
			synthesisInputs.add(compatibleValuesFrom(newMap));
		}
		return synthesisInputs;
	}
	
	private <T> Map<String, Object> compatibleValuesFrom(Map<String, Object> map) {
		Map<String, Object> collectedValues = MapLibrary.newHashMap();
		for (String key : map.keySet()) {
			Object compatibleValue = compatibleValueOf(map.get(key));
			collectedValues.put(key, compatibleValue);
		}
		return collectedValues;
	}
	
	private Object compatibleValueOf(Object value) {
		if (Integer.class.isInstance(value)) {
			value = Double.valueOf((int) value);
		}
		return value;
	}
	
	protected Collection<Expression<?>> inputExpressions(Collection<Map<String, Object>> collectedValues, Expression<?> outputExpression) {
		// FIXME: what to do when keys of collectedValues do not match; intersection of keys?
		Collection<Expression<?>> expressions = ListLibrary.newLinkedList();
		Map<String, Object> anyValueMap = CollectionLibrary.any(collectedValues);
		Collection<String> variableNames = MapLibrary.keySetIntersection(collectedValues);
		for (String variableName : variableNames) {
			Class<?> compatibleClass = compatibleClassOf(anyValueMap.get(variableName));
			expressions.add(new Expression(compatibleClass, variableName));
		}
		expressions.remove(outputExpression);
		return expressions;
	}
	
	private Class<?> compatibleClassFor(Class<?> queriedClass) {
		if (ClassLibrary.isSubclassOf(Boolean.class, queriedClass)) {
			return Boolean.class;
		} else if (ClassLibrary.isSubclassOf(Number.class, queriedClass)) {
			return Number.class;
		}
		throw new IllegalStateException(format("SMT can only use Bool or Real types, the requested type is '%s'.", queriedClass.getName()));
	}
	
	private Class<?> compatibleClassOf(Object object) {
		return compatibleClassFor(object.getClass());
	}
	
	private LocationVariableContainer variableContainerFor(Expression<?> outputExpression, Collection<Operator<?>> operators, Collection<Expression<?>> inputs) {
		logCollection(logger, "Operators:", operators);
		return new LocationVariableContainer(inputs, operators, outputExpression);
	}
	
	protected Map<String, Integer> satisfyingSolution(LocationVariableContainer container, Collection<Map<String, Object>> synthesisInputs) {
		IScript smtScript = scriptBuilder().scriptFrom(logic(), container, synthesisInputs);
		logCollection(logger, "SMTLib Script:", smtScript.commands());
		Map<String, String> satisfyingValues = scriptSolution(container, smtScript);
		Map<String, Integer> toInteger = MapLibrary.valuesParsedAsInteger(satisfyingValues);
		return toInteger;
	}

	private Map<String, String> scriptSolution(LocationVariableContainer container, IScript smtScript) {
		SMTLib newSMTLib = new SMTLib();
		List<LocationVariable<?>> variables = container.operatorsParametersAndOutput(); 
		Collection<String> expressions = LocationVariable.expressionsOf(variables);
		List<ISymbol> smtExpressions = newSMTLib.symbolsFor(expressions);
		Map<String, String> satisfyingValues = newSMTLib.satisfyingValuesFor(smtExpressions, smtScript);
		return satisfyingValues;
	}
	
	private CodeGenesis successfulGenesis(LocationVariableContainer container, Map<String, Integer> toInteger) {
		CodeGenesis genesis = new CodeGenesis(container, toInteger);
		logDebug(logger, "Successful code synthesis: " + genesis.returnStatement());
		return genesis;
	}
	
	private NullCodeGenesis unsuccessfulGenesis() {
		logDebug(logger, "Failed code synthesis, returning NullCodeGenesis");
		return new NullCodeGenesis();
	}
	
	private Collection<OperatorTheory> theories() {
		return theories;
	}
	
	private SynthesisScriptBuilder scriptBuilder() {
		return scriptBuilder;
	}
	
	protected Map<String, Integer> constants() {
		return constants;
	}
	
	private ISymbol logic() {
		return logic;
	}

	private ISymbol logic;
	private Map<String, Integer> constants;
	private Collection<OperatorTheory> theories;
	private SynthesisScriptBuilder scriptBuilder;
	private static Logger logger = newLoggerFor(ConstraintBasedSynthesis.class);
}
