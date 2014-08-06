/*
 * Copyright (C) 2013 INRIA
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package fr.inria.lille.commons.trace;

import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.List;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.filter.CompositeFilter;
import spoon.reflect.visitor.filter.FilteringOperator;
import fr.inria.lille.commons.collections.ListLibrary;
import fr.inria.lille.commons.collections.SetLibrary;
import fr.inria.lille.commons.spoon.ReachableVariableVisitor;
import fr.inria.lille.commons.spoon.filter.BeforeLocationFilter;
import fr.inria.lille.commons.spoon.filter.InBlockFilter;
import fr.inria.lille.commons.spoon.filter.VariableAssignmentFilter;
import fr.inria.lille.commons.spoon.util.SpoonElementLibrary;
import fr.inria.lille.commons.spoon.util.SpoonModelLibrary;
import fr.inria.lille.commons.spoon.util.SpoonStatementLibrary;

public class RuntimeValuesProcessor<T extends CtCodeElement> extends AbstractProcessor<T> {
	
	protected CtStatement statementOf(T codeElement) {
		return SpoonStatementLibrary.statementOf(codeElement);
	}
	
	@Override
	public void process(T codeElement) {
		CtStatement statement = statementOf(codeElement);
		Collection<CtStatement> collectionStatements = valueCollectionStatements(statement);
		insertNewStatements(statement, collectionStatements);
	}
	
	public void insertNewStatements(CtStatement statement, Collection<? extends CtStatement> newStatements) {
		for (CtStatement newStatement : newStatements) {
			statement.insertBefore(newStatement);
		}
	}
	
	public List<CtStatement> valueCollectionStatements(CtStatement statement) {
		Collection<String> variableNames = reachableVariableNames(statement);
		return asCollectionStatements(variableNames, statement);
	}
	public Collection<String> reachableVariableNames(CtStatement statement) {
		Collection<CtVariable<?>> reachableVariables = reachableVariables(statement);
		return variableNames(reachableVariables);
	}
	
	private Collection<CtVariable<?>> reachableVariables(CtStatement statement) {
		ReachableVariableVisitor variableVisitor = new ReachableVariableVisitor(statement);
		Collection<CtVariable<?>> reachedVariables = variableVisitor.reachedVariables();
		return variablesInitializedBefore(statement, reachedVariables);
	}
	
	protected Collection<String> variableNames(Collection<CtVariable<?>> reachedVariables) {
		Collection<String> names = SetLibrary.newHashSet();
		for (CtVariable<?> variable : reachedVariables) {
			names.add(nameFor(variable));
		}
		return names;
	}
	
	private String nameFor(CtVariable<?> variable) {
		String simpleName = variable.getSimpleName();
		if (SpoonElementLibrary.isField(variable)) {
			String declaringClass = ((CtField<?>) variable).getDeclaringType().getSimpleName();
			if (SpoonElementLibrary.hasStaticModifier(variable)) {
				simpleName = declaringClass + "." + simpleName;
			} else {
				if (declaringClass.isEmpty()) {
					/* only when 'variable' is a field of an Anonymous Class */
					simpleName = "this." + simpleName;
				} else {
					simpleName = declaringClass + ".this." + simpleName;
				}
			}
		}
		return simpleName;
	}
	
	protected List<CtStatement> asCollectionStatements(Collection<String> variableNames, CtStatement statement) {
		List<CtStatement> newStatements = ListLibrary.newLinkedList();
		for (String variableName : variableNames) {
			String invocation = valueCollectingSnippet(variableName);
			CtStatement newStatement = SpoonModelLibrary.newStatementFromSnippet(statement.getFactory(), invocation, statement.getParent());
			newStatements.add(newStatement);
		}
		return newStatements;
	}
	
	protected String valueCollectingSnippet(String variableName) {
		return RuntimeValues.collectValueInvocation(variableName);
	}

	private Collection<CtVariable<?>> variablesInitializedBefore(CtStatement statement, Collection<CtVariable<?>> reachedVariables) {
		Collection<CtVariable<?>> initializedVariables = SetLibrary.newHashSet();
		for (CtVariable<?> variable : reachedVariables) {
			if (! SpoonElementLibrary.isLocalVariable(variable) || wasInitializedBefore(statement, variable)) {
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
