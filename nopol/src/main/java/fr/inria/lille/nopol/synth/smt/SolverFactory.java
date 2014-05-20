/**
 * 
 */
package fr.inria.lille.nopol.synth.smt;

import java.io.File;

import org.smtlib.ISolver;
import org.smtlib.SMT.Configuration;
import org.smtlib.solvers.Solver_cvc4;

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
		if (! new File(solverBinaryPath()).exists()) {
			throw new RuntimeException("File does not exists: " + solverBinaryPath());
		}
		return new Solver_cvc4(smtConfig, solverBinaryPath());
	}
	
	public String solverBinaryPath() {
		return CVC4_BINARY_PATH;
	}
}
