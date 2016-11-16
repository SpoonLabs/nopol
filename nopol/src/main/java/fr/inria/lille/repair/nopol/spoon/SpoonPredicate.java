package fr.inria.lille.repair.nopol.spoon;

import spoon.reflect.code.*;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;

/**
 * Created by bdanglot on 11/16/16.
 */
public class SpoonPredicate {

    public static boolean canBeRepairedByChangingCondition(final CtElement element) {
        boolean isCtIf = element instanceof CtIf;
        boolean isCtConditional = element instanceof CtConditional;
        return isCtIf || isCtConditional;
    }

    public static boolean canBeRepairedByAddingPrecondition(final CtElement element) {
        CtElement parent = element.getParent();
        if (parent == null) {
            return false;
        }
        if (element.toString().startsWith("super(")) {
            return false;
        }
        boolean isCtStatement = element instanceof CtStatement && !(element instanceof CtBlock);
        boolean isCtReturn = element instanceof CtReturn;
        boolean isInsideIf = parent.getParent() instanceof CtIf; // Checking parent isn't enough, parent will be CtBlock and grandpa will be CtIf
        boolean isCtLocalVariable = element instanceof CtLocalVariable;
        boolean isInBlock = parent instanceof CtBlock;
        if (isInBlock) {
            boolean isInMethod = parent.getParent() instanceof CtMethod;
            if (isInMethod) {
                if (((CtBlock) parent).getLastStatement() == element && !((CtMethod) parent.getParent()).getType().box().equals(element.getFactory().Class().VOID)) {
                    return false;
                }
            }
        }
        boolean isInsideIfLoopCaseBlock = (parent instanceof CtIf || parent instanceof CtLoop || parent instanceof CtCase || parent instanceof CtBlock);
        boolean isInsideForDeclaration = parent instanceof CtFor ? ((CtFor) (parent)).getForUpdate().contains(element) || ((CtFor) (parent)).getForInit().contains(element) : false;
        boolean isCtSynchronized = element instanceof CtSynchronized;

        boolean result = isCtStatement
                // element instanceof CtClass ||

                // cannot insert code before '{}', for example would try to add code between 'Constructor()' and '{}'
                // element instanceof CtBlock ||
                && !isCtSynchronized
                // cannot insert a conditional before 'return', it won't compile.
                && !(isCtReturn && !(isInsideIf))
                // cannot insert a conditional before a variable declaration, it won't compile if the variable is used
                // later on.
                && !isCtLocalVariable
                // Avoids ClassCastException's. @see spoon.support.reflect.code.CtStatementImpl#insertBefore(CtStatement
                // target, CtStatementList<?> statements)
                && isInsideIfLoopCaseBlock
                // cannot insert if inside update statement in for loop declaration
                && !isInsideForDeclaration
                && isInBlock;
        return result;
    }

}
