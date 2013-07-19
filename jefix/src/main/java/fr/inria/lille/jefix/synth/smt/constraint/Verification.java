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

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.smtlib.ICommand;
import org.smtlib.IExpr;
import org.smtlib.IExpr.IDeclaration;
import org.smtlib.IExpr.ISymbol;
import org.smtlib.ISort;
import org.smtlib.SMT.Configuration;

import com.google.common.base.Function;

import fr.inria.lille.jefix.synth.smt.model.Component;
import fr.inria.lille.jefix.synth.smt.model.InputModel;
import fr.inria.lille.jefix.synth.smt.model.Type;

/**
 * @author Favio D. DeMarco
 * 
 */
final class Verification {

	static final String FUNCTION_NAME = "verification";
	private static final String INPUT_FORMAT = "I%d_%d";
	private static final String INPUT_LINE_FORMAT = "L_I%d_%d";
	private static final String INPUT_PREFIX = "I_";
	private static final String OUTPUT = "O";
	private static final String OUTPUT_LINE = "LO";
	private static final String OUTPUT_LINE_PREFIX = "LO_";
	private static final String OUTPUT_PREFIX = "O_";

	private final ISymbol and;
	private final ICommand.IFactory commandFactory;
	private final IExpr.IFactory efactory;
	private final ISort intSort;
	private final ISort.IFactory sortfactory;
	private final Function<Type, ISort> typeToSort;

	Verification(@Nonnull final Configuration smtConfig) {
		this.efactory = smtConfig.exprFactory;
		this.sortfactory = smtConfig.sortFactory;
		this.commandFactory = smtConfig.commandFactory;
		this.intSort = this.sortfactory.createSortExpression(this.efactory.symbol("Int"));
		this.and = this.efactory.symbol("and");
		this.typeToSort = new TypeToSort(this.sortfactory, this.efactory);
	}

	private IExpr createConnectivityCall(final InputModel model) {
		List<Type> inputTypes = model.getInputTypes();
		List<Component> components = model.getComponents();
		List<IExpr> parameters = new ArrayList<>(6 * components.size() + inputTypes.size() + 2);
		for (int inputIndex = 0; inputIndex < inputTypes.size(); inputIndex++) {
			parameters.add(this.efactory.symbol(String.format(INPUT_PREFIX + inputIndex)));
		}
		int componentIndex = 0;
		for (Component component : components) {
			for (int parameterIndex = 0; parameterIndex < component.getParameterTypes().size(); parameterIndex++) {
				parameters.add(this.efactory.symbol(String.format(INPUT_FORMAT, componentIndex, parameterIndex)));
				parameters.add(this.efactory.symbol(String.format(INPUT_LINE_FORMAT, componentIndex, parameterIndex)));
			}
			parameters.add(this.efactory.symbol(OUTPUT_PREFIX + componentIndex));
			parameters.add(this.efactory.symbol(OUTPUT_LINE_PREFIX + componentIndex));
			componentIndex++;
		}
		parameters.add(this.efactory.symbol(OUTPUT));
		parameters.add(this.efactory.symbol(OUTPUT_LINE));
		return this.efactory.fcn(this.efactory.symbol(Connectivity.FUNCTION_NAME), parameters);
	}

	private IExpr createConstraint(@Nonnull final InputModel model) {
		List<IDeclaration> iot = this.createInputOutputPAndRDeclarations(model.getComponents());
		return this.efactory.exists(iot, this.createConstraint(model, iot));
	}

	private IExpr createConstraint(@Nonnull final InputModel model, @Nonnull final List<IDeclaration> iot) {
		IExpr libCall = this.createLibCall(model.getComponents());
		IExpr connCall = this.createConnectivityCall(model);;
		return this.efactory.fcn(this.and, asList(libCall, connCall));
	}

	ICommand createFunctionDefinitionFor(@Nonnull final InputModel model) {
		List<Component> components = model.getComponents();
		checkArgument(!components.isEmpty(), "The number of operators should be greater than 0.");
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
		ISymbol symbol = this.efactory.symbol(OUTPUT_LINE);
		parameters.add(this.efactory.declaration(symbol, this.intSort));
		int inputIndex = 0;
		for (Type type : model.getInputTypes()) {
			ISymbol input = this.efactory.symbol(String.format(INPUT_PREFIX + inputIndex));
			parameters.add(this.efactory.declaration(input, this.typeToSort.apply(type)));
			inputIndex++;
		}
		parameters.add(this.efactory.declaration(this.efactory.symbol(OUTPUT),
				this.typeToSort.apply(model.getOutputType())));
		return this.commandFactory.define_fun(this.efactory.symbol(FUNCTION_NAME), parameters, this.sortfactory.Bool(),
				this.createConstraint(model));
	}

	private List<IDeclaration> createInputOutputPAndRDeclarations(@Nonnull final Iterable<Component> components) {
		List<IDeclaration> declarations = new ArrayList<>();
		int componentIndex = 0;
		for (Component component : components) {
			int parameterIndex = 0;
			for (Type type : component.getParameterTypes()) {
				ISymbol input = this.efactory.symbol(String.format(INPUT_FORMAT, componentIndex, parameterIndex));
				declarations.add(this.efactory.declaration(input, this.typeToSort.apply(type)));
				parameterIndex++;
			}
			ISymbol output = this.efactory.symbol(OUTPUT_PREFIX + componentIndex);
			declarations.add(this.efactory.declaration(output, this.typeToSort.apply(component.getOutputType())));
			componentIndex++;
		}
		return declarations;
	}

	private IExpr createLibCall(final Iterable<Component> list) {
		List<IExpr> parameters = new ArrayList<>();
		int componentIndex = 0;
		for (Component component : list) {

			for (int parameterIndex = 0; parameterIndex < component.getParameterTypes().size(); parameterIndex++) {
				parameters.add(this.efactory.symbol(String.format(INPUT_FORMAT, componentIndex, parameterIndex)));
			}
			parameters.add(this.efactory.symbol(OUTPUT_PREFIX + componentIndex));
			componentIndex++;
		}
		return this.efactory.fcn(this.efactory.symbol(Library.FUNCTION_NAME), parameters);
	}
}
