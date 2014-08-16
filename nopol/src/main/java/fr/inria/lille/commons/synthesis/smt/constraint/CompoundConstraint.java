package fr.inria.lille.commons.synthesis.smt.constraint;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.smtlib.IExpr;

import xxl.java.extensions.collection.ListLibrary;
import xxl.java.extensions.collection.SetLibrary;
import fr.inria.lille.commons.synthesis.smt.SMTLib;
import fr.inria.lille.commons.synthesis.smt.locationVariables.LocationVariable;
import fr.inria.lille.commons.synthesis.smt.locationVariables.LocationVariableContainer;

public abstract class CompoundConstraint extends Constraint {

	public CompoundConstraint(String name, SMTLib smtlib, Collection<Constraint> subconstraints) {
		super(name, smtlib);
		this.subconstraints = subconstraints;
	}

	public Collection<Constraint> subconstraints() {
		return subconstraints;
	}
	
	@Override
	public boolean isCompound() {
		return true;
	}
	
	@Override
	public List<LocationVariable<?>> variablesForExpression(LocationVariableContainer container) {
		Set<LocationVariable<?>> linkedSet = SetLibrary.newLinkedHashSet();
		for (Constraint constraint : subconstraints()) {
			linkedSet.addAll(constraint.variablesForExpression(container));
		}
		return ListLibrary.newLinkedList(linkedSet);
	}
	
	@Override
	public List<LocationVariable<?>> variablesForSubexpression(LocationVariableContainer container) {
		Set<LocationVariable<?>> linkedSet = SetLibrary.newLinkedHashSet();
		for (Constraint constraint : subconstraints()) {
			linkedSet.addAll(constraint.variablesForSubexpression(container));
		}
		return ListLibrary.newLinkedList(linkedSet);
	}
	
	protected Collection<IExpr> subconstraintInvocations(LocationVariableContainer locationVariableContainer) {
		Collection<IExpr> invocations = ListLibrary.newLinkedList();
		for (Constraint constraint : subconstraints()) {
			invocations.add(constraint.invocation(locationVariableContainer));
		}
		return invocations;
	}
	
	private Collection<Constraint> subconstraints;
}
