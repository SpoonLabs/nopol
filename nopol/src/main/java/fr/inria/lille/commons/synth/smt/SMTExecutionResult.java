package fr.inria.lille.commons.synth.smt;

import fr.inria.lille.nopol.SourceLocation;
import fr.inria.lille.nopol.synth.smt.model.Level;

public class SMTExecutionResult {

	private long executionTime;
	private Level level;
	private SourceLocation sl;
	
	
	public SMTExecutionResult(long executionTime, Level level, SourceLocation sl){
		this.executionTime = executionTime;
		this.level = level;
		this.sl = sl;
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
				+"]";
	}
	
	
}
