package fr.inria.lille.commons.synthesis.smt.solver;

import org.smtlib.IExpr.ISymbol;
import org.smtlib.ISolver;
import org.smtlib.SMT;
import org.smtlib.SMT.Configuration;

import fr.inria.lille.commons.io.FileHandler;

public abstract class SolverFactory {

	public abstract ISymbol logic();

	public abstract ISolver newSolver(Configuration smtConfig);
	
	public static SolverFactory instance() {
		return solverFactory();
	}
	
	public static ISymbol solverLogic() {
		return solverFactory().logic();
	}

	public static void setSolver(String solverName, String pathToSolver) {
		if (solverName.equalsIgnoreCase("z3")) {
			solverFactory = new Z3SolverFactory(pathToSolver);
		} else if (solverName.equalsIgnoreCase("cvc4")) {
			solverFactory = new CVC4SolverFactory(pathToSolver);
		} else {
			throw new RuntimeException("Invalid solver name: " + solverName);
		}
	}
	
	public SolverFactory(String solverPath) {
		FileHandler.ensurePathIsValid(solverPath);
		this.solverPath = solverPath;
	}
	
	public ISolver newSolver() {
		return newSolver(new SMT().smtConfig);
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
