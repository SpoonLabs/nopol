/**
 * 
 */
package fr.inria.lille.nopol.synth.smt;

import org.smtlib.ISolver;
import org.smtlib.SMT.Configuration;
import org.smtlib.solvers.Solver_cvc4;

import utils.FileHandler;

/**
 * @author fav
 * 
 */
public final class SolverFactory {

	private static final String CVC4_BINARY_PATH = "/opt/local/bin/cvc4";
	
	private final Configuration smtConfig;

	public SolverFactory(final Configuration smtConfig) {
		this.smtConfig = smtConfig;
	}

	public ISolver create() {
		return new Solver_cvc4(smtConfig, solverBinaryPath());
	}
	
	public String solverBinaryPath() {
		FileHandler.ensurePathIsValid(CVC4_BINARY_PATH);
		return CVC4_BINARY_PATH;
	}
}
