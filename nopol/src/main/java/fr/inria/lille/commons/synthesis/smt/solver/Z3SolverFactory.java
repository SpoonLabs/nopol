package fr.inria.lille.commons.synthesis.smt.solver;

import org.smtlib.IExpr.ISymbol;
import org.smtlib.ISolver;
import org.smtlib.SMT.Configuration;
import org.smtlib.solvers.Solver_z3_4_3;

import fr.inria.lille.commons.synthesis.smt.SMTLib;

public class Z3SolverFactory extends SolverFactory {

	public Z3SolverFactory() {
		this("lib/cvc4-1.4.1/cvc4_for_mac");
	}
	
	public Z3SolverFactory(String solverPath) {
		super(solverPath);
	}
	
	@Override
	public ISolver newSolver(Configuration smtConfig) {
		return new Solver_z3_4_3(smtConfig, solverPath());
	}

	@Override
	public ISymbol logic() {
		return SMTLib.logicAufnira();
	}
}
