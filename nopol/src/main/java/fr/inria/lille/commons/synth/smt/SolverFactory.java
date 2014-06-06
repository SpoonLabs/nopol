package fr.inria.lille.commons.synth.smt;

import org.smtlib.ISolver;
import org.smtlib.SMT;
import org.smtlib.SMT.Configuration;
import org.smtlib.solvers.Solver_cvc4;

import fr.inria.lille.commons.io.FileHandler;

public final class SolverFactory {

	public static ISolver newSolver() {
		return newSolver(new SMT().smtConfig);
	}
	
	public static ISolver newSolver(Configuration smtConfig) {
		FileHandler.ensurePathIsValid(solverBinaryPath());
		return new Solver_cvc4(smtConfig, solverBinaryPath());
	}
	
	
	protected static String solverBinaryPath() {
		return CVC4_BINARY_PATH;
	}
	
	private SolverFactory() {}
	
	private static final String CVC4_BINARY_PATH = "/opt/local/bin/cvc4";
}
