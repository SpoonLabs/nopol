package fr.inria.lille.repair.nopol.spoon.symbolic;

import gov.nasa.jpf.symbc.Debug;
import spoon.SpoonException;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtTypeReference;

import java.util.*;

/**
 * replace assert by a condition and a Assert.fail
 *
 * @author T. Durieux
 */
public class AssertReplacer extends AbstractProcessor<CtInvocation<?>> {

    private static int exceptionCount = 0;

    public AssertReplacer() {
    }

    @Override
    public boolean isToBeProcessed(CtInvocation<?> candidate) {
        if (!candidate.getExecutable().getSimpleName().contains("assert")) {
            return false;
        }
        CtExecutableReference<?> executable = candidate.getExecutable();
        CtPackageReference executablePackage = executable.getDeclaringType()
                .getPackage();
        if (executablePackage == null
                || !executablePackage.getSimpleName().contains("junit")) {
            return false;
        }
        CtMethod<?> parentMethod = candidate.getParent(CtMethod.class);
        if (parentMethod == null) {
            return false;
        }
        createExecutionMethod(candidate);
        return super.isToBeProcessed(candidate);
        /*
		 * boolean found = false; for (TestCase testCase : faillingTest) { if
		 * (testCase.className().equals(parentClass.getQualifiedName())) { if
		 * (testCase.testName().equals(parentMethod.getSimpleName())) { found =
		 * true; break; } } }
		 * 
		 * return found;
		 */
    }

    /**
     * Create the content of the condition
     *
     * @param ctInvocation
     * @return
     */
    private CtBlock<Object> createThen(CtInvocation<?> ctInvocation) {
        CtBlock<Object> thenStatement = ctInvocation.getFactory().Core()
                .createBlock();
        thenStatement
                .addStatement((getFactory().Code()
                        .createCodeSnippetStatement("System.out.println(\"Else...\")")));
        thenStatement.addStatement((getFactory().Code()
                .createCodeSnippetStatement("System.out.println("
                        + Debug.class.getCanonicalName() + ".getSolvedPC())")));

        CtAssert<Object> ctAssert = ctInvocation.getFactory().Core()
                .createAssert();

        CtCodeSnippetExpression<Boolean> assertExpression = getFactory().Core()
                .createCodeSnippetExpression();
        assertExpression.setValue("false");

        ctAssert.setAssertExpression(assertExpression);
        ctAssert.setExpression(getFactory().Code().createCodeSnippetExpression(
                String.format("\"%s\"",
                        ctInvocation.toString().replaceAll("\"", "'"))));

        thenStatement.addStatement(ctAssert);

        return thenStatement;
    }

