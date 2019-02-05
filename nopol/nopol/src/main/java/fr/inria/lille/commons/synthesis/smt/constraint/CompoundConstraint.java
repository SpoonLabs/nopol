package fr.inria.lille.commons.synthesis.smt.constraint;

import fr.inria.lille.commons.synthesis.smt.SMTLib;
import fr.inria.lille.commons.synthesis.smt.locationVariables.LocationVariable;
import fr.inria.lille.commons.synthesis.smt.locationVariables.LocationVariableContainer;
import org.smtlib.IExpr;
import xxl.java.container.classic.MetaList;
import xxl.java.container.classic.MetaSet;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public abstract class CompoundConstraint extends Constraint {

    public CompoundConstraint(String name, SMTLib smtlib, Collection<Constraint> subconstraints) {
        super(name, smtlib);
        this.subconstraints = subconstraints;
    }

    public Collection<Constraint> subconstraints() {
        return subconstraints;
    }

    public int numberOfSubconstraints() {
        return subconstraints().size();
    }

    @Override
    public boolean isCompound() {
        return true;
    }

    @Override
    public List<LocationVariable<?>> variablesForExpression(LocationVariableContainer container) {
        Set<LocationVariable<?>> linkedSet = MetaSet.newLinkedHashSet(numberOfSubconstraints());
        for (Constraint constraint : subconstraints()) {
            linkedSet.addAll(constraint.variablesForExpression(container));
        }
        return MetaList.newLinkedList(linkedSet);
    }

    @Override
    public List<LocationVariable<?>> variablesForSubexpression(LocationVariableContainer container) {
        Set<LocationVariable<?>> linkedSet = MetaSet.newLinkedHashSet(numberOfSubconstraints());
        for (Constraint constraint : subconstraints()) {
            linkedSet.addAll(constraint.variablesForSubexpression(container));
        }
        return MetaList.newLinkedList(linkedSet);
    }

    protected Collection<IExpr> subconstraintInvocations(LocationVariableContainer locationVariableContainer) {
        Collection<IExpr> invocations = MetaList.newLinkedList();
        for (Constraint constraint : subconstraints()) {
            invocations.add(constraint.invocation(locationVariableContainer));
        }
        return invocations;
    }

    private Collection<Constraint> subconstraints;
}
