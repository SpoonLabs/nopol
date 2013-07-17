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
package fr.inria.lille.jefix.synth.conditional;

import static spoon.reflect.declaration.ModifierKind.STATIC;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtStatement;
import spoon.reflect.cu.SourcePosition;
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
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.visitor.CtAbstractVisitor;

/**
 * 
 * Adds basic logging before each conditionals (if, loops). Use basic scope inference (the real one is hard due to the
 * complex semantics of "static" and "final" (w.r.t. init, anonymous classes, etc.)
 * 
 */
final class ConditionalLoggingInstrumenter extends AbstractProcessor<CtStatement> {

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
				if (this.stoppers.contains(stmt)) {
					return;
				}

				// we only add the new local variables
				if (stmt instanceof CtLocalVariable) {
					this.variables.add((CtVariable<?>) stmt);
				}
			}
		}

		// for a class we add the fields
		@Override
		public <T> void visitCtClass(final CtClass<T> ctClass) {
			for (CtField<?> field : ctClass.getFields()) {
				this.variables.add(field);
			}
		}

		// for a method we add the parameters
		@Override
		public <T> void visitCtMethod(final CtMethod<T> m) {
			for (CtParameter<?> param : m.getParameters()) {
				this.variables.add(param);
			}
		}
	}

	private static final String VALUES_COLLECTOR_CALL = ValuesCollector.class.getName() + ".add(\"";

	private final File file;
	private final int line;

	/**
	 * @param file
	 * @param line
	 */
	ConditionalLoggingInstrumenter(final File file, final int line) {
		this.file = file;
		this.line = line;
	}

	/**
	 * Returns all variables in this scope if el does not define a scope, returns an empty set
	 * 
	 */
	private Collection<CtVariable<?>> _getVariablesInScope(final CtElement el, final Set<CtElement> children) {

		final Set<CtVariable<?>> variables = new HashSet<>();

		// we add all variables in the scope of el
		variables.addAll(this.getVariablesInLocalScope(el, children));

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
			variables.addAll(this._getVariablesInScope(el.getParent(), children));
		}
		return variables;
	}

	private Collection<CtVariable<?>> getVariablesInLocalScope(final CtElement el, final Set<CtElement> stoppers) {
		final Set<CtVariable<?>> variables = new HashSet<>();
		// we will wisit some elements children of "el" to add the variables
		new VariablesInLocalScopeVisitor(stoppers, variables).scan(el);
		return variables;
	}

	private Collection<CtVariable<?>> getVariablesInScope(final CtElement el) {
		return this._getVariablesInScope(el, new HashSet<CtElement>());
	}

	private boolean hasStaticParent(final CtElement el) {
		if (el instanceof CtModifiable) {
			if (((CtModifiable) el).getModifiers().contains(ModifierKind.STATIC)) {
				return true;
			}
		}

		if (el.getParent() != null) {
			return this.hasStaticParent(el.getParent());
		}
		return false;
	}

	@Override
	public boolean isToBeProcessed(final CtStatement candidate) {
		SourcePosition position = candidate.getPosition();
		return (candidate instanceof CtIf || candidate instanceof CtConditional) && position.getLine() == this.line
				&& position.getFile().equals(this.file);
	}

	@Override
	public void process(final CtStatement statement) {

		boolean inStaticCode = this.hasStaticParent(statement);
		StringBuilder snippet = new StringBuilder();

		for (CtVariable<?> var : this.getVariablesInScope(statement)) {
			boolean isStaticVar = var.getModifiers().contains(STATIC);

			// we only add if the code is non static
			// or if code is static and the variable as well
			if (!inStaticCode || inStaticCode && (isStaticVar || !(var instanceof CtField))) {
				// if the local var is not initialized, it might be a compilation problem
				// because of "not initialized"
				if (var instanceof CtLocalVariable) {
					CtLocalVariable<?> lvar = (CtLocalVariable<?>) var;
					if (lvar.getDefaultExpression() == null) {
						continue;
					}
				}
				String varName = var.getSimpleName();
				snippet.append(VALUES_COLLECTOR_CALL).append(varName).append("\", ").append(varName).append(");")
				.append(System.lineSeparator());
			}
		}
		if (snippet.length() > 0) {
			statement.insertBefore(this.getFactory().Code().createCodeSnippetStatement(snippet.toString()));
		}
	}
}
