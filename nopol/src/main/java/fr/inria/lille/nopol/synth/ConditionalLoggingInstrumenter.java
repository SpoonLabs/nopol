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
package fr.inria.lille.nopol.synth;

import static spoon.reflect.declaration.ModifierKind.STATIC;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.LoggerFactory;

import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.CtAbstractVisitor;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.filter.TypeFilter;
import fr.inria.lille.nopol.synth.collector.ValuesCollector;

/**
 * 
 * Adds basic logging before each conditionals (if, loops). Use basic scope inference (the real one is hard due to the
 * complex semantics of "static" and "final" (w.r.t. init, anonymous classes, etc.)
 * 
 */
final class ConditionalLoggingInstrumenter implements Processor {

	private static final class VariablesInLocalScopeVisitor extends CtAbstractVisitor {
		private final Set<CtElement> stoppers;
		private final Set<CtVariable<?>> variables;

		private VariablesInLocalScopeVisitor(final Set<CtElement> stoppers, final Set<CtVariable<?>> variables) {
			this.stoppers = stoppers;
			this.variables = variables;
		}

		// for a block we add the local variables
		@Override
		public <R> void visitCtBlock(final CtBlock<R> block) {
			for (CtStatement stmt : block.getStatements()) {
				// we can not add variables that are declared after the stoppers
				if (stoppers.contains(stmt)) {
					return;
				}

				// we only add the new local variables
				if (stmt instanceof CtLocalVariable) {
					variables.add((CtVariable<?>) stmt);
				}
			}
		}

		// for a class we add the fields
		@Override
		public <T> void visitCtClass(final CtClass<T> ctClass) {
			for (CtField<?> field : ctClass.getFields()) {
				variables.add(field);
			}
		}

		// for a method we add the parameters
		@Override
		public <T> void visitCtMethod(final CtMethod<T> m) {
			for (CtParameter<?> param : m.getParameters()) {
				variables.add(param);
			}
		}

		@Override
		public <T> void visitCtThisAccess(CtThisAccess<T> thisAccess) {
		}

		
	}

	private static final String VALUES_COLLECTOR_CALL = ValuesCollector.class.getName() + ".collectValue(\"";
	private static final String NULLNESS_COLLECTOR_CALL = ValuesCollector.class.getName() + ".collectNullness(\"";

	
	/**
	 * Returns all variables in this scope if el does not define a scope, returns an empty set
	 * 
	 */
	private Collection<CtVariable<?>> _getVariablesInScope(final CtElement el, final Set<CtElement> children) {
		final Set<CtVariable<?>> variables = new HashSet<>();

		// we add all variables in the scope of el
		variables.addAll(getVariablesInLocalScope(el, children));

		// recursion: we collect all variables in this scope
		// and in the scope of its parent
		if (
				// if we have parent
				el.getParent() != null

				// but a package does not define a scope
				&& !CtPackage.class.isAssignableFrom(el.getParent().getClass())

				// there are complex compilation rules with final fields
				// and anonymous classes, skip parents of anonymous classes
				&& !(el instanceof CtNewClass)

				// constructor and "final" errors
				&& !(el instanceof CtConstructor)

				// static blocks and "may not have been initialized", skip
				&& !(el instanceof CtAnonymousExecutable)

				// Cannot refer to a non-final variable initial inside an inner class defined in a different method
				&& !(el instanceof CtSimpleType && el.getParent() instanceof CtBlock)) {
			// here is the recursion
			children.add(el);
			variables.addAll(_getVariablesInScope(el.getParent(), children));
		}
		return variables;
	}

	private CtStatement getStatement(final CtElement codeElement) {
		if (codeElement instanceof CtStatement) {
			return (CtStatement) codeElement;
		}
		return codeElement.getParent(CtStatement.class);
	}

	private Collection<CtVariable<?>> getVariablesInLocalScope(final CtElement el, final Set<CtElement> stoppers) {
		final Set<CtVariable<?>> variables = new HashSet<>();
		// we will wisit some elements children of "el" to add the variables
		new VariablesInLocalScopeVisitor(stoppers, variables).scan(el);
		return variables;
	}