    /**
     * replace assert Equals
     *
     * @param ctInvocation
     * @return
     */
    private CtStatement replaceAssertEquals(CtInvocation<?> ctInvocation) {
        List<CtExpression<?>> arguments = ctInvocation.getArguments();
        CtIf newIf = ctInvocation.getFactory().Core().createIf();
        CtCodeSnippetExpression<Boolean> condition = ctInvocation.getFactory()
                .Core().createCodeSnippetExpression();
        CtExpression<?> elem1 = arguments.get(0);
        CtExpression<?> elem2 = arguments.get(1);
        if (arguments.size() > 2
                && (elem1.getType().equals(
                ctInvocation.getFactory().Class().STRING) || elem1
                .getType().equals(getFactory().Class().nullType()))) {
            elem1 = arguments.get(1);
            elem2 = arguments.get(2);
        }

        boolean isNull = false;
        Class<?> classArg1 = null;
        if (elem1.toString().equals("null")) {
            isNull = true;
        } else {
            try {
                classArg1 = ((CtTypedElement<?>) elem1).getType()
                        .getActualClass();
            } catch (SpoonException e) {
                e.printStackTrace();
            }
        }
        if (isNull
                || (classArg1 != null && (classArg1.equals(int.class)
                || classArg1.equals(Integer.class)
                || classArg1.equals(boolean.class)
                || classArg1.equals(Boolean.class)
                || classArg1.equals(byte.class)
                || classArg1.equals(Byte.class)
                || classArg1.equals(long.class)
                || classArg1.equals(Long.class)
                || classArg1.equals(double.class)
                || classArg1.equals(Double.class)
                || classArg1.equals(float.class)
                || classArg1.equals(Float.class)
                || classArg1.equals(short.class)
                || classArg1.equals(Short.class)
                || classArg1.equals(char.class) || classArg1
                .equals(Character.class)))) {
            condition.setValue("" + elem1 + " == " + elem2 + "");
        } else {
            condition.setValue("(" + elem1 + ".equals(" + elem2 + "))");
        }
        newIf.setCondition(condition);
        // newIf.setThenStatement(getFactory().Code().createCodeSnippetStatement(
        // Debug.class.getCanonicalName() + ".printPC(\"Path Condition: \")"));
        // newIf.setThenStatement(getFactory().Code().createCodeSnippetStatement("System.out.println("+Debug.class.getCanonicalName()+".getSolvedPC())"));
		/*
		 * CtBlock<Object> thenStatement = ctInvocation.getFactory().Core()
		 * .createBlock(); thenStatement.addStatement((getFactory().Code()
		 * .createCodeSnippetStatement("System.out.println(\"Then...\")")));
		 * thenStatement.addStatement((getFactory().Code()
		 * .createCodeSnippetStatement("System.out.println(" +
		 * Debug.class.getCanonicalName() + ".getSolvedPC())")));
		 * newIf.setThenStatement(thenStatement);
		 */
        newIf.setThenStatement(createThen(ctInvocation));

        return newIf;
    }

    /**
     * replace assertNull
     *
     * @param ctInvocation
     * @return
     */
    private CtStatement replaceAssertNull(CtInvocation<?> ctInvocation) {
        List<?> arguments = ctInvocation.getArguments();
        CtIf newIf = ctInvocation.getFactory().Core().createIf();

        Object elem1 = arguments.get(0);
        CtExpression<Boolean> condition = ctInvocation.getFactory().Code()
                .createCodeSnippetExpression("(" + elem1 + ") == null");
        newIf.setCondition(condition);
        // newIf.setThenStatement(getFactory().Code().createCodeSnippetStatement(
        // Debug.class.getCanonicalName() + ".printPC(\"Path Condition: \")"));
		/*
		 * CtBlock<Object> thenStatement = ctInvocation.getFactory().Core()
		 * .createBlock(); thenStatement .addStatement((getFactory().Code()
		 * .createCodeSnippetStatement("System.out.println(\"Then...\")")));
		 * thenStatement.addStatement((getFactory().Code()
		 * .createCodeSnippetStatement("System.out.println(" +
		 * Debug.class.getCanonicalName() + ".getSolvedPC())")));
		 * newIf.setThenStatement(thenStatement);
		 */
        newIf.setThenStatement(createThen(ctInvocation));
        return newIf;
    }

    /**
     * replace assertNotNull
     *
     * @param ctInvocation
     * @return
     */
    private CtStatement replaceAssertNotNull(CtInvocation<?> ctInvocation) {
        List<?> arguments = ctInvocation.getArguments();
        CtIf newIf = ctInvocation.getFactory().Core().createIf();

        Object elem1 = arguments.get(0);
        CtExpression<Boolean> condition = ctInvocation.getFactory().Code()
                .createCodeSnippetExpression("(" + elem1 + ") != null");
        newIf.setCondition(condition);
        // newIf.setThenStatement(getFactory().Code().createCodeSnippetStatement(
        // Debug.class.getCanonicalName() + ".printPC(\"Path Condition: \")"));
		/*
		 * CtBlock<Object> thenStatement = ctInvocation.getFactory().Core()
		 * .createBlock(); thenStatement .addStatement((getFactory().Code()
		 * .createCodeSnippetStatement("System.out.println(\"Then...\")")));
		 * thenStatement.addStatement((getFactory().Code()
		 * .createCodeSnippetStatement("System.out.println(" +
		 * Debug.class.getCanonicalName() + ".getSolvedPC())")));
		 * newIf.setThenStatement(thenStatement);
		 */
        newIf.setThenStatement(createThen(ctInvocation));
        return newIf;
    }

