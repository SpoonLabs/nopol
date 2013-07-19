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
package fr.inria.lille.jefix.synth.smt.constraint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;

import org.smtlib.ICommand;
import org.smtlib.IExpr;
import org.smtlib.IExpr.IIdentifier;
import org.smtlib.IExpr.IQualifiedIdentifier;
import org.smtlib.ISort;
import org.smtlib.SMT.Configuration;

import fr.inria.lille.jefix.synth.smt.model.Component;
import fr.inria.lille.jefix.synth.smt.model.InputModel;

/**
 * @author Favio D. DeMarco
 *
 */
final class Synthesis {

	private static final String INPUT_LINE_FORMAT = "L_I%d_%d";
	private static final String OUTPUT_LINE = "LO";
	private static final String OUTPUT_LINE_PREFIX = "LO_";

	private final IQualifiedIdentifier and;
	private final ICommand.IFactory commandFactory;
	private final IExpr.IFactory efactory;
	private final ISort intSort;
	private final InputModel model;
	private final Configuration smtConfig;
	private final ISort.IFactory sortfactory;

	Synthesis(@Nonnull final Configuration smtConfig, @Nonnull final InputModel model) {
		this.smtConfig = smtConfig;
		this.efactory = smtConfig.exprFactory;
		this.sortfactory = smtConfig.sortFactory;
		this.commandFactory = smtConfig.commandFactory;
		this.intSort = this.sortfactory.createSortExpression(this.efactory.symbol("Int"));
		this.and = this.efactory.symbol("and");
		this.model = model;
	}

	private void addFunctionDeclarationsTo(final Collection<ICommand> script) {
		List<Component> components = this.model.getComponents();
		script.add(new Acyclicity(this.smtConfig).createFunctionDefinitionFor(components));
		script.add(new Consistency(this.smtConfig).createFunctionDefinitionFor(components.size()));
		script.add(new WellFormedProgram(this.smtConfig).createFunctionDefinitionFor(this.model));
		script.add(new Library(this.smtConfig).createFunctionDefinitionFor(components));
		script.add(new Connectivity(this.smtConfig).createFunctionDefinitionFor(this.model));
		script.add(new Verification(this.smtConfig).createFunctionDefinitionFor(this.model));
	}

	/**
	 * @param script
	 */
	private void addLocationFunctionsTo(final Collection<ICommand> script) {
		Collection<IExpr> ioModel = this.getModel();
		for (IExpr symbol : ioModel) {
			script.add(this.commandFactory.declare_fun((IIdentifier) symbol, Collections.<ISort> emptyList(),
					this.intSort));
		}
	}

	private IExpr createConstraint(final Iterable<List<IExpr>> inputValues,
			final Iterable<IExpr> outputValues) {
		List<IExpr> constraints = new ArrayList<>();
		List<IExpr> ioModel = this.getModel();
		constraints.add(this.efactory.fcn(this.efactory.symbol(WellFormedProgram.FUNCTION_NAME), ioModel));
		Iterator<IExpr> outputs = outputValues.iterator();
		for (List<IExpr> inputs : inputValues) {
			IExpr output = outputs.next();
			List<IExpr> parameters = new ArrayList<>(inputs.size() + ioModel.size() + 1);
			parameters.addAll(ioModel);
			parameters.addAll(inputs);
			parameters.add(output);
			constraints.add(this.efactory.fcn(this.efactory.symbol(Verification.FUNCTION_NAME), parameters));
		}
		return this.efactory.fcn(this.and, constraints);
	}

	/**
	 * 
	 * @param inputValues
	 * @param outputValues
	 * @return
	 */
	Collection<ICommand> createScriptFor(final Iterable<List<IExpr>> inputValues, final Iterable<IExpr> outputValues) {
		Collection<ICommand> script = new ArrayList<>(this.model.getComponents().size() * 3);
		this.addFunctionDeclarationsTo(script);
		this.addLocationFunctionsTo(script);
		script.add(this.commandFactory.assertCommand(this.createConstraint(inputValues, outputValues)));
		return script;
	}

	/**
	 * 
	 * @return
	 */
	List<IExpr> getModel() {
		Collection<Component> components = this.model.getComponents();
		List<IExpr> parameters = new ArrayList<>(components.size() * 3);
		int componentIndex = 0;
		for (Component component : components) {
			for (int parameterIndex = 0; parameterIndex < component.getParameterTypes().size(); parameterIndex++) {
				parameters.add(this.efactory.symbol(String.format(INPUT_LINE_FORMAT, componentIndex, parameterIndex)));
			}
			parameters.add(this.efactory.symbol(OUTPUT_LINE_PREFIX + componentIndex++));
		}
		parameters.add(this.efactory.symbol(OUTPUT_LINE));
		return parameters;
	}
}
