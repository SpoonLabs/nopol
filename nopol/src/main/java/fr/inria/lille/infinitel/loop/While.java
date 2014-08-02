package fr.inria.lille.infinitel.loop;

import java.util.Collection;

import spoon.reflect.code.CtBreak;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtWhile;
import spoon.reflect.cu.SourcePosition;
import fr.inria.lille.commons.spoon.util.SpoonLoopLibrary;

public class While {

	public While(CtWhile astLoop) {
		this(astLoop, SpoonLoopLibrary.breakStatementsIn(astLoop), SpoonLoopLibrary.returnStatementsIn(astLoop));
	}
	
	public While(CtWhile astLoop, Collection<CtBreak> breakStatements, Collection<CtReturn<?>> returnStatements) {
		this.astLoop = astLoop;
		this.breakStatements = breakStatements;
		this.returnStatements = returnStatements;
	}
	
	public boolean hasBodyExit() {
		return returnStatements().size() + breakStatements().size() > 0;
	}

	public CtWhile astLoop() {
		return astLoop;
	}

	public SourcePosition position() {
		return astLoop().getPosition();
	}
	
	public CtExpression<Boolean> loopCondition() {
		return astLoop().getLoopingExpression();
	}
	
	public CtStatement loopBody() {
		return astLoop().getBody();
	}
	
	public boolean hasBreaks() {
		return numberOfBreaks() > 0;
	}

	public int numberOfBreaks() {
		return breakStatements().size();
	}
	
	public Collection<CtBreak> breakStatements() {
		return breakStatements;
	}
	
	public boolean hasReturns() {
		return numberOfReturns() > 0;
	}
	
	public int numberOfReturns() {
		return returnStatements().size();
	}
	
	public Collection<CtReturn<?>> returnStatements() {
		return returnStatements;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + position().hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		While other = (While) obj;
		return position().equals(other.position());
	}
	
	@Override
	public String toString() {
		return "While(" + position().toString() + ")";
	}

	private CtWhile astLoop;
	private Collection<CtBreak> breakStatements; 
	private Collection<CtReturn<?>> returnStatements;
}
