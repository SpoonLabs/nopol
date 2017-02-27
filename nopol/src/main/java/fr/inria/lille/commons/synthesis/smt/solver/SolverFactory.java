package fr.inria.lille.commons.synthesis.smt.solver;

import fr.inria.lille.repair.common.config.NopolContext;
import org.smtlib.IExpr.ISymbol;
import org.smtlib.ISolver;
import org.smtlib.SMT;
import org.smtlib.SMT.Configuration;
import xxl.java.library.FileLibrary;

public abstract class SolverFactory {

    public abstract String solverName();

    public abstract ISymbol logic();

    public abstract ISolver newSolver(Configuration smtConfig);

    public static SolverFactory instance() {
        return solverFactory();
    }

    public static ISymbol solverLogic() {
        return solverFactory().logic();
    }

    public static void setSolver(String solverName, String pathToSolver) {
        if (solverName.equalsIgnoreCase("z3")) {
            solverFactory = new Z3SolverFactory(pathToSolver);
        } else if (solverName.equalsIgnoreCase("cvc4")) {
            solverFactory = new CVC4SolverFactory(pathToSolver);
        } else {
            throw new RuntimeException("Invalid solver name: " + solverName);
        }
    }

    public static void setSolver(NopolContext.NopolSolver solver, String pathToSolver) {
        switch (solver) {
            case Z3:
                solverFactory = new Z3SolverFactory(pathToSolver);
                break;
            case CVC4:
                solverFactory = new CVC4SolverFactory(pathToSolver);
                break;
            default:
                throw new RuntimeException("Invalid solver name: " + solver);
        }
    }

    public SolverFactory(String solverPath) {
        FileLibrary.ensurePathIsValid(solverPath);
        this.solverPath = solverPath;
    }

    public String solverPath() {
        return solverPath;
    }

    public ISolver newSolver() {
        ISolver solver = newSolver(new SMT().smtConfig);
        solver.start();
        return solver;
    }

    private static SolverFactory solverFactory() {
        if (solverFactory == null) {
            solverFactory = new Z3SolverFactory();
        }
        return solverFactory;
    }

    private String solverPath;
    private static SolverFactory solverFactory;
}
