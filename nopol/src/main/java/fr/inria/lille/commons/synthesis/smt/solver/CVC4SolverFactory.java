package fr.inria.lille.commons.synthesis.smt.solver;

import org.smtlib.ISolver;
import org.smtlib.SMT.Configuration;
import org.smtlib.solvers.Solver_cvc4;

import fr.inria.lille.commons.io.FileHandler;

public class CVC4SolverFactory extends SolverFactory {

	@Override
	public ISolver newSolver(Configuration smtConfig) {
		FileHandler.ensurePathIsValid(solverPath);
		return new Solver_cvc4(smtConfig, solverPath);
	}
	
	public static final String solverPath = "lib/cvc4-1.4.1/cvc4_for_mac";
}
