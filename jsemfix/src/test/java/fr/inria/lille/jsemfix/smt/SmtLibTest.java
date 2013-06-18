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
package fr.inria.lille.jsemfix.smt;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.smtlib.Utils.PRODUCE_MODELS;
import static org.smtlib.Utils.TRUE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.smtlib.ICommand;
import org.smtlib.ICommand.IScript;
import org.smtlib.IExpr;
import org.smtlib.IExpr.IStringLiteral;
import org.smtlib.ISolver;
import org.smtlib.ISort;
import org.smtlib.IVisitor.VisitorException;
import org.smtlib.SMT;
import org.smtlib.SMT.Configuration;
import org.smtlib.logic.QF_UF;
import org.smtlib.solvers.Solver_test;

/**
 * @author Favio D. DeMarco
 * 
 */
public final class SmtLibTest {

	/**
	 * {@code (set-option :produce-models true)(set-logic QF_UF)(declare-fun p () Bool)}
	 * 
	 * @throws VisitorException
	 */
	@Test
	public void test_valid_expressions() throws VisitorException {

		// GIVEN
		SMT smt = new SMT();

		Configuration smtConfig = smt.smtConfig;
		ICommand.IFactory commandFactory = smtConfig.commandFactory;
		IExpr.IFactory efactory = smtConfig.exprFactory;
		ISort.IFactory sortfactory = smtConfig.sortFactory;

		List<ICommand> commands = new ArrayList<>();

		commands.add(commandFactory.set_option(efactory.keyword(PRODUCE_MODELS), TRUE));
		commands.add(commandFactory.set_logic(efactory.symbol(QF_UF.class.getSimpleName())));
		commands.add(commandFactory.declare_fun(efactory.symbol("p"), Collections.<ISort> emptyList(),
				sortfactory.Bool()));
		IExpr.ISymbol p = efactory.symbol("p");
		IExpr notp = efactory.fcn(efactory.symbol("not"), new IExpr[] { p });
		IExpr and = efactory.fcn(efactory.symbol("and"), new IExpr[] { p, notp });
		commands.add(commandFactory.assertCommand(and));

		// WHEN
		IScript script = commandFactory.script((IStringLiteral) null, commands);

		ISolver solver = new Solver_test(smtConfig, (String) null);

		// THEN
		assertTrue(solver.start().isOK());
		assertTrue(script.execute(solver).isOK());
		assertTrue(solver.exit().isOK());
	}

	@Test
	public void test_invalid_expression() throws VisitorException {

		// GIVEN
		SMT smt = new SMT();

		Configuration smtConfig = smt.smtConfig;
		ICommand.IFactory commandFactory = smtConfig.commandFactory;
		IExpr.IFactory efactory = smtConfig.exprFactory;
		ISort.IFactory sortfactory = smtConfig.sortFactory;

		List<ICommand> commands = new ArrayList<>();

		commands.add(commandFactory.set_option(efactory.keyword(PRODUCE_MODELS), TRUE));
		commands.add(commandFactory.set_logic(efactory.symbol(QF_UF.class.getSimpleName())));
		commands.add(commandFactory.declare_fun(efactory.symbol("p"), Collections.<ISort> emptyList(),
				sortfactory.Bool()));
		IExpr.ISymbol p = efactory.symbol("p");

		// WHEN
		IExpr notp = efactory.fcn(efactory.symbol("nor"), new IExpr[] { p });

		// THEN
		IExpr and = efactory.fcn(efactory.symbol("and"), new IExpr[] { p, notp });
		commands.add(commandFactory.assertCommand(and));

		IScript script = commandFactory.script((IStringLiteral) null, commands);

		ISolver solver = new Solver_test(smtConfig, (String) null);

		assertTrue(solver.start().isOK());
		assertFalse(script.execute(solver).isOK());
		assertTrue(solver.exit().isOK());
	}
}
