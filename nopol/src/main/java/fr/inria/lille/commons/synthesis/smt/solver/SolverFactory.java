package fr.inria.lille.commons.synthesis.smt.solver;

import org.smtlib.ISolver;
import org.smtlib.SMT;
import org.smtlib.SMT.Configuration;

public abstract class SolverFactory {

	public abstract ISolver newSolver(Configuration smtConfig);
	
	public ISolver newSolver() {
		return newSolver(new SMT().smtConfig);
	}
	
	public static ISolver defaultSolver() {
		return defaultSolver(new SMT().smtConfig);
	}
	
	public static ISolver defaultSolver(Configuration smtConfig) {
		return new CVC4SolverFactory().newSolver(smtConfig);
	}
}
