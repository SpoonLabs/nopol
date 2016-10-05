package fr.inria.lille.commons.synthesis;

import fr.inria.lille.commons.synthesis.expression.Expression;
import fr.inria.lille.commons.synthesis.operator.Operator;
import fr.inria.lille.commons.synthesis.smt.SMTLib;
import fr.inria.lille.commons.synthesis.smt.locationVariables.LocationVariable;
import fr.inria.lille.commons.synthesis.smt.locationVariables.LocationVariableContainer;
import fr.inria.lille.commons.synthesis.smt.solver.SolverFactory;
import fr.inria.lille.commons.synthesis.theory.OperatorTheory;
import fr.inria.lille.commons.trace.Specification;
import org.slf4j.Logger;
import org.smtlib.ICommand.IScript;
import org.smtlib.IExpr.ISymbol;
import xxl.java.container.classic.MetaCollection;
import xxl.java.container.classic.MetaList;
import xxl.java.container.classic.MetaMap;
import xxl.java.container.map.Multimap;
import xxl.java.library.ClassLibrary;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static xxl.java.library.LoggerLibrary.*;

public class ConstraintBasedSynthesis {

    private ISymbol logic;
    private Map<String, Number> constants;
    private Collection<OperatorTheory> theories;
    private SynthesisScriptBuilder scriptBuilder;
    public static int level = 0;
    public static Collection<Operator<?>> operators;

    public ConstraintBasedSynthesis(Map<String, Number> constants) {
        this.logic = SolverFactory.solverLogic();
        this.theories = SynthesisTheoriesBuilder.theoriesForConstraintBasedSynthesis(logic);
        this.constants = constants;
        scriptBuilder = new SynthesisScriptBuilder();
        level = 0;
    }

    public ConstraintBasedSynthesis() {
        this(SolverFactory.solverLogic());
    }

    public ConstraintBasedSynthesis(ISymbol logic) {
        this(logic, MetaMap.<String, Number>newHashMap(asList("-1", "0", "1"), Arrays.<Number>asList(-1, 0, 1)), SynthesisTheoriesBuilder.theoriesForConstraintBasedSynthesis(logic));
    }

    public ConstraintBasedSynthesis(ISymbol logic, Map<String, Number> constants, Collection<OperatorTheory> theories) {
        this.logic = logic;
        this.theories = theories;
        this.constants = constants;
        scriptBuilder = new SynthesisScriptBuilder();
        level = 0;
    }

    public <T> CodeGenesis codesSynthesisedFrom(Class<T> outputClass, Collection<Specification<T>> specifications) {
        Collection<Operator<?>> operators = MetaList.newLinkedList();
        Expression<?> outputExpression = outputExpressionFor(outputClass);
        Collection<Map<String, Object>> synthesisInputs = synthesisInputValues(specifications, outputExpression);
        Collection<Expression<?>> inputs = inputExpressions(synthesisInputs, outputExpression);
        for (OperatorTheory theory : theories()) {
            level++;
            ConstraintBasedSynthesis.operators = operators;
            operators.addAll(theory.operators());
            LocationVariableContainer container = variableContainerFor(outputExpression, operators, inputs);
            Map<String, Integer> toInteger = satisfyingSolution(container, synthesisInputs);
            if (!toInteger.isEmpty()) {
                return successfulGenesis(container, toInteger);
            }
        }
        return unsuccessfulGenesis();
    }

    protected Expression<?> outputExpressionFor(Class<?> outputClass) {
        Class<?> smtLibCompatibleClass = compatibleClassFor(outputClass);
        return new Expression(smtLibCompatibleClass, "result");
    }

    private <T> Collection<Map<String, Object>> synthesisInputValues(Collection<Specification<T>> specifications, Expression<?> outputExpression) {
        logCollection(logger(), "Specifications:", specifications);
        Collection<Map<String, Object>> synthesisInputs = MetaList.newLinkedList();
        for (Specification<T> specification : specifications) {
            Map<String, Object> newMap = specification.inputs();
            newMap.put(outputExpression.expression(), specification.output());
            newMap.putAll(constants());
            synthesisInputs.add(compatibleValuesFrom(newMap));
        }
        reduceInputsWithConstants(synthesisInputs);
        return synthesisInputs;
    }

