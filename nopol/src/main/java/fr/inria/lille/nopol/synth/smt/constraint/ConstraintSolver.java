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

import static org.smtlib.impl.Response.UNSAT;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

import fr.inria.lille.nopol.SourceLocation;
import fr.inria.lille.nopol.synth.RepairCandidate;
import fr.inria.lille.nopol.synth.smt.SMTExecutionResult;
import fr.inria.lille.nopol.synth.smt.SolverFactory;
import fr.inria.lille.nopol.synth.smt.model.InputModel;

/**
 * @author Favio D. DeMarco
 * 
 */
public final class ConstraintSolver {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final File outputFolder;
	private final SourceLocation sl;
	private static int i = 0;
	private static List<SMTExecutionResult> execResult = new ArrayList<>();

	public ConstraintSolver(final File outputFolder, SourceLocation sl){
		this.outputFolder = outputFolder;
		this.sl = sl;
	}
	
	private void handleResponse(final IResponse response) {
		if (response.isError()) {
			logger.error(response.toString());
		}
	}

	private void log(final Iterable<ICommand> script) {
		for (ICommand command : script) {
			logger.debug(pretty(command.toString()).toString());
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
		ISolver solver = new SolverFactory(smtConfig).create();
		solver.start();
		Synthesis synthesis = new Synthesis(smtConfig, model);
		IScript script = smtConfig.commandFactory.script((IStringLiteral) null, synthesis.createScript());
		if (logger.isDebugEnabled()) {
			log(script.commands());
		}
		
		long startTime = System.currentTimeMillis();
		handleResponse(script.execute(solver));
		execResult.add(new SMTExecutionResult(System.currentTimeMillis()-startTime, model.getLevel(), sl));
		

		createSMTFile(script, model);

		if (UNSAT.equals(solver.check_sat())) {
			logger.debug("UNSAT");
			return null;
		} else {
			IExpr[] solverModel = synthesis.getModel().toArray(new IExpr[] {});
			IResponse modelResponse = solver.get_value(solverModel);
			Set<IResponse> responses = new HashSet<IResponse>();
			RepairCandidate repairCandidate;
			do {
				responses.add(modelResponse);
				logger.debug("Model: {}", modelResponse);
				repairCandidate = new RepairCandidateBuilder(model, modelResponse).build();
				LoggerFactory.getLogger("code.synthesis").debug("Candidate: {}", repairCandidate);
				// smt-lib needs a check-sat before each get-value to return a new model
				solver.check_sat();
				modelResponse = solver.get_value(solverModel);
			} while (responses.contains(modelResponse));
			return repairCandidate;
		}
	}
	
	private void createSMTFile(IScript script, InputModel model) {
		File output = createOutputFile(i);
		PrintWriter writer=null;
		try {
			DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			writer = new PrintWriter(output);
			writer.println(df.format(new Date()));
			writer.println("STM-File generate for project \""+outputFolder.getCanonicalPath()+"\"");
			writer.println("Class : "+sl.getContainingClassName());
			writer.println("Line : "+sl.getLineNumber());
			writer.println("Level : "+model.getLevel().toString());
			writer.println("-----------------------------------");
		} catch (Exception e) {
			throw new IllegalStateException(e);
		} 
		for ( ICommand cmd : script.commands() ){
			writer.println(cmd.toString());
		}
		writer.flush();
		writer.close();
		i++;
	}

	private File createOutputFile(int i){
		int index = sl.getContainingClassName().lastIndexOf(".");
		String className = sl.getContainingClassName().substring(index+1);
		File output = new File(outputFolder.getParent()+File.separatorChar+className+":"+sl.getLineNumber()+".smt_"+i);
		try {
			output.delete();
			output.createNewFile();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		return output;	
	}
	
	public static List<SMTExecutionResult> getExecResult() {
		return execResult;
	}

}
