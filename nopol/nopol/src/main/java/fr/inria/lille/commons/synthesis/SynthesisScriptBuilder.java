package fr.inria.lille.commons.synthesis;

import fr.inria.lille.commons.synthesis.smt.SMTLib;
import fr.inria.lille.commons.synthesis.smt.constraint.CompoundConstraint;
import fr.inria.lille.commons.synthesis.smt.constraint.Constraint;
import fr.inria.lille.commons.synthesis.smt.constraint.VerificationConstraint;
import fr.inria.lille.commons.synthesis.smt.constraint.WellFormedProgramConstraint;
import fr.inria.lille.commons.synthesis.smt.locationVariables.LocationVariable;
import fr.inria.lille.commons.synthesis.smt.locationVariables.LocationVariableContainer;
import org.smtlib.ICommand;
import org.smtlib.ICommand.IScript;
import org.smtlib.IExpr;
import org.smtlib.IExpr.ISymbol;
import xxl.java.container.classic.MetaList;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SynthesisScriptBuilder {

    public SynthesisScriptBuilder() {
        this.smtlib = new SMTLib();
        wellFormedConstraint = new WellFormedProgramConstraint(smtlib());
        verificationConstraint = new VerificationConstraint(smtlib());
    }

    public SMTLib smtlib() {
        return smtlib;
    }

    public IScript scriptFrom(ISymbol logic, LocationVariableContainer container, Collection<Map<String, Object>> synthesisInputs) {
        Collection<ICommand> commands = commandsFrom(container);
        Collection<ICommand> assertions = assertionsFor(container, synthesisInputs);
        return smtlib().scriptFrom(logic, commands, assertions);
    }

    public Collection<ICommand> commandsFrom(LocationVariableContainer container) {
        Collection<ICommand> commands = MetaList.newLinkedList();
        addLocationVariableDeclarations(commands, container);
        addDefinitions(commands, wellFormedConstraint(), container);
        addDefinitions(commands, verificationConstraint(), container);
        return commands;
    }

    public Collection<ICommand> assertionsFor(LocationVariableContainer container, Collection<Map<String, Object>> synthesisInputs) {
        Collection<ICommand> assertions = MetaList.newLinkedList();
        addVerificationAssertions(assertions, container, synthesisInputs);
        assertions.add(smtlib().assertion(wellFormedConstraint().invocation(container)));
        return assertions;
    }

    private void addDefinitions(Collection<ICommand> commands, CompoundConstraint compoundConstraint, LocationVariableContainer container) {
        for (Constraint constraint : compoundConstraint.subconstraints()) {
            commands.add(constraint.definition(container));
        }
        commands.add(compoundConstraint.definition(container));
    }

    private void addLocationVariableDeclarations(Collection<ICommand> commands, LocationVariableContainer container) {
        List<LocationVariable<?>> variables = container.operatorsParametersAndOutput();
        for (LocationVariable<?> locationVariable : variables) {
            commands.add(smtlib().constant(locationVariable.expression(), SMTLib.intSort()));
        }
    }

    private void addVerificationAssertions(Collection<ICommand> assertions, LocationVariableContainer container, Collection<Map<String, Object>> synthesisInputs) {
        for (Map<String, Object> values : synthesisInputs) {
            IExpr invocation = verificationConstraint().invocationWithValues(container, values);
            assertions.add(smtlib().assertion(invocation));
        }
    }

    private CompoundConstraint wellFormedConstraint() {
        return wellFormedConstraint;
    }

    private CompoundConstraint verificationConstraint() {
        return verificationConstraint;
    }

    private SMTLib smtlib;
    private CompoundConstraint wellFormedConstraint;
    private CompoundConstraint verificationConstraint;
}
