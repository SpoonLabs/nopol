package fr.inria.lille.commons.synthesis.smt.solver;

import org.smtlib.IExpr.ISymbol;
import org.smtlib.ISolver;
import org.smtlib.SMT.Configuration;
import org.smtlib.solvers.Solver_z3_4_3;

import fr.inria.lille.commons.synthesis.smt.SMTLib;

public class Z3SolverFactory extends SolverFactory {

	/** Command to run from terminal:
	 * 
	 * 		$ z3 -smt2 <script-file>
	 */
	
	public Z3SolverFactory() {
		this("lib/z3-4.3.2/z3_for_mac");
	}
	
	public Z3SolverFactory(String solverPath) {
		super(solverPath);
	}
	
	@Override
	public String solverName() {
		return "z3";
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
