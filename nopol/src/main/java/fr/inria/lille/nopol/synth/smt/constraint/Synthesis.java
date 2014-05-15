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

import static org.smtlib.Utils.FALSE;
import static org.smtlib.Utils.PRODUCE_MODELS;
import static org.smtlib.Utils.TRUE;

import java.math.BigDecimal;
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
import org.smtlib.logic.AUFNIRA;

import com.google.common.collect.Iterables;

import fr.inria.lille.nopol.synth.smt.model.Component;
import fr.inria.lille.nopol.synth.smt.model.InputModel;
import fr.inria.lille.nopol.synth.smt.model.Type;
import fr.inria.lille.nopol.synth.smt.model.ValuesModel;

/**
 * @author Favio D. DeMarco
 * 
 */
final class Synthesis {

	private static final String MINUS = "-";
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
		efactory = smtConfig.exprFactory;
		sortfactory = smtConfig.sortFactory;
		commandFactory = smtConfig.commandFactory;
		intSort = sortfactory.createSortExpression(efactory.symbol("Int"));
		and = efactory.symbol("and");
		this.model = model;
	}

	private void addFunctionDeclarationsTo(final Collection<ICommand> script) {
		List<Component> components = model.getComponents();
		script.add(new Acyclicity(smtConfig).createFunctionDefinitionFor(components));
		script.add(new Consistency(smtConfig).createFunctionDefinitionFor(components.size()));
		script.add(new WellFormedProgram(smtConfig).createFunctionDefinitionFor(model));
		script.add(new Library(smtConfig).createFunctionDefinitionFor(components));
		script.add(new Connectivity(smtConfig).createFunctionDefinitionFor(model));
		script.add(new Verification(smtConfig).createFunctionDefinitionFor(model));
	}

	/**
	 * @param script
	 */
	private void addLocationFunctionsTo(final Collection<ICommand> script) {
		Collection<IExpr> ioModel = getModel();
		for (IExpr symbol : ioModel) {
			script.add(commandFactory.declare_fun((IIdentifier) symbol, Collections.<ISort> emptyList(), intSort));
		}
	}

	private IExpr createConstraint(final Iterable<List<IExpr>> inputValues, final Iterable<IExpr> outputValues) {
		List<IExpr> constraints = new ArrayList<IExpr>();
		List<IExpr> ioModel = getModel();
		constraints.add(efactory.fcn(efactory.symbol(WellFormedProgram.FUNCTION_NAME), ioModel));
		int index = 0;
		for (IExpr output : outputValues) {
			List<IExpr> parameters = new ArrayList<IExpr>(ioModel.size() + 1);
			for (List<IExpr> inputs : inputValues) {
				parameters.add(inputs.get(index));
			}
			index++;
			parameters.add(output);
			parameters.addAll(ioModel);
			constraints.add(efactory.fcn(efactory.symbol(Verification.FUNCTION_NAME), parameters));
		}
		return efactory.fcn(and, constraints);
	}

	List<ICommand> createScript() {

		Iterable<List<IExpr>> inputValues = getInputValues();
		Iterable<IExpr> outputValues = getOutputValues();

		return createScriptFor(inputValues, outputValues);
	}

	/**
	 * 
	 * @param inputValues
	 * @param outputValues
	 * @return
	 */
	private List<ICommand> createScriptFor(final Iterable<List<IExpr>> inputValues, final Iterable<IExpr> outputValues) {
		List<ICommand> script = new ArrayList<ICommand>(model.getComponents().size() * 3);
		script.add(commandFactory.set_option(efactory.keyword(PRODUCE_MODELS), TRUE));
		script.add(commandFactory.set_logic(efactory.symbol(AUFNIRA.class.getSimpleName())));
		addFunctionDeclarationsTo(script);
		addLocationFunctionsTo(script);
		script.add(commandFactory.assertCommand(createConstraint(inputValues, outputValues)));
		return script;
	}

	private Iterable<List<IExpr>> getInputValues() {
		Collection<List<IExpr>> expressions = new ArrayList<List<IExpr>>();
		ValuesModel valuesModel = model.getValues();
		for (Collection<Object> values : valuesModel.getInputvalues().asMap().values()) {
			expressions.add(iExprCollectionFromObjects(values));
		}

		// XXX FIXME TODO
		int size = Iterables.size(valuesModel.getOutputValues());

		for (Object constant : valuesModel.getConstants()) {
			expressions.add(Collections.nCopies(size, objectToIExpr(constant)));
		}
		return expressions;
	}

	/**
	 * 
	 * @return
	 */
	List<IExpr> getModel() {
		Collection<Component> components = model.getComponents();
		List<IExpr> parameters = new ArrayList<IExpr>(components.size() * 3);
		parameters.add(efactory.symbol(OUTPUT_LINE));
		int componentIndex = 0;
		for (Component component : components) {
			parameters.add(efactory.symbol(OUTPUT_LINE_PREFIX + componentIndex));
			for (int parameterIndex = 0; parameterIndex < component.getParameterTypes().size(); parameterIndex++) {
				parameters.add(efactory.symbol(String.format(INPUT_LINE_FORMAT, componentIndex, parameterIndex)));
			}
			componentIndex++;
		}
		return parameters;
	}

	private Iterable<IExpr> getOutputValues() {
		return iExprCollectionFromObjects(model.getValues().getOutputValues());
	}

	private List<IExpr> iExprCollectionFromObjects(final Iterable<Object> values) {
		List<IExpr> expressions = new ArrayList<IExpr>();
		for (Object value : values) {
			expressions.add(objectToIExpr(value));
		}
		return expressions;
	}

	private IExpr objectToIExpr(final Object value) {
		Type type = Type.ValueToType.INSTANCE.apply(value);
		if (Type.BOOLEAN == type) {
			return (Boolean) value ? TRUE : FALSE;
		} else if (Type.NUMBER == type) {
			String stringValue = value.toString();
			// Avoid scientific notation
			if ( stringValue.contains("E") ){
				stringValue = new BigDecimal((double)value).toPlainString();
			}
			if (stringValue.startsWith(MINUS)) {
				return efactory.fcn(efactory.symbol(MINUS), efactory.decimal(stringValue.substring(1)));
			} else {
				return efactory.decimal(stringValue);
			}
		}
		throw new IllegalStateException("Unknown type: " + type);
	}
}
