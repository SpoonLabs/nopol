package fr.inria.lille.nopol.synth.smt;

import org.smtlib.IResponse;

import fr.inria.lille.nopol.SourceLocation;
import fr.inria.lille.nopol.synth.smt.model.Level;

public class SMTExecutionResult {

	private long executionTime;
	private Level level;
	private SourceLocation sl;
	private IResponse state;
	
	
	public SMTExecutionResult(long executionTime, Level level, SourceLocation sl, IResponse state){
		this.executionTime = executionTime;
		this.level = level;
		this.sl = sl;
		this.state = state;
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
	
	
}
