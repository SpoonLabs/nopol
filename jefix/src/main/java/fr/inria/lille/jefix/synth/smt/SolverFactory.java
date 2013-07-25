/**
 * 
 */
package fr.inria.lille.jefix.synth.smt;

import org.smtlib.ISolver;
import org.smtlib.SMT.Configuration;
import org.smtlib.solvers.Solver_cvc4;

/**
 * @author fav
 * 
 */
public final class SolverFactory {

	/**
	 * XXX FIXME TODO should be a parameter
	 */
	private static final String CVC4_BINARY_PATH = "/usr/bin/cvc4";

	private final Configuration smtConfig;

	public SolverFactory(final Configuration smtConfig) {
		this.smtConfig = smtConfig;
	}

	public ISolver create() {
		return new Solver_cvc4(this.smtConfig, CVC4_BINARY_PATH);
	}
}
