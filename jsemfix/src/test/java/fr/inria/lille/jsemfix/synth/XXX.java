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
package fr.inria.lille.jsemfix.synth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.smtlib.Utils.PRODUCE_MODELS;
import static org.smtlib.Utils.TRUE;
import static org.smtlib.impl.Response.SAT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.smtlib.ICommand;
import org.smtlib.ICommand.IScript;
import org.smtlib.IExpr;
import org.smtlib.IExpr.IQualifiedIdentifier;
import org.smtlib.IExpr.IStringLiteral;
import org.smtlib.IExpr.ISymbol;
import org.smtlib.IResponse;
import org.smtlib.ISolver;
import org.smtlib.ISort;
import org.smtlib.SMT;
import org.smtlib.SMT.Configuration;
import org.smtlib.logic.AUFLIA;
import org.smtlib.solvers.Solver_cvc4;

/**
 * @author Favio D. DeMarco
 * 
 */
public final class XXX {

	private static final String CVC4_BINARY_PATH = "/usr/bin/cvc4";

	private final List<String> operators = Arrays.asList("=", "distinct", "<", "<=");

	private final List<String> constants = Arrays.asList("0", "1", "-1");

	private final List<String> variables = Arrays.asList("a", "b");

	private final Configuration smtConfig = new SMT().smtConfig;
	private final IExpr.IFactory efactory = this.smtConfig.exprFactory;
	private final ICommand.IFactory commandFactory = this.smtConfig.commandFactory;
	private final ISort.IFactory sortfactory = this.smtConfig.sortFactory;
	private final ISort intSort = this.sortfactory.createSortExpression(this.efactory.symbol("Int"));
	private final IQualifiedIdentifier distinct = this.efactory.symbol("distinct");

	@Test
	public void xxx() {

		List<BinaryOperator> binaryOperators = new ArrayList<>(this.operators.size());
		int order = 0;
		for (String operator : this.operators) {
			binaryOperators.add(BinaryOperator.createForLine(order++, this.efactory));
		}

		List<ISymbol> values = new ArrayList<>(this.variables.size() + this.constants.size());
		for (String variable : this.variables) {
			values.add(this.efactory.symbol("var" + variable));
		}

		List<ICommand> commands = new ArrayList<>();

		// initialize solver
		commands.add(this.commandFactory.set_option(this.efactory.keyword(PRODUCE_MODELS), TRUE));
		commands.add(this.commandFactory.set_logic(this.efactory.symbol(AUFLIA.class.getSimpleName())));

		// initialize symbols (variables)
		this.addOperandsFunctionsTo(binaryOperators, commands);
		this.addVariablesFunctionsTo(values, commands);

		this.addConsistencyConstraint(binaryOperators, commands);
		this.addConsistencyConstraint(binaryOperators, commands);

		this.print(commands);

		// WHEN
		IScript script = this.commandFactory.script((IStringLiteral) null, commands);

		ISolver solver = new Solver_cvc4(this.smtConfig, CVC4_BINARY_PATH);

		// THEN
		assertTrue(solver.start().isOK());
		assertTrue(script.execute(solver).isOK());

		// sat
		IResponse sat = solver.check_sat();
		assertFalse(sat.isError());
		assertEquals(SAT, sat);

		System.out.println();
		System.out.println("Model:");
		for (BinaryOperator op : binaryOperators) {
			System.out.println(solver.get_value(op.getOutputLine(), op.getLeftInputLine(), op.getRightInputLine()));
		}

		assertTrue(solver.exit().isOK());
	}

	private void print(final Iterable<ICommand> commands) {
		for (ICommand command : commands) {
			System.out.println(command);
		}
	}

	private void addConsistencyConstraint(final List<BinaryOperator> binaryOperators,
			final Collection<ICommand> commands) {
		int i = 1;
		int size = binaryOperators.size();
		for (BinaryOperator leftOperator : binaryOperators) {
			Iterable<BinaryOperator> subList = binaryOperators.subList(i++, size);
			for (BinaryOperator rightOperator : subList) {
				IExpr comparison = this.efactory.fcn(this.distinct, leftOperator.getOutputLine(),
						rightOperator.getOutputLine());

				// XXX FIXME TODO should we use individual asserts or (assert (and ... ... ...))?
				commands.add(this.commandFactory.assertCommand(comparison));
			}
		}
	}

	private void addVariablesFunctionsTo(final Iterable<ISymbol> values, final Collection<ICommand> commands) {
		for (ISymbol symbol : values) {
			this.addIntegerFunctionTo(symbol, commands);
		}
	}

	private void addOperandsFunctionsTo(final Iterable<BinaryOperator> binaryOperators,
			final Collection<ICommand> commands) {
		for (BinaryOperator binaryOperator : binaryOperators) {
			this.addBooleanFunctionTo(binaryOperator.getOutput(), commands);
			this.addIntegerFunctionTo(binaryOperator.getLeftInput(), commands);
			this.addIntegerFunctionTo(binaryOperator.getRightInput(), commands);
			this.addIntegerFunctionTo(binaryOperator.getOutputLine(), commands);
			this.addIntegerFunctionTo(binaryOperator.getLeftInputLine(), commands);
			this.addIntegerFunctionTo(binaryOperator.getRightInputLine(), commands);
		}
	}

	private void addBooleanFunctionTo(final ISymbol symbol, final Collection<ICommand> commands) {
		commands.add(this.commandFactory.declare_fun(symbol, Collections.<ISort> emptyList(), this.sortfactory.Bool()));
	}

	private void addIntegerFunctionTo(final ISymbol symbol, final Collection<ICommand> commands) {
		commands.add(this.commandFactory.declare_fun(symbol, Collections.<ISort> emptyList(), this.intSort));
	}

}