    protected Collection<String> reduceInputsWithConstants(Collection<Map<String, Object>> inputs) {
        Collection<Number> constantValues = constants().values();
        Collection<String> duplicates = MetaList.newLinkedList();
        Multimap<String, Object> multimap = Multimap.newSetMultimap();
        multimap.addAll(inputs);
        for (String key : multimap.keySet()) {
            Collection<Object> collection = multimap.get(key);
            if (collection.size() == 1 && constantValues.containsAll(collection)) {
                duplicates.add(key);
            }
        }
        MetaMap.removeKeysInAll(duplicates, inputs);
        return duplicates;
    }

    private <T> Map<String, Object> compatibleValuesFrom(Map<String, Object> map) {
        Map<String, Object> collectedValues = MetaMap.newHashMap();
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
        if (Character.class.isInstance(value)) {
            value = Double.valueOf((int) (char) value);
        }
        return value;
    }

    protected Collection<Expression<?>> inputExpressions(Collection<Map<String, Object>> collectedValues, Expression<?> outputExpression) {
        Collection<Expression<?>> expressions = MetaList.newLinkedList();
        Map<String, Object> anyValueMap = MetaCollection.any(collectedValues);
        Collection<String> variableNames = MetaMap.keySetIntersection(collectedValues);
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
        } else if (ClassLibrary.isSubclassOf(Character.class, queriedClass)) {
            return Number.class;
        }
        throw new IllegalStateException(format("SMT can only use Bool or Real types, the requested type is '%s'.", queriedClass.getName()));
    }

    private Class<?> compatibleClassOf(Object object) {
        return compatibleClassFor(object.getClass());
    }

    private LocationVariableContainer variableContainerFor(Expression<?> outputExpression, Collection<Operator<?>> operators, Collection<Expression<?>> inputs) {
        logCollection(logger(), "Operators:", operators);
        return new LocationVariableContainer(inputs, operators, outputExpression);
    }

    protected Map<String, Integer> satisfyingSolution(LocationVariableContainer container, Collection<Map<String, Object>> synthesisInputs) {
        IScript smtScript = scriptBuilder().scriptFrom(logic(), container, synthesisInputs);
        logCollection(logger(), "SMTLib Script:", smtScript.commands());
        Map<String, String> satisfyingValues = scriptSolution(container, smtScript);
        Map<String, Integer> toInteger = MetaMap.valuesParsedAsInteger(satisfyingValues);
        return toInteger;
    }

    private Map<String, String> scriptSolution(LocationVariableContainer container, IScript smtScript) {
        SMTLib newSMTLib = new SMTLib();
        List<LocationVariable<?>> variables = container.operatorsParametersAndOutput();
        Collection<String> expressions = LocationVariable.expressionsOf(variables);
        List<ISymbol> smtExpressions = newSMTLib.symbolsFor(expressions);
        Map<String, String> satisfyingValues = newSMTLib.anySolutionFor(smtScript, smtExpressions);
        return satisfyingValues;
    }

    private CodeGenesis successfulGenesis(LocationVariableContainer container, Map<String, Integer> toInteger) {
        CodeGenesis genesis = new CodeGenesis(container, toInteger);
        logDebug(logger(), "Successful code synthesis: " + genesis.returnStatement());
        return genesis;
    }

    private NullCodeGenesis unsuccessfulGenesis() {
        logDebug(logger(), "Failed code synthesis, returning NullCodeGenesis");
        return new NullCodeGenesis();
    }

    private Collection<OperatorTheory> theories() {
        return theories;
    }

    private SynthesisScriptBuilder scriptBuilder() {
        return scriptBuilder;
    }

    protected Map<String, Number> constants() {
        return constants;
    }

    private ISymbol logic() {
        return logic;
    }

    private Logger logger() {
        return loggerFor(this);
    }


}
