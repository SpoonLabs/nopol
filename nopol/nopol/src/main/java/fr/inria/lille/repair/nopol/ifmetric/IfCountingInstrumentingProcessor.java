package fr.inria.lille.repair.nopol.ifmetric;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.Filter;
import spoon.support.reflect.code.CtStatementListImpl;

import java.util.List;

/*
 * TODO : 
 * Missing 2 cases :
 * 	When method end with switch
 * 	When method end with if which has return on then and else
 * 
 */
public class IfCountingInstrumentingProcessor extends
        AbstractProcessor<CtMethod<?>> {
    /**
     *
     */
    private IfMetric ifMetric;

    public IfCountingInstrumentingProcessor() {
    }

    public IfCountingInstrumentingProcessor(IfMetric ifMetric) {
        this.ifMetric = ifMetric;
    }

    private void instrumentIfInsideMethod(CtIf elem) {

        String className = elem.getPosition().getCompilationUnit().getMainType().getSimpleName();

        if (elem.getThenStatement() != null) {
            StringBuilder snippet = new StringBuilder();
            snippet.append(IfMetric.THEN_EXECUTED_CALL).append("\"" + className + "\"")
                    .append(",").append(elem.getPosition().getLine() + ")");
            CtStatement call = getFactory().Code().createCodeSnippetStatement(
                    snippet.toString());
            if (!(elem.getThenStatement() instanceof CtBlock)) {
                CtBlock<?> block = getFactory().Core().createBlock();
                call.setParent(block);
                elem.getThenStatement().setParent(block);
                block.addStatement(call);
                block.addStatement(elem.getThenStatement());
                block.setParent(elem);
                elem.setThenStatement(block);
            } else {
                CtBlock<?> block = (CtBlock<?>) elem.getThenStatement();
                block.insertBegin(call);
            }
        }
        if (elem.getElseStatement() != null) {
            StringBuilder snippet = new StringBuilder();
            snippet.append(IfMetric.ELSE_EXECUTED_CALL).append("\"" + className + "\"")
                    .append(",").append(elem.getPosition().getLine() + ")");
            CtStatement call = getFactory().Code().createCodeSnippetStatement(
                    snippet.toString());
            if (!(elem.getElseStatement() instanceof CtBlock)) {
                CtBlock<?> block = getFactory().Core().createBlock();
                call.setParent(block);
                elem.getElseStatement().setParent(block);
                block.addStatement(call);
                block.addStatement(elem.getElseStatement());
                block.setParent(elem);
                elem.setElseStatement(block);
            } else {
                CtBlock<?> block = (CtBlock<?>) elem.getElseStatement();
                block.insertBegin(call);
            }
        } else { // generate else block if the if doesn't have one
            StringBuilder snippet = new StringBuilder();
            snippet.append(IfMetric.ELSE_EXECUTED_CALL).append("\"" + className + "\"")
                    .append(",").append(elem.getPosition().getLine() + ")");
            CtStatement call = getFactory().Code().createCodeSnippetStatement(
                    snippet.toString());
            CtBlock<?> block = getFactory().Core().createBlock();
            block.addStatement(call);
            elem.setElseStatement(block);
        }


    }

    private void instrumentMethod(CtMethod<?> method) {


        String className = method.getPosition().getCompilationUnit().getMainType().getSimpleName();

        if (method.getBody() != null) {
            StringBuilder snippet_compute = new StringBuilder();
            snippet_compute.append(IfMetric.COMPUTE_METRIC_CALL)
                    .append("\"" + className + "\"").append("+")
                    .append("\"").append(".")
                    .append(method.getSimpleName()).append("\")");
            CtStatement call_compute = getFactory().Code()
                    .createCodeSnippetStatement(snippet_compute.toString());

            StringBuilder snippet_reset = new StringBuilder();
            snippet_reset.append(IfMetric.RESET_METRIC_CALL);
            CtStatement call_reset = getFactory().Code()
                    .createCodeSnippetStatement(snippet_reset.toString());

            CtStatementList list_call = new CtStatementListImpl<>();
            list_call.addStatement(call_compute);
            list_call.addStatement(call_reset);
            /*
			 * Workaround, getLastStatement throw
			 * ArrayIndexOutOfBoundException when the method is empty
			 */
            try {
                CtStatement lastStatement = method.getBody()
                        .getLastStatement();
                if (lastStatement instanceof CtReturn) {
                    lastStatement.insertBefore(list_call);
                } else {
                    lastStatement.insertAfter(list_call);
                }
            } catch (ArrayIndexOutOfBoundsException aeoob) {
				/*
				 * Do nothing because of empty method
				 */
            }
        }

    }

    private boolean isTestCase(CtMethod<?> method) {
        boolean isNamedTest = method.getSimpleName().toLowerCase().endsWith("test"); // to detect TestCase under JUnit 3.x
        boolean hasTestAnnotation = false; // to detect TestCase under JUnit 4.x
        List<CtAnnotation<?>> listAnnotation = method.getAnnotations();
        for (CtAnnotation<?> tmp : listAnnotation) {
            if (tmp.getType().equals(method.getFactory().Annotation().createReference(org.junit.Test.class))) {
                hasTestAnnotation = true;
            }
        }

        return isNamedTest || hasTestAnnotation;
    }


    @Override
    public void process(final CtMethod<?> method) {
        if (method != null) {
            if (isTestCase(method)) {
                instrumentMethod(method);
            } else {
                if (method.getBody() != null) {
                    List<CtIf> ifList = method.getBody().getElements(
                            new Filter<CtIf>() {

                                @Override
                                public boolean matches(CtIf arg0) {
                                    if (!(arg0 instanceof CtIf)) {
                                        return false;
                                    }

                                    return true;
                                }
                            });
                    for (CtIf tmp : ifList) {
                        instrumentIfInsideMethod(tmp);
                    }
                }
            }

            String s = method.getDeclaringType().getQualifiedName();
            if (this.ifMetric != null && !this.ifMetric.modifyClass.contains(s)) {
                this.ifMetric.modifyClass.add(s);
            }


        }

    }


}