package fr.inria.lille.commons.synthesis.smt.constraint;

import java.util.Arrays;
import java.util.Collection;

import org.smtlib.IExpr;

import fr.inria.lille.commons.synthesis.smt.SMTLib;
import fr.inria.lille.commons.synthesis.smt.locationVariables.LocationVariableContainer;

public class WellFormedProgramConstraint extends CompoundConstraint {

	public WellFormedProgramConstraint(SMTLib smtlib) {
		super("WellFormedProgram", smtlib, Arrays.asList(new AcyclicityConstraint(smtlib), new ConsistencyConstraint(smtlib), new LineBoundConstraint(smtlib)));
	}

	@Override
	protected Collection<IExpr> definitionExpressions(LocationVariableContainer locationVariableContainer) {
		return subconstraintInvocations(locationVariableContainer);
	}	
}