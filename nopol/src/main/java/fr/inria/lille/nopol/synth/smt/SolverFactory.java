/**
 * 
 */
package fr.inria.lille.nopol.synth.smt;

import org.smtlib.ISolver;
import org.smtlib.SMT.Configuration;
import org.smtlib.solvers.Solver_cvc4;
import org.smtlib.solvers.Solver_z3_4_3;

/**
 * @author fav
 * 
 */
public final class SolverFactory {

	/**
	 * XXX FIXME TODO should be a parameter
	 */
	private static final String Z3_BINARY_PATH = "/usr/bin/z3";
	
	private final Configuration smtConfig;

	public SolverFactory(final Configuration smtConfig) {
		this.smtConfig = smtConfig;
	}

	public ISolver create() {
		return new Solver_z3_4_3(smtConfig, Z3_BINARY_PATH);
	}
}
