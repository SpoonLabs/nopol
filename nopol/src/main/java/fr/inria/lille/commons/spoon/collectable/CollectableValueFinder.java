package fr.inria.lille.commons.spoon.collectable;

import static fr.inria.lille.commons.spoon.util.SpoonElementLibrary.hasStaticModifier;
import static fr.inria.lille.commons.spoon.util.SpoonElementLibrary.hasVisibilityOf;
import static fr.inria.lille.commons.spoon.util.SpoonElementLibrary.isField;
import static fr.inria.lille.commons.spoon.util.SpoonElementLibrary.isLocalVariable;
import static fr.inria.lille.commons.spoon.util.SpoonElementLibrary.isParameter;
import static java.util.Arrays.asList;

import java.util.Collection;

import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtWhile;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.filter.CompositeFilter;
import spoon.reflect.visitor.filter.FilteringOperator;
import xxl.java.container.classic.MetaSet;
import fr.inria.lille.commons.spoon.filter.BeforeLocationFilter;
import fr.inria.lille.commons.spoon.filter.InBlockFilter;
import fr.inria.lille.commons.spoon.filter.VariableAssignmentFilter;

public class CollectableValueFinder {
	
	public static CollectableValueFinder firstInstance() {
		/* Refer to Singleton.createSingleton() */
		return new CollectableValueFinder();
	}
	
	protected CollectableValueFinder() {}
	
	public Collection<String> findFromIf(CtIf ifStatement) {
		Collection<String> collectables = findFromStatement(ifStatement);
		addCollectableSubconditions(collectables, ifStatement.getCondition());
		return collectables;
	}
	
	public Collection<String> findFromWhile(CtWhile loop) {
		Collection<String> collectables = findFromStatement(loop);
		addCollectableSubconditions(collectables, loop.getLoopingExpression());
		return collectables;
	}
	
	public Collection<String> findFromStatement(CtStatement statement) {
		ReachableVariableVisitor variableVisitor = new ReachableVariableVisitor(statement);
		Collection<CtVariable<?>> reachedVariables = variablesInitializedBefore(statement, variableVisitor.reachedVariables());
		return collectableNames(statement, reachedVariables);
	}
	
	protected Collection<String> collectableNames(CtStatement statement, Collection<CtVariable<?>> reachedVariables) {
		Collection<String> names = MetaSet.newHashSet();
		for (CtVariable<?> variable : reachedVariables) {
			names.add(nameFor(variable));
			if (isParameter(variable)) {
				addVisibleFields(names, statement, variable);
			}
		}
		return names;
	}
	
	private void addVisibleFields(Collection<String> names, CtStatement statement, CtVariable<?> variable) {
		String variableName = nameFor(variable);
		for (CtFieldReference<?> fieldReference : variable.getType().getAllFields()) {
			CtField<?> field = fieldReference.getDeclaration();
			if (field != null && hasVisibilityOf(field, statement)) {
				names.add(nameForField(field, variableName));
			}
		}
	}
	
	private String nameFor(CtVariable<?> variable) {
		if (isField(variable)) {
			return nameForField((CtField<?>) variable, null);
		}
		return variable.getSimpleName();
	}
	
	private String nameForField(CtField<?> field, String fieldOwner) {
		String fieldName = field.getSimpleName();
		CtSimpleType<?> declaringType = field.getDeclaringType();
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
	
	private void addCollectableSubconditions(Collection<String> collectables, CtExpression<Boolean> condition) {
		SubconditionVisitor extractor = new SubconditionVisitor(condition);
		collectables.addAll(extractor.subexpressions());
	}
	
	private Collection<CtVariable<?>> variablesInitializedBefore(CtStatement statement, Collection<CtVariable<?>> reachedVariables) {
		Collection<CtVariable<?>> initializedVariables = MetaSet.newHashSet();
		for (CtVariable<?> variable : reachedVariables) {
			if (! isLocalVariable(variable) || wasInitializedBefore(statement, variable)) {
				initializedVariables.add(variable);
			}
		}
		return initializedVariables;
	}
	
	private boolean wasInitializedBefore(CtStatement statement, CtVariable<?> variable) {
		if (variable.getDefaultExpression() == null) {
			CtBlock<?> block = variable.getParent(CtBlock.class);
			Filter<CtAssignment<?,?>> filter = initializationAssignmentsFilterFor(variable, statement);
			return ! block.getElements(filter).isEmpty();
		}
		return true;
	}
	
	private Filter<CtAssignment<?,?>> initializationAssignmentsFilterFor(CtVariable<?> variable, CtStatement statement) {
		VariableAssignmentFilter variableAssignment = new VariableAssignmentFilter(variable);
		BeforeLocationFilter<CtAssignment<?,?>> beforeLocation = new BeforeLocationFilter(CtAssignment.class, statement.getPosition());
		InBlockFilter<CtAssignment<?,?>> inVariableDeclarationBlock = new InBlockFilter(CtAssignment.class, asList(variable.getParent(CtBlock.class)));
		InBlockFilter<CtAssignment<?,?>> inStatementBlock = new InBlockFilter(CtAssignment.class, asList(statement.getParent(CtBlock.class)));
		Filter<CtAssignment<?,?>> inBlockFilter = new CompositeFilter(FilteringOperator.UNION, inStatementBlock, inVariableDeclarationBlock);
		return new CompositeFilter(FilteringOperator.INTERSECTION, variableAssignment, beforeLocation, inBlockFilter);
	}
}
