package fr.inria.lille.commons.synthesis.smt.solver;

import fr.inria.lille.commons.synthesis.smt.SMTLib;
import org.smtlib.IExpr.ISymbol;
import org.smtlib.ISolver;
import org.smtlib.SMT.Configuration;
import org.smtlib.solvers.Solver_cvc4;

public class CVC4SolverFactory extends SolverFactory {

    /**
     * Command to run from terminal:
     * <p/>
     * $ cvc4 --lang=smt <script-file>
     */

    public CVC4SolverFactory() {
        this("lib/cvc4-1.4.2/cvc4_for_mac");
    }

    public CVC4SolverFactory(String solverPath) {
        super(solverPath);
    }

    @Override
    public String solverName() {
        return "cvc4";
    }

    @Override
    public ISolver newSolver(Configuration smtConfig) {
        return new Solver_cvc4(smtConfig, solverPath());
    }

    @Override
    public ISymbol logic() {
        return SMTLib.logicAuflira();
    }
}
