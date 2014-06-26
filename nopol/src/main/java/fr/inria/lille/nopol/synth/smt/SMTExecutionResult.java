package fr.inria.lille.nopol.synth.smt;

import java.io.File;

import org.smtlib.IResponse;

import fr.inria.lille.nopol.SourceLocation;
import fr.inria.lille.nopol.synth.smt.model.Level;

public class SMTExecutionResult {

	private long executionTime;
	private Level level;
	private SourceLocation sl;
	private IResponse state;
	private File output;
	
	
	public SMTExecutionResult(long executionTime, Level level, SourceLocation sl, IResponse state, File output){
		this.executionTime = executionTime;
		this.level = level;
		this.sl = sl;
		this.state = state;
		this.output = output;
	}
	
	@Override
	public String toString() {
		return "SMT Execution Time ("
				+level
				+") : "
				+executionTime
				+"ms"
				+" ["
				+sl.getRootClassName()
				+":"
				+sl.getLineNumber()
				+"]["
				+state
				+"]";
	}

	public File getOutput() {
		return output;
	}

	public void setExecutionTime(long executionTime) {
		this.executionTime = executionTime;
	}

	public void setState(IResponse state) {
		this.state = state;
	}
	
	
	
	
}
