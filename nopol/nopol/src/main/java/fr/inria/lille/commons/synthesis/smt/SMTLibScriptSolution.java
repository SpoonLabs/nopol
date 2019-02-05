package fr.inria.lille.commons.synthesis.smt;

import fr.inria.lille.commons.synthesis.smt.solver.SolverFactory;
import org.smtlib.ICommand;
import org.smtlib.ICommand.IScript;
import org.smtlib.IExpr;
import org.smtlib.IParser.ParserException;
import org.smtlib.IResponse;
import org.smtlib.ISolver;
import org.smtlib.sexpr.ISexpr;
import org.smtlib.sexpr.ISexpr.IToken;
import org.smtlib.sexpr.Sexpr.Seq;
import xxl.java.container.classic.MetaList;
import xxl.java.container.classic.MetaMap;
import xxl.java.library.StringLibrary;

import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

public class SMTLibScriptSolution implements Enumeration<Map<String, String>> {

    public SMTLibScriptSolution(SMTLib smtlib, IScript script, Collection<? extends IExpr> variables) {
        this.smtlib = smtlib;
        this.script = script;
        this.variables = variables.toArray(new IExpr[variables.size()]);
    }

    @Override
    public boolean hasMoreElements() {
        IResponse response = script().execute(solver());
        boolean hasMoreElements = response.isOK() && isSatisfiable();
        if (!hasMoreElements) {
            shutdownSolver();
        }
        return hasMoreElements;
    }

    @Override
    public Map<String, String> nextElement() {
        IResponse response = solver().get_value(variables());
        Map<IExpr, IExpr> solution = solutionFrom(response);
        updateScript(solution);
        shutdownSolver();
        return StringLibrary.asStringMap(solution);
    }

    protected Map<IExpr, IExpr> solutionFrom(IResponse response) {
        Map<IExpr, IExpr> solution = MetaMap.newHashMap();
        try {
            Seq seq = (Seq) response;
            for (ISexpr expr : seq.sexprs()) {
                Seq nestedSeq = (Seq) expr;
                IToken<?> key = (IToken<?>) nestedSeq.sexprs().get(0);
                String value = nestedSeq.sexprs().get(1).toString();
                IExpr valueExpr = smtlib().parserFor(value).parseExpr();
                solution.put((IExpr) key, valueExpr);
            }
        } catch (IndexOutOfBoundsException ioobe) {
            ioobe.printStackTrace();
        } catch (ClassCastException cce) {
            cce.printStackTrace();
        } catch (ParserException pe) {
            pe.printStackTrace();
        }
        return solution;
    }

    protected boolean isSatisfiable() {
        return solver().check_sat().equals(org.smtlib.impl.Response.SAT);
    }

    protected void updateScript(Map<IExpr, IExpr> solution) {
        List<IExpr> values = variablesAndValues(solution);
        IExpr visitedSolution = smtlib().notExpression(smtlib().conjunctionExpression(values));
        ICommand skipVisitedSolution = smtlib().assertion(visitedSolution);
        script().commands().add(skipVisitedSolution);
    }

    protected List<IExpr> variablesAndValues(Map<IExpr, IExpr> solution) {
        List<IExpr> values = MetaList.newLinkedList();
        for (IExpr expr : solution.keySet()) {
            IExpr valueExpr = smtlib().equalsExpression(expr, solution.get(expr));
            values.add(valueExpr);
        }
        return values;
    }

    private SMTLib smtlib() {
        return smtlib;
    }

    private IScript script() {
        return script;
    }

    private IExpr[] variables() {
        return variables;
    }

    private ISolver solver() {
        if (solver == null) {
            solver = SolverFactory.instance().newSolver();
        }
        return solver;
    }

    private void shutdownSolver() {
        solver().exit();
        solver = null;
    }

    private ISolver solver;
    private SMTLib smtlib;
    private IScript script;
    private IExpr[] variables;
}
