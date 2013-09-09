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
package fr.inria.lille.nopol.synth.smt.constraint;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.smtlib.ICommand;
import org.smtlib.IExpr;
import org.smtlib.IExpr.IDeclaration;
import org.smtlib.IExpr.IQualifiedIdentifier;
import org.smtlib.IExpr.ISymbol;
import org.smtlib.ISort;
import org.smtlib.SMT.Configuration;

import fr.inria.lille.nopol.synth.smt.model.Component;

/**
 * @author Favio D. DeMarco
 * 
 */
final class Acyclicity {

	private static final String OUTPUT_LINE_PREFIX = "LO_";
	static final String FUNCTION_NAME = "acyc";
	private static final String INPUT_LINE_FORMAT = "L_I%d_%d";

	private final ICommand.IFactory commandFactory;
	private final IExpr.IFactory efactory;
	private final IQualifiedIdentifier lessThan;
	private final ISort.IFactory sortfactory;
	private final ISort intSort;

	Acyclicity(@Nonnull final Configuration smtConfig) {
		this.efactory = smtConfig.exprFactory;
		this.sortfactory = smtConfig.sortFactory;
		this.commandFactory = smtConfig.commandFactory;
		this.lessThan = this.efactory.symbol("<");
		this.intSort = this.sortfactory.createSortExpression(this.efactory.symbol("Int"));
	}

	ICommand createFunctionDefinitionFor(@Nonnull final List<Component> components) {
		List<IDeclaration> parameters = new ArrayList<>(components.size() * 3);
		int componentIndex = 0;
		for (Component component : components) {
			for (int parameterIndex = 0; parameterIndex < component.getParameterTypes().size(); parameterIndex++) {
				ISymbol symbol = this.efactory.symbol(String.format(INPUT_LINE_FORMAT, componentIndex, parameterIndex));
				parameters.add(this.efactory.declaration(symbol, this.intSort));
			}
			ISymbol symbol = this.efactory.symbol(OUTPUT_LINE_PREFIX + componentIndex++);
			parameters.add(this.efactory.declaration(symbol, this.intSort));
		}
		return this.commandFactory.define_fun(this.efactory.symbol(FUNCTION_NAME), parameters, this.sortfactory.Bool(),
				this.createConstraint(components));
	}

	private IExpr createConstraint(final List<Component> operators) {
		List<IExpr> constraints = new ArrayList<>();
		int i = 0;
		for (Component operator : operators) {
			ISymbol output = this.efactory.symbol(OUTPUT_LINE_PREFIX + i);
			for (int j = 0; j < operator.getParameterTypes().size(); j++) {
				ISymbol symbol = this.efactory.symbol(String.format(INPUT_LINE_FORMAT, i, j));
				constraints.add(this.efactory.fcn(this.lessThan, symbol, output));
			}
			i++;
		}
		return new Simplifier(this.efactory).simplifyAnd(constraints);
	}
}