    /**
     * replace assertTrue
     *
     * @param ctInvocation
     * @return
     */
    private CtStatement replaceAssertTrueFalse(boolean type,
                                               CtInvocation<?> ctInvocation) {
        List<?> arguments = ctInvocation.getArguments();
        CtIf newIf = ctInvocation.getFactory().Core().createIf();

        Object elem1 = arguments.get(0);
        if (arguments.size() > 1) {
            elem1 = arguments.get(1);
        }
        if (!type) {
            CtExpression<Boolean> condition = ctInvocation
                    .getFactory()
                    .Code()
                    .createCodeSnippetExpression(
                            "!(" + elem1 + ")");
            newIf.setCondition(condition);
        } else {
            newIf.setCondition((CtExpression<Boolean>) elem1);
        }
        // newIf.setThenStatement(getFactory().Code().createCodeSnippetStatement(
        // Debug.class.getCanonicalName() + ".printPC(\"Path Condition: \")"));
		/*
		 * CtBlock<Object> thenStatement = ctInvocation.getFactory().Core()
		 * .createBlock(); // thenStatement.addStatement((getFactory().Code().
		 * createCodeSnippetStatement("System.out.println(\"Then...\")")));
		 * thenStatement.addStatement((getFactory().Code()
		 * .createCodeSnippetStatement("System.out.println(" +
		 * Debug.class.getCanonicalName() + ".getSolvedPC())")));
		 * newIf.setThenStatement(thenStatement);
		 */
        newIf.setThenStatement(createThen(ctInvocation));
        return newIf;
    }

    @Override
    public void process(CtInvocation<?> ctInvocation) {
        // ignore no assert invocation
        // CtTypeReference<?> executableType = executable.getDeclaringType();

        // List<CtExpression<?>> parameters = ctInvocation.getArguments();
        // int parametersSize = parameters.size();
        // Class<?>[] parameterTypes = new Class<?>[parametersSize];
        // for (int i = 0; i < parametersSize; i++) {
        // parameterTypes[0] = parameters.get(i).getType().getActualClass();
        // }
        // List<CtExpression<?>> arguments = ctInvocation.getArguments();
        // CtIf newIf = ctInvocation.getFactory().Core().createIf();
        // String body = Assert.class.getCanonicalName() + "."
        // + ctInvocation.getExecutable().getSimpleName() + "(";
        // for (int i = 0; i < arguments.size(); i++) {
        // CtExpression<?> ctExpression = arguments.get(i);
        // body += ctExpression;
        // if (i < arguments.size() - 1) {
        // body += ", ";
        // }
        // }
        // body += ")";
        // CtCodeSnippetExpression<Boolean> condition =
        // ctInvocation.getFactory()
        // .Code().createCodeSnippetExpression(body);
        // newIf.setCondition(condition);
        // newIf.setElseStatement(createThen(ctInvocation));
        // ctInvocation.replace(newIf);
        // createExecutionMethod(ctInvocation);
        // return;
        //CtTry ctTry = getFactory().Core().createTry();
        //CtCatch ctCatch = getFactory().Core().createCatch();
        CtCatchVariable<Exception> localVariable = getFactory().Code()
                .createCatchVariable(
                        getFactory().Class().createReference(Exception.class),
                        "exception" + (++exceptionCount));
        //ctCatch.setParameter(localVariable);
        //ctCatch.setBody(createThen(ctInvocation));
        //ctTry.addCatcher(ctCatch);
        CtBlock<Object> tryBody = ctInvocation.getFactory().Core()
                .createBlock();
        //ctTry.setBody(tryBody);
        switch (ctInvocation.getExecutable().getSimpleName()) {
            case "assertEquals":
                ctInvocation.replace(replaceAssertEquals(ctInvocation));
                break;
            case "assertFalse":
                ctInvocation.replace(replaceAssertTrueFalse(false, ctInvocation));
                break;
            case "assertTrue":
                ctInvocation.replace(replaceAssertTrueFalse(true, ctInvocation));
                break;
            case "assertNull":
                ctInvocation.replace(replaceAssertNull(ctInvocation));
                break;
            case "assertNotNull":
                ctInvocation.replace(replaceAssertNotNull(ctInvocation));
                break;
            default:
                //System.out.println("Assert transformation not found: " + ctInvocation.getExecutable().getSimpleName());
                return;
        }
        // insertAfterUnderSameParent(((getFactory().Code().createCodeSnippetStatement("System.out.println(\"Fianlly: \" + "+Debug.class.getCanonicalName()+".getSolvedPC())"))),
        // ctInvocation);
		/*if(ctInvocation.getParent(CtTry.class) == null) {
			ctInvocation.replace(ctTry);
		} else {
			ctInvocation.replace(tryBody);
		}*/
		/*
		 * System.out.println("REPLACE: "); System.out.println(ctInvocation);
		 * System.out.println("BY: "); System.out.println(ctTry);
		 */
        //createExecutionMethod(ctInvocation);
    }

