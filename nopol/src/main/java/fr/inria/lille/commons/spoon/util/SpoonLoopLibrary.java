package fr.inria.lille.commons.spoon.util;

import spoon.reflect.code.CtBreak;
import spoon.reflect.code.CtLoop;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.declaration.CtMethod;
import xxl.java.container.classic.MetaList;

import java.util.Collection;
import java.util.List;

import static fr.inria.lille.commons.spoon.util.SpoonElementLibrary.allChildrenOf;

public class SpoonLoopLibrary {

    public static Collection<CtBreak> breakStatementsIn(CtLoop loop) {
        List<CtBreak> breaksOfLoop = MetaList.newArrayList();
        List<CtBreak> allBreaks = allChildrenOf(loop, CtBreak.class);
        for (CtBreak candidateBreak : allBreaks) {
            if (isBreakingFrom(loop, candidateBreak)) {
                breaksOfLoop.add(candidateBreak);
            }
        }
        return breaksOfLoop;
    }

    public static boolean isBreakingFrom(CtLoop loop, CtBreak breakStatement) {
        if (breakStatement.getParent(CtLoop.class) == loop) {
            return breakStatement.getParent(CtSwitch.class) == loop.getParent(CtSwitch.class);
        }
        return false;
    }

    public static Collection<CtReturn<?>> returnStatementsIn(CtLoop loop) {
        List<CtReturn<?>> returnsOfLoop = MetaList.newArrayList();
        List<CtReturn<?>> allReturns = (List) allChildrenOf(loop, CtReturn.class);
        for (CtReturn<?> candidateReturn : allReturns) {
            if (isReturningFrom(loop, candidateReturn)) {
                returnsOfLoop.add(candidateReturn);
            }
        }
        return returnsOfLoop;
    }

    public static boolean isReturningFrom(CtLoop loop, CtReturn<?> returnStatement) {
        if (returnStatement.getParent(CtLoop.class) == loop) {
            return returnStatement.getParent(CtMethod.class) == loop.getParent(CtMethod.class);
        }
        return false;
    }
}
