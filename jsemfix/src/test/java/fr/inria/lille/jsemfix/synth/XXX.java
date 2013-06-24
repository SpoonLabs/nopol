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
import static org.junit.Assert.assertTrue;
import static org.smtlib.Utils.PRODUCE_MODELS;
import static org.smtlib.Utils.TRUE;
import static org.smtlib.impl.Response.SAT;

import java.io.File;
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
import org.smtlib.impl.Response;
import org.smtlib.logic.AUFLIA;
import org.smtlib.solvers.Solver_cvc4;
import org.smtlib.solvers.Solver_test;

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
	private final IQualifiedIdentifier and = this.efactory.symbol("and");
	private final IQualifiedIdentifier lessThan = this.efactory.symbol("<");
	private final IQualifiedIdentifier lessOrEqualThan = this.efactory.symbol("<=");

	@Test
	public void xxx() {

		List<BinaryOperator> binaryOperators = new ArrayList<>(this.operators.size());
		for (int order = 0; order < this.operators.size(); order++) {
			binaryOperators.add(BinaryOperator.createForLine(order, this.efactory));
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

		this.addWellFormedProgramConstraint(binaryOperators, commands);

		this.print(commands);

		// WHEN
		IScript script = this.commandFactory.script((IStringLiteral) null, commands);

		ISolver solver = new Solver_test(this.smtConfig, (String) null);
		// THEN
		assertTrue(solver.start().isOK());
		assertTrue(script.execute(solver).isOK());
		// sat
		IResponse sat = solver.check_sat();
		assertEquals(Response.UNKNOWN, sat);
		assertTrue(solver.exit().isOK());

		if (new File(CVC4_BINARY_PATH).exists()) {
			this.solve(script, binaryOperators);
		}
	}

	private void addWellFormedProgramConstraint(final List<BinaryOperator> binaryOperators,
			final List<ICommand> commands) {
		this.addConsistencyConstraint(binaryOperators, commands);
		this.addAcyclicityConstraint(binaryOperators, commands);

		long numberOfInputs = this.constants.size() + this.variables.size();
		long numberOfComponents = this.operators.size() + numberOfInputs;

		for (BinaryOperator operator : binaryOperators) {
			IExpr leftInputRange = this.createRangeExpression(operator.getLeftInputLine(), 0L, numberOfComponents - 1L);
			// XXX FIXME TODO should we use individual asserts or (assert (and ... ... ...))?
			commands.add(this.commandFactory.assertCommand(leftInputRange));

			IExpr rightInputRange = this.createRangeExpression(operator.getRightInputLine(), 0L,
					numberOfComponents - 1L);
			// XXX FIXME TODO should we use individual asserts or (assert (and ... ... ...))?
			commands.add(this.commandFactory.assertCommand(rightInputRange));

			IExpr outputRange = this
					.createRangeExpression(operator.getOutputLine(), numberOfInputs, numberOfComponents);
			// XXX FIXME TODO should we use individual asserts or (assert (and ... ... ...))?
			commands.add(this.commandFactory.assertCommand(outputRange));
		}
	}

	private IExpr createRangeExpression(final IQualifiedIdentifier identifier, final long from, final long to) {
		IExpr leftInput = this.efactory.fcn(this.lessOrEqualThan, this.efactory.numeral(from), identifier);
		IExpr rightInput = this.efactory.fcn(this.lessThan, identifier, this.efactory.numeral(to));
		return this.efactory.fcn(this.and, leftInput, rightInput);
	}

	private void solve(final IScript script, final List<BinaryOperator> binaryOperators) {
		ISolver solver = new Solver_cvc4(this.smtConfig, CVC4_BINARY_PATH);

		// THEN
		assertTrue(solver.start().isOK());
		assertTrue(script.execute(solver).isOK());

		// sat
		assertEquals(SAT, solver.check_sat());

		System.out.println();
		System.out.println("Model:");
		for (BinaryOperator op : binaryOperators) {
			System.out.println(solver.get_value(op.getOutputLine(), op.getLeftInputLine(), op.getRightInputLine()));
		}
		assertTrue(solver.exit().isOK());
	}

	private void addAcyclicityConstraint(final List<BinaryOperator> binaryOperators, final List<ICommand> commands) {
		for (BinaryOperator operator : binaryOperators) {
			IExpr leftInput = this.efactory.fcn(this.lessThan, operator.getLeftInputLine(),
					operator.getOutputLine());
			IExpr rightInput = this.efactory.fcn(this.lessThan, operator.getRightInputLine(),
					operator.getOutputLine());
			IExpr constraint = this.efactory.fcn(this.and, leftInput, rightInput);

			// XXX FIXME TODO should we use individual asserts or (assert (and ... ... ...))?
			commands.add(this.commandFactory.assertCommand(constraint));
		}
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
