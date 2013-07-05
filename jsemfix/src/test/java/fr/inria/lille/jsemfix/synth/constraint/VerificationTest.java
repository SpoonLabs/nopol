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
package fr.inria.lille.jsemfix.synth.constraint;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.smtlib.ICommand;
import org.smtlib.ICommand.IScript;
import org.smtlib.IExpr.IStringLiteral;
import org.smtlib.IResponse;
import org.smtlib.ISolver;
import org.smtlib.SMT;
import org.smtlib.SMT.Configuration;
import org.smtlib.impl.Response;
import org.smtlib.logic.AUFLIA;
import org.smtlib.solvers.Solver_test;

import fr.inria.lille.jsemfix.synth.model.Components;
import fr.inria.lille.jsemfix.synth.model.InputModel;
import fr.inria.lille.jsemfix.synth.model.Type;

/**
 * @author Favio D. DeMarco
 * 
 */
public class VerificationTest {

	private final Configuration smtConfig = new SMT().smtConfig;

	/**
	 * Test method for
	 * {@link fr.inria.lille.jsemfix.synth.constraint.WellFormedProgram#createFunctionDefinitionFor(fr.inria.lille.jsemfix.synth.model.InputModel)}
	 * .
	 */
	@Test
	public final void testCreateFunctionDefinitionFor() {

		List<ICommand> commands = new ArrayList<>();
		// initialize solver
		commands.add(this.smtConfig.commandFactory.set_logic(this.smtConfig.exprFactory.symbol(AUFLIA.class
				.getSimpleName())));

		InputModel model = new InputModel(asList(Type.INTEGER, Type.INTEGER, Type.BOOLEAN, Type.INTEGER, Type.BOOLEAN),
				asList(Components.AND, Components.OR, Components.ITE, Components.DISTINCT, Components.EQUALS,
						Components.LESS_OR_EQUAL_THAN, Components.LESS_THAN, Components.NOT, Components.AND,
						Components.OR, Components.NOT), Type.BOOLEAN);

		commands.add(new Library(this.smtConfig).createFunctionDefinitionFor(model.getComponents()));
		commands.add(new Connectivity(this.smtConfig).createFunctionDefinitionFor(model));
		commands.add(new Verification(this.smtConfig).createFunctionDefinitionFor(model));

		this.print(commands);

		// WHEN
		IScript script = this.smtConfig.commandFactory.script((IStringLiteral) null, commands);

		ISolver solver = new Solver_test(this.smtConfig, (String) null);
		// THEN
		assertTrue(solver.start().isOK());
		IResponse response = script.execute(solver);
		assertTrue(response.toString(), response.isOK());
		// sat
		IResponse sat = solver.check_sat();
		assertEquals(Response.UNKNOWN, sat);
		assertTrue(solver.exit().isOK());
	}

	private void print(final Iterable<ICommand> commands) {
		for (ICommand command : commands) {
			System.out.println(this.pretty(command.toString()));
		}
	}

	private CharSequence pretty(final String string) {
		StringBuilder builder = new StringBuilder(string.length() * 2);
		String tabs = "";
		for (char c : string.toCharArray()) {
			if (c == '(') {
				if (!tabs.isEmpty()) {
					builder.append(System.lineSeparator());
					builder.append(tabs);
				}
				tabs += '\t';
			} else if (c == ')') {
				tabs = tabs.substring(1);
			}
			builder.append(c);
		}
		return builder;
	}
}