	private Collection<CtVariable<?>> getVariablesInScope(final CtElement el) {
		return _getVariablesInScope(el, new HashSet<CtElement>());
	}

	private boolean hasStaticParent(final CtElement el) {
		if (el instanceof CtModifiable) {
			if (((CtModifiable) el).getModifiers().contains(ModifierKind.STATIC)) {
				return true;
			}
		}

		if (el.getParent() != null) {
			return hasStaticParent(el.getParent());
		}
		return false;
	}

	/**
	 * Adds monitoring code for both if expressions (CtIf and all statements for missing preconditions).
	 * That's why we consider CtElement and not only CtIf
	 */
	public void process(final Factory factory, final CtElement statement) {
		boolean inStaticCode = hasStaticParent(statement);
		StringBuilder snippet = new StringBuilder();
		for (CtVariable<?> var : getVariablesInScope(statement)) {
			boolean isStaticVar = var.getModifiers().contains(STATIC);
			// we only add if the code is non static
			// or if code is static and the variable as well
			if (!inStaticCode || inStaticCode && (isStaticVar || !(var instanceof CtField))) {
				// if the local var is not initialized, it might be a compilation problem
				// because of "not initialized"
				if (var instanceof CtLocalVariable) {
					final CtLocalVariable<?> lvar = (CtLocalVariable<?>) var;
					if (lvar.getDefaultExpression() == null) {			
						CtBlock<?> methodBody = lvar.getParent(CtMethod.class).getBody();
						List<CtAssignment<?,?>> assignments = methodBody.getElements(new Filter<CtAssignment<?,?>>() {
							@Override
							public boolean matches(CtAssignment<?,?> elem) {
								if ( !(elem.getAssigned() instanceof CtVariableAccess) ){
									return false;
								}
								return ((CtVariableAccess<?>)elem.getAssigned()).getVariable().getSimpleName().equals(lvar.getSimpleName());
							}
							@Override
							public Class<?> getType() {
								return CtAssignment.class;
							}
						});
						boolean isInit = false;
						for (CtAssignment<?,?> a : assignments) {
							if (a.getPosition().getLine() < statement.getPosition().getLine()){
								isInit = true;
							}
						}
						if ( !isInit ) {
							continue;
						}
					}
				}
				String varName = var.getSimpleName();
				createSnippetCollect(snippet, varName, var);
			}
		}
		if ( statement instanceof CtIf ){
			CtIf iff = (CtIf) statement;
			CtExpression<?> condition = iff.getCondition();
			List<CtExpression<?>> subConditions = condition.getElements(new TypeFilter<CtExpression<?>>(CtExpression.class));
			for ( CtExpression<?> sub : subConditions ){
				if ( !(sub instanceof CtLiteral<?>)){
					createSnippetCollect(snippet, sub.toString(), sub);
				}
			}
		}
		
		
		if (snippet.length() > 0) {
			CtStatement target = getStatement(statement);
			target.insertBefore(factory.Code().createCodeSnippetStatement(snippet.toString()));
			LoggerFactory.getLogger(this.getClass()).debug("Instrumenting [{}] in\n{}", target, target.getParent());
		}
	}

	private void createSnippetCollect(StringBuilder snippet, String varName, CtTypedElement<?> element){
		snippet.append(VALUES_COLLECTOR_CALL).append(varName).append("\", ").append(varName).append(", "+ConditionalValueHolder.ID_Conditional)
		.append(");")
		.append(System.lineSeparator());
		if ( !element.getType().isPrimitive() ){
			snippet.append(NULLNESS_COLLECTOR_CALL).append(varName).append("\", ").append(varName).append(", "+ConditionalValueHolder.ID_Conditional)
			.append(");")
			.append(System.lineSeparator());
		} else {
			// huge workaround
			// because otherwise there are missing values in ValuesCollector at runtime
			// and the indices get wrong
			// so we add a fake value so as to have the same number of collected values at runtime
			// this value is "true", if SMT uses it, the resulting synthesize expression would still compile
			snippet.append(ValuesCollector.class.getName() + ".collectTrue("+ConditionalValueHolder.ID_Conditional+");").append(System.lineSeparator());
		}
	}
	
}
