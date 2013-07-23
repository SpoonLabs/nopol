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

import static org.smtlib.impl.Response.SAT;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smtlib.ICommand;
import org.smtlib.ICommand.IScript;
import org.smtlib.IExpr;
import org.smtlib.IExpr.IStringLiteral;
import org.smtlib.IResponse;
import org.smtlib.ISolver;
import org.smtlib.SMT;
import org.smtlib.SMT.Configuration;
import org.smtlib.solvers.Solver_cvc4;

import fr.inria.lille.jefix.synth.RepairCandidate;
import fr.inria.lille.jefix.synth.smt.model.InputModel;

/**
 * @author Favio D. DeMarco
 * 
 */
public final class ConstraintSolver {

	/**
	 * XXX FIXME TODO should be a parameter
	 */
	private static final String CVC4_BINARY_PATH = "/usr/bin/cvc4";

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private void log(final Iterable<ICommand> script) {
		for (ICommand command : script) {
			this.logger.debug(this.pretty(command.toString()).toString());
		}
	}

	private CharSequence pretty(final String string) {
		StringBuilder builder = new StringBuilder(string.length() * 2);
		builder.append(System.lineSeparator());
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

	public RepairCandidate solve(final InputModel model) {
		Configuration smtConfig = new SMT().smtConfig;
		ISolver solver = new Solver_cvc4(smtConfig, CVC4_BINARY_PATH);
		solver.start();
		Synthesis synthesis = new Synthesis(smtConfig, model);
		IScript script = smtConfig.commandFactory.script((IStringLiteral) null, synthesis.createScript());

		if (this.logger.isDebugEnabled()) {
			this.log(script.commands());
		}

		script.execute(solver);
		if (SAT.equals(solver.check_sat())) {
			IResponse solverResponse = solver.get_value(synthesis.getModel().toArray(new IExpr[] {}));
			this.logger.debug("Model: {}", solverResponse);
			return new RepairCandidateBuilder(model, solverResponse).build();
		} else {
			this.logger.debug("UNSAT");
			return null;
		}
	}
}
