package fr.inria.lille.commons.synthesis.smt.solver;

import org.smtlib.ISolver;
import org.smtlib.SMT;
import org.smtlib.SMT.Configuration;

import fr.inria.lille.commons.io.FileHandler;

public abstract class SolverFactory {

	protected abstract ISolver newSolver(Configuration smtConfig);

	public SolverFactory(String solverPath) {
		FileHandler.ensurePathIsValid(solverPath);
		this.solverPath = solverPath;
	}
	
	public static ISolver newDefaultSolver() {
		return newDefaultSolver(new SMT().smtConfig);
	}
	
	public static ISolver newDefaultSolver(Configuration smtConfig) {
		return solverFactory().newSolver(smtConfig);
	}
	
	public static void setSolver(String solverName, String pathToSolver) {
		if (solverName.equalsIgnoreCase("z3")) {
			solverFactory = new Z3SolverFactory(pathToSolver);
		} else if (solverName.equalsIgnoreCase("cvc4")) {
			solverFactory = new CVC4SolverFactory(pathToSolver);
		}
		throw new RuntimeException("Invalid solver name: " + solverName);
	}
	
	private static SolverFactory solverFactory() {
		if (solverFactory == null) {
			solverFactory = new CVC4SolverFactory();
		}
		return solverFactory;
	}
	
	protected String solverPath() {
		return solverPath;
	}
	
	private String solverPath;
	private static SolverFactory solverFactory;
}
