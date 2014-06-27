/**
 * 
 */
package fr.inria.lille.nopol.synth.smt;

import java.io.IOException;

import org.smtlib.ISolver;
import org.smtlib.SMT.Configuration;
import org.smtlib.logic.AUFLIRA;
import org.smtlib.logic.AUFNIRA;
import org.smtlib.solvers.Solver_cvc4;
import org.smtlib.solvers.Solver_z3_4_3;

import fr.inria.lille.nopol.synth.smt.model.Level;

/**
 * @author fav
 * 
 */
public final class SolverFactory {

	private interface Solvers {	
		public ISolver create(final Configuration smtConfig);
		public String getLogicName();
		public void setBinaryPath(String path);
		public String getBinaryPath();
		public boolean isWorking(String testFilePath);
		public Level getMaxLevel();
	}
	
	public enum Solver implements Solvers{
		Z3 {
			private String Z3_BINARY_PATH = "/usr/bin/z3";
			
			@Override
			public ISolver create(Configuration smtConfig) {
				return new Solver_z3_4_3(smtConfig, Z3_BINARY_PATH);
			}

			@Override
			public String getLogicName() {
				return AUFNIRA.class.getSimpleName();
			}

			@Override
			public void setBinaryPath(String path) {
				Z3_BINARY_PATH = path;
			}

			@Override
			public String getBinaryPath() {
				return Z3_BINARY_PATH;
			}

			@Override
			public boolean isWorking(String testFilePath) {
				Process p;
				try {
					p = Runtime.getRuntime().exec(Z3_BINARY_PATH+" -smt2 "+testFilePath);
					return p.waitFor() == 0;
				} catch (IOException | InterruptedException e) {
					return false;
				}
				
			}

			@Override
			public Level getMaxLevel() {
				return Level.MULTIPLICATION_2;
			}
		}, 
		CVC4 {
			private String CVC4_BINARY_PATH = "/usr/bin/cvc4";
			 
			@Override
			public ISolver create(Configuration smtConfig) {
				return new Solver_cvc4(smtConfig, CVC4_BINARY_PATH);
			}

			@Override
			public String getLogicName() {
				return AUFLIRA.class.getSimpleName();
			}

			@Override
			public void setBinaryPath(String path) {
				CVC4_BINARY_PATH = path;
			}

			@Override
			public String getBinaryPath() {
				return CVC4_BINARY_PATH;
			}

			@Override
			public boolean isWorking(String testFilePath) {
				Process p;
				try {
					p = Runtime.getRuntime().exec(CVC4_BINARY_PATH+" --lang=smt "+testFilePath);
					return p.waitFor() == 0;
				} catch (IOException | InterruptedException e) {
					return false;
				}
			}

			@Override
			public Level getMaxLevel() {
				return Level.ARITHMETIC_2;
			}
		};

	}
	
	private static Solver solver = Solver.Z3;
	private final Configuration smtConfig;

	public SolverFactory(final Configuration smtConfig) {
		this.smtConfig = smtConfig;
	}

	public ISolver create() {
		return solver.create(smtConfig);
	}
	
	public static void changeSolver(Solver solver){
		SolverFactory.solver = solver;
	}
	
	public static Solver getCurrentSolver(){
		return SolverFactory.solver;
	}
	
}