    private void createExecutionMethod(CtInvocation<?> ctInvocation) {
        CtClass<?> parent = ctInvocation.getParent(CtClass.class);
        if (!parent.isTopLevel()) {
            return;
        }
        if (parent.getModifiers().contains(ModifierKind.ABSTRACT)) {
            return;
        }
        CtTypeReference<String[]> typedReference = getFactory().Class()
                .createReference(String[].class);
        if (parent.getMethod("runJPFTest", typedReference) != null) {
            return;
        }
        CtTypeReference<Object> returntypedReference = getFactory().Class()
                .createReference("void");

        Set<ModifierKind> modifiers = new LinkedHashSet<ModifierKind>(2);
        modifiers.add(ModifierKind.PUBLIC);
        modifiers.add(ModifierKind.STATIC);

        String invocation = parent.getQualifiedName() + "(";
        if (parent.getConstructor() == null) {
            // CtTypeReference<?> superClass = parent.getSuperclass();
            if (parent.getConstructor(ctInvocation.getFactory().Class().STRING) != null) {
                invocation += "\"" + parent.getSimpleName() + "\"";
            } else {
                return;
            }
        }
        invocation += ")";

        CtBlock<?> body = getFactory().Core().createBlock();
        String bodyString = "for (String method : methods) {\n";
        bodyString += "\t\tSystem.out.println(method);\n\t\t";
        bodyString += parent.getQualifiedName() + " instance = new "
                + invocation + ";\n\t\t";
        if (parent.getMethod("setUp") != null) {
            bodyString += "instance.setUp();";
        }
        bodyString += parent.getQualifiedName()
                + ".class.getMethod(method, null).invoke(instance);\n\t\t";
        bodyString += "}\n";
        body.addStatement(getFactory().Code().createCodeSnippetStatement(
                bodyString));
        HashSet<CtTypeReference<? extends Throwable>> exceptions = new HashSet<CtTypeReference<? extends Throwable>>();
        exceptions.add(getFactory().Class().createReference(Exception.class));
        CtMethod<?> method = getFactory().Method().create(parent, modifiers,
                returntypedReference, "runJPFTest",
                new ArrayList<CtParameter<?>>(), exceptions, body);
        getFactory().Method()
                .createParameter(method, typedReference, "methods");
    }

}
