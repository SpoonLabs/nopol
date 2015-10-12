package fr.inria.lille.commons.spoon.collectable;

import fr.inria.lille.commons.spoon.filter.BeforeLocationFilter;
import fr.inria.lille.commons.spoon.filter.InBlockFilter;
import fr.inria.lille.commons.spoon.filter.VariableAssignmentFilter;
import org.slf4j.Logger;
import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.filter.CompositeFilter;
import spoon.reflect.visitor.filter.FilteringOperator;
import xxl.java.container.classic.MetaSet;
import xxl.java.container.map.Multimap;

import java.util.Collection;

import static fr.inria.lille.commons.spoon.util.SpoonElementLibrary.*;
import static fr.inria.lille.commons.spoon.util.SpoonMethodLibrary.isGetter;
import static fr.inria.lille.commons.spoon.util.SpoonReferenceLibrary.accessibleFieldsFrom;
import static fr.inria.lille.commons.spoon.util.SpoonReferenceLibrary.accessibleMethodsFrom;
import static java.util.Arrays.asList;
import static xxl.java.library.LoggerLibrary.loggerFor;

public class CollectableValueFinder {

    public static CollectableValueFinder valueFinderFromWhile(CtWhile loop) {
        CollectableValueFinder finder = new CollectableValueFinder();
        finder.findFromWhile(loop);
        return finder;
    }

    public static CollectableValueFinder valueFinderFromIf(CtIf ifStatement) {
        CollectableValueFinder finder = new CollectableValueFinder();
        finder.findFromIf(ifStatement);
        return finder;
    }

    public static CollectableValueFinder valueFinderFrom(CtStatement statement) {
        CollectableValueFinder finder = new CollectableValueFinder();
        finder.findFromStatement(statement);
        return finder;
    }

    private CollectableValueFinder() {
        accessibleGetters = Multimap.newSetMultimap();
        reachableVariables = MetaSet.newHashSet();
    }

    public Collection<String> reachableVariables() {
        return reachableVariables;
    }

    public Multimap<String, String> accessibleGetters() {
        return accessibleGetters;
    }

    protected void findFromIf(CtIf ifStatement) {
        findFromStatement(ifStatement);
        addCollectableSubconditions(ifStatement.getCondition());
    }

    protected void findFromWhile(CtWhile loop) {
        findFromStatement(loop);
        addCollectableSubconditions(loop.getLoopingExpression());
    }

    protected void findFromStatement(CtStatement statement) {
        CtType parentType;
        try {
            parentType = parentOfType(CtType.class, statement);
        } catch (ParentNotInitializedException e) {
            return;
        }
        if (parentType == null) {
            return;
        }
        ReachableVariableVisitor variableVisitor = new ReachableVariableVisitor(statement);
        Collection<CtVariable<?>> reachedVariables = variablesInitializedBefore(statement, variableVisitor.reachedVariables());
        addVariableNames(reachedVariables);
        CtTypeReference<?> typeReference = parentType.getReference();
        addVisibleFieldsOfParameters(reachedVariables, typeReference);
        addGettersOfFields(reachedVariables, typeReference);
    }

    protected void addVariableNames(Collection<CtVariable<?>> reachedVariables) {
        for (CtVariable<?> variable : reachedVariables) {
            if (variable.getSimpleName().equals("serialVersionUID")) {
                continue;
            }
            reachableVariables().add(nameFor(variable));
        }
    }

    protected void addVisibleFieldsOfParameters(Collection<CtVariable<?>> reachedVariables, CtTypeReference<?> type) {
        for (CtVariable<?> variable : reachedVariables) {
            String variableName = nameFor(variable);
            if (isParameter(variable)) {
                addVisibleFields(type, variable, variableName);
            }
        }
    }

    protected void addVisibleFields(CtTypeReference<?> type, CtVariable<?> variable, String variableName) {
        Collection<CtField<?>> fields = accessibleFieldsFrom(type, variable.getType());
        for (CtField<?> field : fields) {
            String name = nameForField(field, variableName);
            reachableVariables().add(name);
        }
    }

