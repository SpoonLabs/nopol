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

import static org.smtlib.Utils.FALSE;
import static org.smtlib.Utils.PRODUCE_MODELS;
import static org.smtlib.Utils.TRUE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import org.smtlib.ICommand;
import org.smtlib.IExpr;
import org.smtlib.IExpr.IIdentifier;
import org.smtlib.IExpr.IQualifiedIdentifier;
import org.smtlib.ISort;
import org.smtlib.SMT.Configuration;
import org.smtlib.logic.AUFLIA;

import com.google.common.collect.Iterables;

import fr.inria.lille.jefix.synth.smt.model.Component;
import fr.inria.lille.jefix.synth.smt.model.InputModel;
import fr.inria.lille.jefix.synth.smt.model.Type;
import fr.inria.lille.jefix.synth.smt.model.ValuesModel;

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

	private IExpr createConstraint(final Iterable<List<IExpr>> inputValues, final Iterable<IExpr> outputValues) {
		List<IExpr> constraints = new ArrayList<>();
		List<IExpr> ioModel = this.getModel();
		constraints.add(this.efactory.fcn(this.efactory.symbol(WellFormedProgram.FUNCTION_NAME), ioModel));
		int index = 0;
		for (IExpr output : outputValues) {
			List<IExpr> parameters = new ArrayList<>(ioModel.size() + 1);
			parameters.addAll(ioModel);
			for (List<IExpr> inputs : inputValues) {
				parameters.add(inputs.get(index));
			}
			index++;
			parameters.add(output);
			constraints.add(this.efactory.fcn(this.efactory.symbol(Verification.FUNCTION_NAME), parameters));
		}
		return this.efactory.fcn(this.and, constraints);
	}

	List<ICommand> createScript() {

		Iterable<List<IExpr>> inputValues = this.getInputValues();
		Iterable<IExpr> outputValues = this.getOutputValues();

		return this.createScriptFor(inputValues, outputValues);
	}

	/**
	 * 
	 * @param inputValues
	 * @param outputValues
	 * @return
	 */
	private List<ICommand> createScriptFor(final Iterable<List<IExpr>> inputValues,
			final Iterable<IExpr> outputValues) {
		List<ICommand> script = new ArrayList<>(this.model.getComponents().size() * 3);
		script.add(this.commandFactory.set_option(this.efactory.keyword(PRODUCE_MODELS), TRUE));
		script.add(this.commandFactory.set_logic(this.efactory.symbol(AUFLIA.class.getSimpleName())));
		this.addFunctionDeclarationsTo(script);
		this.addLocationFunctionsTo(script);
		script.add(this.commandFactory.assertCommand(this.createConstraint(inputValues, outputValues)));
		return script;
	}

	private Iterable<List<IExpr>> getInputValues() {
		Collection<List<IExpr>> expressions = new ArrayList<>();
		ValuesModel valuesModel = this.model.getValues();
		for (Collection<Object> values : valuesModel.getInputvalues().asMap().values()) {
			expressions.add(this.iExprCollectionFromObjects(values));
		}

		// XXX FIXME TODO
		int size = Iterables.size(valuesModel.getOutputValues());

		for (Object constant : valuesModel.getConstants()) {
			expressions.add(Collections.nCopies(size, this.objectToIExpr(constant)));
		}
		return expressions;
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

	private Iterable<IExpr> getOutputValues() {
		return this.iExprCollectionFromObjects(this.model.getValues().getOutputValues());
	}

	private List<IExpr> iExprCollectionFromObjects(final Iterable<Object> values) {
		List<IExpr> expressions = new ArrayList<>();
		for (Object value : values) {
			expressions.add(this.objectToIExpr(value));
		}
		return expressions;
	}

	private IExpr objectToIExpr(final Object value) {
		Type type = Type.ValueToType.INSTANCE.apply(value);
		if (Type.BOOLEAN == type) {
			return (Boolean) value ? TRUE : FALSE;
		} else if (Type.INTEGER == type) {
			long longValue = ((Number) value).longValue();
			if (longValue < 0) {
				return this.efactory.fcn(this.efactory.symbol("-"), this.efactory.numeral(Math.abs(longValue)));
			} else {
				return this.efactory.numeral(longValue);
			}
		}
		throw new IllegalStateException("Unknown type: " + type);
	}
}
