package fr.inria.lille.commons.spoon.util;

import spoon.Launcher;
import spoon.SpoonModelBuilder;
import spoon.compiler.Environment;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtBreak;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtThrow;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtWhile;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.CodeFactory;
import spoon.reflect.factory.CoreFactory;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import xxl.java.container.classic.MetaList;
import xxl.java.library.JavaLibrary;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import static fr.inria.lille.commons.spoon.util.SpoonStatementLibrary.asBlock;
import static java.util.Arrays.asList;

public class SpoonModelLibrary {

    public static Factory modelFor(File[] sourceFiles) {
        return modelFor(sourceFiles, null);
    }

    public static Factory modelFor(File[] sourceFiles, URL[] classpath) {
        return modelFor(newFactory(), sourceFiles, classpath);
    }

    public static Factory modelFor(Factory factory, File[] sourceFiles, URL[] classpath) {
        factory.getEnvironment().setLevel("OFF");
        try {
            SpoonModelBuilder compiler = launcher().createCompiler(factory);
            if (classpath != null) {
                compiler.setSourceClasspath(JavaLibrary.asFilePath(classpath));
            }
            for (int i = 0; i < sourceFiles.length; i++) {
                File sourceFile = sourceFiles[i];
                compiler.addInputSource(sourceFile);
            }
            compiler.build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return factory;
    }

    public static <T extends CtElement> T clone(T toBeCloned) {
        Factory factory = toBeCloned.getFactory();
        return factory.Core().clone(toBeCloned);
    }

    public static CtBreak newBreak(Factory factory) {
        return factory.Core().createBreak();
    }

    public static <T> CtLiteral<T> newLiteral(Factory factory, T value) {
        CtLiteral<T> newLiteral = factory.Core().createLiteral();
        newLiteral.setValue(value);
        return newLiteral;
    }

    public static <T> CtLocalVariable<T> newLocalVariableDeclaration(Factory factory, Class<T> type, String variableName, T defaultValue, CtElement parent) {
        CtLocalVariable<T> localVariable = newLocalVariableDeclaration(factory, type, variableName, defaultValue);
        setParent(parent, localVariable);
        return localVariable;
    }

    public static <T> CtLocalVariable<T> newLocalVariableDeclaration(Factory factory, Class<T> type, String variableName, String defaultValue, CtElement parent) {
        CtLocalVariable<T> localVariable = newLocalVariableDeclaration(factory, type, variableName, defaultValue);
        setParent(parent, localVariable);
        return localVariable;
    }

    public static <T> CtLocalVariable<T> newLocalVariableDeclaration(Factory factory, Class<T> type, String variableName, T defaultValue) {
        return newLocalVariable(factory, type, variableName, newLiteral(factory, defaultValue));
    }

    public static <T> CtLocalVariable<T> newLocalVariableDeclarationString(Factory factory, Class<T> type, String variableName, String defaultValue) {
        return newLocalVariableDeclaration(factory, type, variableName, defaultValue);
    }

    public static <T> CtLocalVariable<T> newLocalVariableDeclaration(Factory factory, Class<T> type, String variableName, String defaultValue) {
        return newLocalVariable(factory, type, variableName, newExpressionFromSnippet(factory, defaultValue, type));
    }

    public static <T> CtLocalVariable<T> newLocalVariable(Factory factory, Class<T> aClass, String variableName, CtExpression<T> defaultValue) {
        CtLocalVariable<T> variable = newLocalVariable(factory, aClass, variableName);
        variable.setDefaultExpression(defaultValue);
        return variable;
    }

    public static <T> CtLocalVariable<T> newLocalVariable(Factory factory, Class<T> aClass, String variableName) {
        CtLocalVariable<T> variable = factory.Core().createLocalVariable();
        variable.setType(newTypeReference(factory, aClass));
        variable.setSimpleName(variableName);
        return variable;
    }

    public static <T> CtExpression<T> newExpressionFromSnippet(Factory factory, String codeSnippet, Class<T> expressionClass, CtElement parent) {
        CtExpression<T> expression = newExpressionFromSnippet(factory, codeSnippet, expressionClass);
        setParent(parent, expression);
        return expression;
    }

    public static <T> CtExpression<T> newExpressionFromSnippet(Factory factory, String codeSnippet, Class<T> expressionClass) {
        return factory.Code().createCodeSnippetExpression(codeSnippet);
    }

    public static CtStatement newStatementFromSnippet(Factory factory, String codeSnippet, CtElement parent) {
        CtStatement statement = newStatementFromSnippet(factory, codeSnippet);
        setParent(parent, statement);
        return statement;
    }

    public static CtStatement newStatementFromSnippet(Factory factory, String codeSnippet) {
        return factory.Code().createCodeSnippetStatement(codeSnippet);
    }

    public static CtBlock<CtStatement> newBlock(Factory factory, CtStatement... statements) {
        return newBlock(factory, MetaList.newArrayList(statements));
    }

    public static CtBlock<CtStatement> newBlock(Factory factory, List<CtStatement> statements) {
        CtBlock<CtStatement> newBlock = factory.Core().createBlock();
        newBlock.setStatements(statements);
        setParent(newBlock, statements);
        return newBlock;
    }

    public static CtExpression<Boolean> newConjunctionExpression(Factory factory, CtExpression<Boolean> leftExpression, CtExpression<Boolean> rightExpression) {
        return newComposedExpression(factory, leftExpression, rightExpression, BinaryOperatorKind.AND);
    }

    public static CtExpression<Boolean> newDisjunctionExpression(Factory factory, CtExpression<Boolean> leftExpression, CtExpression<Boolean> rightExpression) {
        return newComposedExpression(factory, leftExpression, rightExpression, BinaryOperatorKind.OR);
    }

    public static <T> CtExpression<T> newComposedExpression(Factory factory, CtExpression<T> leftExpression, CtExpression<T> rightExpression, BinaryOperatorKind operator) {
        CtBinaryOperator<T> composedExpression = factory.Code().createBinaryOperator(leftExpression, rightExpression, operator);
        setParent(composedExpression, leftExpression, rightExpression);
        return composedExpression;
    }

    public static CtIf newIf(Factory factory, CtExpression<Boolean> condition, CtStatement thenBranch) {
        CtIf newIf = factory.Core().createIf();
        thenBranch = asBlock(thenBranch, newIf);
        setParent(newIf, condition, thenBranch);
        newIf.setCondition(condition);
        newIf.setThenStatement(thenBranch);
        return newIf;
    }

    public static CtIf newIf(Factory factory, CtExpression<Boolean> condition, CtStatement thenBranch, CtStatement elseBranch) {
        CtIf newIf = newIf(factory, condition, thenBranch);
        elseBranch = asBlock(elseBranch, newIf);
        setParent(newIf, elseBranch);
        newIf.setElseStatement(elseBranch);
        return newIf;
    }

    public static <E extends Throwable> CtTry newTryCatch(Factory factory, CtStatement tryBlock, Class<E> exception, String catchName, CtStatement catchBlock, CtElement parent) {
        CtCatch newCatch = newCatch(factory, exception, catchName, catchBlock);
        return newTryCatch(factory, tryBlock, asList(newCatch), parent);
    }

    public static CtTry newTryCatch(Factory factory, CtStatement tryBlock, List<CtCatch> catchers, CtElement parent) {
        CtTry tryCatch = factory.Core().createTry();
        tryCatch.setBody(asBlock(tryBlock, tryCatch));
        tryCatch.setCatchers(catchers);
        setParent(tryCatch, catchers);
        setParent(parent, tryCatch);
        return tryCatch;
    }

    public static <E extends Throwable> CtCatch newCatch(Factory factory, Class<E> throwableClass, String catchName, CtStatement catchBlock) {
        CtCatch aCatch = factory.Core().createCatch();
        aCatch.setParameter(factory.Code().createCatchVariable(newTypeReference(factory, throwableClass), catchName));
        aCatch.setBody(asBlock(catchBlock, aCatch));
        return aCatch;
    }

    public static <E extends Throwable> CtThrow newThrow(Factory factory, Class<E> throwableClass, String thrownExpression) {
        return newThrow(factory, newExpressionFromSnippet(factory, thrownExpression, throwableClass));
    }

    public static <E extends Throwable> CtThrow newThrow(Factory factory, CtExpression<E> thrownExpression) {
        CtThrow aThrow = factory.Core().createThrow();
        aThrow.setThrownExpression(thrownExpression);
        return aThrow;
    }

    public static <E> CtTypeReference<E> newTypeReference(Factory factory, Class<E> aClass) {
        return factory.Type().createReference(aClass);
    }

    public static void setParent(CtElement parent, Collection<? extends CtElement> children) {
        setParent(parent, children.toArray(new CtElement[children.size()]));
    }

    public static void setParent(CtElement parent, CtElement... children) {
        for (CtElement child : children) {
            child.setParent(parent);
        }
    }

    public static void setLoopBody(CtWhile loop, CtStatement loopBody) {
        loopBody = asBlock(loopBody, loop);
        loop.setBody(loopBody);
    }

    public static void setLoopingCondition(CtWhile loop, CtExpression<Boolean> loopingCondition) {
        setParent(loop, loopingCondition);
        loop.setLoopingExpression(loopingCondition);
    }

    public static Factory newFactory() {
        return launcher().createFactory();
    }

    private static Launcher launcher() {
        if (launcher == null) {
            launcher = new Launcher();
        }
        return launcher;
    }

    private static Launcher launcher;
}
