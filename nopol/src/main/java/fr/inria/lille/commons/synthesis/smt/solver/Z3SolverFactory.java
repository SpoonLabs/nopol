package fr.inria.lille.commons.synthesis.smt.solver;

import org.smtlib.ISolver;
import org.smtlib.SMT.Configuration;
import org.smtlib.solvers.Solver_z3_4_3;

import fr.inria.lille.commons.io.FileHandler;

public class Z3SolverFactory extends SolverFactory {

	@Override
	public ISolver newSolver(Configuration smtConfig) {
		FileHandler.ensurePathIsValid(solverPath);
		return new Solver_z3_4_3(smtConfig, solverPath);
	}
	
	public static final String solverPath = "lib/cvc4-1.4.1/cvc4_for_mac";
}
