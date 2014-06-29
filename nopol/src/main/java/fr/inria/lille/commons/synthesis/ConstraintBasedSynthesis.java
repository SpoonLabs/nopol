package fr.inria.lille.commons.synthesis;

import static java.lang.String.format;
import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.smtlib.ICommand.IScript;
import org.smtlib.IExpr;

import fr.inria.lille.commons.classes.ClassLibrary;
import fr.inria.lille.commons.collections.CollectionLibrary;
import fr.inria.lille.commons.collections.ListLibrary;
import fr.inria.lille.commons.collections.MapLibrary;
import fr.inria.lille.commons.synthesis.expression.Expression;
import fr.inria.lille.commons.synthesis.operator.Operator;
import fr.inria.lille.commons.synthesis.smt.SMTLib;
import fr.inria.lille.commons.synthesis.smt.locationVariables.LocationVariable;
import fr.inria.lille.commons.synthesis.smt.locationVariables.LocationVariableContainer;
import fr.inria.lille.commons.synthesis.theory.OperatorTheory;
import fr.inria.lille.commons.trace.Specification;

public class ConstraintBasedSynthesis {

	public ConstraintBasedSynthesis() {
		this(MapLibrary.newHashMap(asList("-1", "0", "1"), asList(-1, 0, 1)), SynthesisTheoriesBuilder.theoriesForConstraintBasedSynthesis());
	}
	
	public ConstraintBasedSynthesis(Map<String, Integer> constants, Collection<OperatorTheory> theories) {
		this.theories = theories;
		this.constants = constants;
		scriptBuilder = new SynthesisScriptBuilder();
	}
	
	public <T> Expression<T> outputExpressionFor(Class<? extends T> outputClass) {
		Class<T> smtLibCompatibleClass = smtLibCompatibleClassFor(outputClass);
		return new Expression<>(smtLibCompatibleClass, "result");
	}
	
	public <T> CodeGenesis codesSynthesisedFrom(Class<T> outputClass, Collection<Specification<T>> specifications) {
		Expression<?> outputExpression = outputExpressionFor(outputClass);
		Collection<Map<String, Object>> extracted = extractedCollectedValues(specifications, outputExpression);
		Collection<Operator<?>> operators = ListLibrary.newLinkedList();
		Collection<Expression<?>> inputs = inputExpressions(extracted, outputExpression); 
		for (OperatorTheory theory : theories()) {
			operators.addAll(theory.operators());
			LocationVariableContainer container = new LocationVariableContainer(inputs, operators, outputExpression);
			Map<String, Integer> toInteger = satisfyingSolution(container, extracted);
			if (! toInteger.isEmpty()) {
				return new CodeGenesis(container, toInteger);
			}
		}
		return new NullCodeGenesis();
	}
	
	private <T> Collection<Map<String, Object>> extractedCollectedValues(Collection<Specification<T>> specifications, Expression<?> outputExpression) {
		Collection<Map<String, Object>> extracted = ListLibrary.newLinkedList();
		for (Specification<T> specification : specifications) {
			Map<String, Object> newMap = MapLibrary.newHashMap(specification.inputs());
			newMap.put(outputExpression.expression(), specification.output());
			newMap.putAll(constants());
			extracted.add(newMap);
		}
		return extracted;
	}
	
	protected Collection<Expression<?>> inputExpressions(Collection<Map<String, Object>> collectedValues, Expression<?> outputExpression) {
		// FIXME: what to do when keys of collectedValues do not match
		Map<String, Object> values = CollectionLibrary.any(collectedValues);
		Collection<Expression<?>> expressions = ListLibrary.newLinkedList();
		for (String value : values.keySet()) {
			expressions.add(new Expression<>((Class) smtLibCompatibleClassFor(values.get(value)), value));
		}
		expressions.remove(outputExpression);
		return expressions;
	}
	
	private Class<?> smtLibCompatibleClassFor(Object object) {
		return smtLibCompatibleClassFor(object.getClass());
	}
	
	private <T> Class<T> smtLibCompatibleClassFor(Class<? extends T> queriedClass) {
		if (ClassLibrary.isSubclassOf(Boolean.class, queriedClass)) {
			return (Class) Boolean.class;
		} else if (ClassLibrary.isSubclassOf(Number.class, queriedClass)) {
			return (Class) Number.class;
		}
		throw new IllegalStateException(format("SMT can only use Bool or Real types, the requested type is '%s'.", queriedClass.getName()));
	}
	
	protected Map<String, Integer> satisfyingSolution(LocationVariableContainer container, Collection<Map<String, Object>> collectedValues) {
		IScript smtScript = scriptBuilder().scriptFrom(container, collectedValues);
		Map<String, String> satisfyingValues = scriptSolution(container, smtScript);
		Map<String, Integer> toInteger = MapLibrary.valuesParsedAsInteger(satisfyingValues);
		return toInteger;
	}

	private Map<String, String> scriptSolution(LocationVariableContainer container, IScript smtScript) {
		SMTLib newSMTLib = new SMTLib();
		Collection<String> expressions = LocationVariable.expressionsOf(container.copyOfOperatorsParametersAndOutput());
		List<IExpr> smtExpressions = (List) scriptBuilder().smtlib().symbolsFor(expressions);
		Map<String, String> satisfyingValues = newSMTLib.satisfyingValuesFor(smtExpressions, smtScript);
		return satisfyingValues;
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
	
	private Map<String, Integer> constants;
	private Collection<OperatorTheory> theories;
	private SynthesisScriptBuilder scriptBuilder;
}
