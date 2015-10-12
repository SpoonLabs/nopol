package fr.inria.lille.commons.spoon.util;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.declaration.CtElement;
import xxl.java.container.classic.MetaList;

import java.util.List;

import static fr.inria.lille.commons.spoon.util.SpoonElementLibrary.*;
import static fr.inria.lille.commons.spoon.util.SpoonModelLibrary.newBlock;
import static fr.inria.lille.commons.spoon.util.SpoonModelLibrary.setParent;
import static xxl.java.library.ClassLibrary.isInstanceOf;

public class SpoonStatementLibrary {

    public static CtBlock<?> asBlock(CtStatement statement, CtElement parent) {
        CtBlock<?> block;
        if (isBlock(statement)) {
            block = (CtBlock<?>) statement;
        } else {
            block = newBlock(statement.getFactory(), statement);
        }
        setParent(parent, block);
        return block;
    }

    public static void insertBeforeUnderSameParent(CtStatement toBeInserted, CtStatement insertionPoint) {
        CtElement parent;
        if (isBlock(insertionPoint)) {
            CtBlock<?> block = (CtBlock<?>) insertionPoint;
            block.insertBegin(toBeInserted);
            parent = block;
        } else {
            insertionPoint.insertBefore(toBeInserted);
            parent = insertionPoint.getParent();
        }
        setParent(parent, toBeInserted);
    }

    public static void insertAfterUnderSameParent(CtStatement toBeInserted, CtStatement insertionPoint) {
        CtElement parent;
        if (isBlock(insertionPoint)) {
            CtBlock<?> block = (CtBlock<?>) insertionPoint;
            block.insertEnd(toBeInserted);
            parent = block;
        } else {
            insertionPoint.insertAfter(toBeInserted);
            parent = insertionPoint.getParent();
        }
        setParent(parent, toBeInserted);
    }

    public static boolean isLastStatementOfMethod(CtStatement statement) {
        CtElement statementParent = statement.getParent();
        if (!isStatementList(statementParent)) {
            return isLastStatementOfMethod((CtStatement) statementParent);
        }
        CtStatementList block = (CtStatementList) statementParent;
        if (isLastStatementOf(block, statement)) {
            CtElement blockParent = block.getParent();
            if (isStatement(blockParent)) {
                return isLastStatementOfMethod((CtStatement) blockParent);
            } else {
                return isMethod(blockParent);
            }
        }
        return false;
    }

    public static boolean isLastStatementOf(CtStatementList block, CtStatement statement) {
        List<CtStatement> statements = block.getStatements();
        CtStatement lastStatement = MetaList.last(statements);
        return lastStatement == statement;
    }

    public static CtStatement statementOf(CtCodeElement codeElement) {
        Class<CtStatement> statementClass = CtStatement.class;
        if (isInstanceOf(statementClass, codeElement)) {
            return (CtStatement) codeElement;
        }
        return codeElement.getParent(statementClass);
    }
}
