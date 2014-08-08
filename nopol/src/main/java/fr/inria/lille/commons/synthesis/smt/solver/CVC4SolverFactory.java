package fr.inria.lille.commons.synthesis.smt.solver;

import org.smtlib.ISolver;
import org.smtlib.SMT.Configuration;
import org.smtlib.solvers.Solver_cvc4;

public class CVC4SolverFactory extends SolverFactory {

	public CVC4SolverFactory() {
		this("lib/cvc4-1.4.1/cvc4_for_mac");
	}
	
	public CVC4SolverFactory(String solverPath) {
		super(solverPath);
	}
	
	@Override
	protected ISolver newSolver(Configuration smtConfig) {
		return new Solver_cvc4(smtConfig, solverPath());
	}
}
