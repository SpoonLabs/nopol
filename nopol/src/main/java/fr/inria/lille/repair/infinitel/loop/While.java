package fr.inria.lille.repair.infinitel.loop;

import fr.inria.lille.commons.spoon.util.SpoonLoopLibrary;
import spoon.reflect.code.*;
import spoon.reflect.cu.SourcePosition;
import xxl.java.container.classic.MetaList;

import java.util.Collection;

public class While {

    public static Collection<While> loopsWithBreak(Collection<While> loops) {
        Collection<While> loopsWithBreak = MetaList.newLinkedList();
        for (While loop : loops) {
            if (loop.hasBreaks()) {
                loopsWithBreak.add(loop);
            }
        }
        return loopsWithBreak;
    }

    public static Collection<While> loopsWithReturn(Collection<While> loops) {
        Collection<While> loopsWithReturn = MetaList.newLinkedList();
        for (While loop : loops) {
            if (loop.hasReturns()) {
                loopsWithReturn.add(loop);
            }
        }
        return loopsWithReturn;
    }

    public static Collection<While> loopsWithBreakAndReturn(Collection<While> loops) {
        Collection<While> loopsWithBreakAndReturn = MetaList.newLinkedList();
        for (While loop : loopsWithReturn(loops)) {
            if (loop.hasBreaks()) {
                loopsWithBreakAndReturn.add(loop);
            }
        }
        return loopsWithBreakAndReturn;
    }

    public static Collection<While> loopsWithoutBodyExit(Collection<While> loops) {
        Collection<While> loopsWithoutBodyExit = MetaList.newLinkedList();
        for (While loop : loops) {
            if (!(loop.hasBodyExit())) {
                loopsWithoutBodyExit.add(loop);
            }
        }
        return loopsWithoutBodyExit;
    }

    public While(CtWhile astLoop) {
        this(astLoop, SpoonLoopLibrary.breakStatementsIn(astLoop), SpoonLoopLibrary.returnStatementsIn(astLoop));
    }

    public While(CtWhile astLoop, Collection<CtBreak> breakStatements, Collection<CtReturn<?>> returnStatements) {
        this.astLoop = astLoop;
        this.breakStatements = breakStatements;
        this.returnStatements = returnStatements;
        loopingCondition = astLoop().getLoopingExpression().toString();
        unbreakable = false;
    }

    public boolean hasBodyExit() {
        return returnStatements().size() + breakStatements().size() > 0;
    }

    public CtWhile astLoop() {
        return astLoop;
    }

    public String loopingCondition() {
        return loopingCondition;
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

    public boolean isUnbreakable() {
        return unbreakable;
    }

    public void setUnbreakable() {
        unbreakable = true;
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
        return "While(" + loopingCondition() + "): " + position().toString();
    }

    private CtWhile astLoop;
    private boolean unbreakable;
    private String loopingCondition;
    private Collection<CtBreak> breakStatements;
    private Collection<CtReturn<?>> returnStatements;
}