    protected void addGettersOfFields(Collection<CtVariable<?>> reachedVariables, CtTypeReference<?> type) {
        for (CtVariable<?> variable : reachedVariables) {
            if (isField(variable)) {
                addGetters(type, variable, nameFor(variable));
            }
        }
    }

    protected void addGetters(CtTypeReference<?> type, CtVariable<?> variable, String variableName) {
        Collection<CtMethod<?>> methods = accessibleMethodsFrom(type, variable.getType());
        for (CtMethod<?> method : methods) {
            if (isGetter(method)) {
                accessibleGetters().add(variableName, method.getSimpleName());
            }
        }
    }

    protected String nameFor(CtVariable<?> variable) {
        if (isField(variable)) {
            return nameForField((CtField<?>) variable, null);
        }
        return variable.getSimpleName();
    }

    protected String nameForField(CtField<?> field, String fieldOwner) {
        String fieldName = field.getSimpleName();
        CtType<?> declaringType = field.getDeclaringType();
        String declaringClass = declaringType.getQualifiedName().replace('$', '.');
        if (hasStaticModifier(field)) {
            fieldName = declaringClass + "." + fieldName;
        } else if (declaringType.getSimpleName().isEmpty()) {
            /* only when 'variable' is a field of an Anonymous Class */
            fieldName = "this." + fieldName;
        } else if (fieldOwner == null) {
            fieldName = declaringClass + ".this." + fieldName;
        } else {
            fieldName = fieldOwner + "." + fieldName;
        }
        return fieldName;
    }

    private void addCollectableSubconditions(CtExpression<Boolean> condition) {
        SubconditionVisitor extractor = new SubconditionVisitor(condition);
        reachableVariables().addAll(extractor.subexpressions());
    }

    private Collection<CtVariable<?>> variablesInitializedBefore(CtStatement statement, Collection<CtVariable<?>> reachedVariables) {
        Collection<CtVariable<?>> initializedVariables = MetaSet.newHashSet();
        for (CtVariable<?> variable : reachedVariables) {
            if (!isLocalVariable(variable) || wasInitializedBefore(statement, variable)) {
                initializedVariables.add(variable);
            }
        }
        return initializedVariables;
    }

    private boolean wasInitializedBefore(CtStatement statement, CtVariable<?> variable) {
        if (variable.getDefaultExpression() == null) {
            CtBlock<?> block = variable.getParent(CtBlock.class);
            Filter<CtAssignment<?, ?>> filter = initializationAssignmentsFilterFor(variable, statement);
            return !block.getElements(filter).isEmpty();
        }
        return true;
    }

    private Filter<CtAssignment<?, ?>> initializationAssignmentsFilterFor(CtVariable<?> variable, CtStatement statement) {
        VariableAssignmentFilter variableAssignment = new VariableAssignmentFilter(variable);
        BeforeLocationFilter<CtAssignment<?, ?>> beforeLocation = new BeforeLocationFilter(CtAssignment.class, statement.getPosition());
        InBlockFilter<CtAssignment<?, ?>> inVariableDeclarationBlock = new InBlockFilter(CtAssignment.class, asList(variable.getParent(CtBlock.class)));
        InBlockFilter<CtAssignment<?, ?>> inStatementBlock = new InBlockFilter(CtAssignment.class, asList(statement.getParent(CtBlock.class)));
        Filter<CtAssignment<?, ?>> inBlockFilter = new CompositeFilter(FilteringOperator.UNION, inStatementBlock, inVariableDeclarationBlock);
        return new CompositeFilter(FilteringOperator.INTERSECTION, variableAssignment, beforeLocation, inBlockFilter);
    }

    protected Logger logger() {
        return loggerFor(this);
    }

    private Multimap<String, String> accessibleGetters;
    private Collection<String> reachableVariables;
}
