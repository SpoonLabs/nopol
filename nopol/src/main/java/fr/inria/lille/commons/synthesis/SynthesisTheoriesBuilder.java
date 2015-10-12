package fr.inria.lille.commons.synthesis;

import fr.inria.lille.commons.synthesis.smt.SMTLib;
import fr.inria.lille.commons.synthesis.theory.*;
import org.smtlib.IExpr.ISymbol;
import xxl.java.container.classic.MetaList;

import java.util.List;

import static java.util.Arrays.asList;

public class SynthesisTheoriesBuilder {

    public static List<OperatorTheory> theoriesForConstraintBasedSynthesis(ISymbol logic) {
        if (logic.equals(SMTLib.logicAufnira())) {
            return theoriesForAufnira();
        } else if (logic.equals(SMTLib.logicAuflira())) {
            return theoriesForAuflira();
        }
        throw new IllegalArgumentException("Do not know how to build theories for " + logic.toString());
    }

    public static List<OperatorTheory> theoriesForAufnira() {
        List<OperatorTheory> theories = MetaList.newArrayList();
        EmptyTheory empty = new EmptyTheory();
        NumberComparisonTheory comparison = new NumberComparisonTheory();
        LogicTheory logic = new LogicTheory();
        LinearTheory linear = new LinearTheory();
        IfThenElseTheory ifThenElse = new IfThenElseTheory();
        NonlinearTheory nonlinear = new NonlinearTheory();
        theories.addAll(asList(empty, comparison, logic, linear, comparison, ifThenElse, nonlinear, logic, linear, ifThenElse, nonlinear));
        return theories;
    }

    public static List<OperatorTheory> theoriesForAuflira() {
        List<OperatorTheory> theories = MetaList.newArrayList();
        EmptyTheory empty = new EmptyTheory();
        NumberComparisonTheory comparison = new NumberComparisonTheory();
        LogicTheory logic = new LogicTheory();
        LinearTheory linear = new LinearTheory();
        IfThenElseTheory ifThenElse = new IfThenElseTheory();
        theories.addAll(asList(empty, comparison, logic, linear, comparison, logic, linear, ifThenElse, ifThenElse));
        return theories;
    }

}
