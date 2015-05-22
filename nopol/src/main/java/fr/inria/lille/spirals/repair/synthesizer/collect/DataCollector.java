package fr.inria.lille.spirals.repair.synthesizer.collect;

import com.sun.jdi.*;
import com.sun.jdi.request.BreakpointRequest;
import fr.inria.lille.repair.nopol.SourceLocation;
import fr.inria.lille.spirals.repair.commons.Candidates;
import fr.inria.lille.repair.common.config.Config;
import fr.inria.lille.spirals.repair.expression.*;
import fr.inria.lille.spirals.repair.synthesizer.collect.factory.ExpressionFacotry;
import fr.inria.lille.spirals.repair.synthesizer.collect.filter.FieldFilter;
import fr.inria.lille.spirals.repair.synthesizer.collect.filter.MethodFilter;
import fr.inria.lille.spirals.repair.synthesizer.collect.spoon.StatCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.reflect.reference.CtExecutableReference;

import java.util.*;
import java.util.concurrent.*;

/**
 * Created by Thomas Durieux on 06/03/15.
 */
public class DataCollector {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ThreadReference threadRef;
    private final Candidates constants;
    private final Set<String> importedClasses;
    private final String buggyMethod;
    private final SourceLocation location;
    private final StatCollector statCollector;

    public DataCollector(ThreadReference threadRef, Candidates constants, SourceLocation location, String buggyMethod, Set<String> classes, StatCollector statCollector) {
        this.threadRef = threadRef;

        this.constants = constants;
        this.importedClasses = classes;
        this.buggyMethod = buggyMethod;
        this.location = location;
        this.statCollector = statCollector;
    }

    public Candidates collect() {
        Candidates candidates = new Candidates();
        try {
            StackFrame stackFrame = threadRef.frame(0);

            logger.debug("Collect Level 1");
            // collect this
            if (stackFrame.thisObject() != null) {
                ComplexValue variableThis = new ComplexValueImpl("this", stackFrame.thisObject());
                candidates.add(variableThis);
                logger.debug("[data] " + variableThis + "=" + variableThis.getValue());
            }
            if (Config.INSTANCE.isCollectStaticFields()) {
                List<Field> fields = stackFrame.location().declaringType().visibleFields();
                for (int i = 0; i < fields.size(); i++) {
                    Field field = fields.get(i);
                    Value value = stackFrame.location().declaringType().getValue(field);
                    ComplexTypeExpression complexConstant = new ComplexConstantImpl(stackFrame.location().declaringType().name(), stackFrame.location().declaringType());
                    Expression expression = ExpressionFacotry.create(complexConstant, field, value);
                    logger.debug("[data] " + expression + "=" + expression.getValue());
                    candidates.add(expression);
                }
            }
            stackFrame = threadRef.frame(0);

            collectVariables(stackFrame, candidates);
            candidates.add(new PrimitiveConstantImpl(0, Integer.class));
            candidates.add(new PrimitiveConstantImpl(1, Integer.class));
            candidates.add(new ComplexConstantImpl("null", null));
            if (Config.INSTANCE.isCollectLiterals()) {
                candidates.addAll(constants);
            }
            if (Config.INSTANCE.isCollectStaticMethods()) {
                collectMethods(statCollector.getStatMethod().keySet(), threadRef, candidates, candidates);
            }
            Candidates previousLevel = new Candidates();
            previousLevel.addAll(candidates);
            for (int depth = 1; depth < Config.INSTANCE.getSynthesisDepth(); depth++) {
                Candidates nextLevel = new Candidates();
                logger.debug("Collect Level " + (depth + 1));
                for (int i = 0; i < previousLevel.size(); i++) {
                    Expression expression = previousLevel.get(i);
                    if (expression.getValue() instanceof ObjectReference) {
                        Candidates current = collect((ComplexTypeExpression) expression, threadRef, candidates);
                        for (int j = 0; j < current.size(); j++) {
                            Expression o = current.get(j);
                            if (!nextLevel.contains(o)) {
                                nextLevel.add(o);
                            } else {
                                List<Expression> alternatives = nextLevel.get(nextLevel.indexOf(o)).getAlternatives();
                                alternatives.addAll(o.getAlternatives());
                                o.getAlternatives().clear();
                                alternatives.add(o);
                            }
                        }
                    }
                }
                previousLevel = new Candidates();
                for (int j = 0; j < nextLevel.size(); j++) {
                    Expression o = nextLevel.get(j);
                    if (!candidates.contains(o)) {
                        candidates.add(o);
                        previousLevel.add(o);
                    } else {
                        List<Expression> alternatives = candidates.get(candidates.indexOf(o)).getAlternatives();
                        alternatives.addAll(o.getAlternatives());
                        o.getAlternatives().clear();
                        alternatives.add(o);
                    }
                }
            }
        } catch (IncompatibleThreadStateException e) {
            e.printStackTrace();
        }

        return candidates;
    }



    private Candidates collect(ComplexTypeExpression exp, ThreadReference threadRef, Candidates candidates) {
        Candidates results = new Candidates();
        collectFields(exp, results);
        collectMethods(exp, threadRef, results, candidates);

        return results;
    }

    private void collectVariables(StackFrame stackFrame, Candidates candidates) {
        try {
            List<LocalVariable> variables = stackFrame.visibleVariables();
            for (int i = 0; i < variables.size(); i++) {
                LocalVariable localVariable = variables.get(i);
                Value value = stackFrame.getValue(localVariable);
                Expression expression = ExpressionFacotry.create(localVariable, value);
                logger.debug("[data] " + expression + "=" + expression.getValue());
                candidates.add(expression);
            }

        } catch (AbsentInformationException e) {
            e.printStackTrace();
        }
    }

    private void collectFields(ComplexTypeExpression exp, Candidates candidates) {
        if (exp instanceof Constant) {
            return;
        }
        ObjectReference ref = ((ObjectReference) exp.getValue());
        List<Field> fields = ref.referenceType().visibleFields();
        Map<Field, Value> fieldValues = ref.getValues(fields);
        for (Iterator<Field> iterator = fieldValues.keySet().iterator(); iterator.hasNext(); ) {
            Field field = iterator.next();
            if (!exp.toString().equals("this") && !field.isPublic()) {
                continue;
            }
            if (!FieldFilter.toProcess(field)) {
                continue;
            }

            Value value = fieldValues.get(field);
            Expression expression = ExpressionFacotry.create(exp, field, value);
            if (expression == null) {
                continue;
            }
            logger.debug("[data] " + expression + "=" + expression.getValue());
            candidates.add(expression);
        }
    }

    private void collectMethods(Set<CtExecutableReference> ctExecutableReferences, ThreadReference threadRef, Candidates candidates, Candidates params) {
        Iterator<CtExecutableReference> it = ctExecutableReferences.iterator();
        while (it.hasNext()) {
            CtExecutableReference next = it.next();
            List<ReferenceType> refs = threadRef.virtualMachine().classesByName(next.getDeclaringType().getQualifiedName());
            if (refs.size() == 0) {
                continue;
            }

            ReferenceType ref = refs.get(0);
            ComplexValue exp = new ComplexValueImpl(next.getDeclaringType().getQualifiedName(), ref);
            collectMethods(exp, next.getSimpleName(), threadRef, candidates, params);
        }
    }

    private void collectMethods(ComplexValue exp, String methodName, ThreadReference threadRef, Candidates candidates, Candidates parmas) {
        boolean isStatic;
        ReferenceType ref;
        if (exp.getValue() instanceof ObjectReference) {
            isStatic = false;
            ref = ((ObjectReference) exp.getValue()).referenceType();
        } else if (exp.getValue() instanceof ReferenceType) {
            isStatic = true;
            ref = (ReferenceType) exp.getValue();
        } else {
            return;
        }
        List<Method> methods = ref.methodsByName(methodName);
        for (int i = 0; i < methods.size(); i++) {
            Method method = methods.get(i);
            if (!exp.toString().equals("this") && !method.isPublic()) {
                continue;
            }
            if ((exp.toString().equals("this") || this.location.getContainingClassName().equals(ref.name()) )
                    && method.name().equals(buggyMethod)) {
                continue;
            }
            if (!MethodFilter.toProcess(method)) {
                continue;
            }
            if (isStatic && !method.isStatic()) {
                continue;
            }
            try {
                List<Type> argumentTypes = method.argumentTypes();
                List<List<Expression>> argumentCandidates = new ArrayList<>();
                for (int j = 0; j < argumentTypes.size(); j++) {
                    Type type = argumentTypes.get(j);
                    Candidates expressions = (parmas.filter(type));
                    argumentCandidates.add(expressions);
                }
                argumentCandidates = combine(argumentCandidates);
                if (argumentCandidates.size() == 0 && 0 == method.argumentTypeNames().size()) {
                    candidates.add(callMethod(threadRef, exp, method, Collections.EMPTY_LIST));
                }
                for (int j = 0; j < argumentCandidates.size(); j++) {
                    List<Expression> expressions = argumentCandidates.get(j);
                    if (expressions.size() != method.argumentTypeNames().size()) {
                        break;
                    }
                    candidates.add(callMethod(threadRef, exp, method, expressions));
                }
            } catch (ClassNotLoadedException e) {
                e.printStackTrace();
            }
        }
    }

    private void collectMethods(ComplexTypeExpression exp, ThreadReference threadRef, Candidates candidates, Candidates parmas) {
        boolean isStatic;
        ReferenceType ref;
        if (exp.getValue() instanceof ObjectReference) {
            isStatic = false;
            ref = ((ObjectReference) exp.getValue()).referenceType();
        } else if (exp.getValue() instanceof ReferenceType) {
            isStatic = true;
            ref = (ReferenceType) exp.getValue();
        } else {
            return;
        }

        List<Method> methods = ref.visibleMethods();
        for (int i = 0; i < methods.size(); i++) {
            Method method = methods.get(i);
            if (!exp.toString().equals("this") && !method.isPublic()) {
                continue;
            }
            if ((exp.toString().equals("this") || this.location.getContainingClassName().equals(ref.name()) )
                    && method.name().equals(buggyMethod)) {
                continue;
            }
            if (!MethodFilter.toProcess(method)) {
                continue;
            }
            if (isStatic && !method.isStatic()) {
                continue;
            }
            try {
                List<Type> argumentTypes = method.argumentTypes();
                List<List<Expression>> argumentCandidates = new ArrayList<>();
                for (int j = 0; j < argumentTypes.size(); j++) {
                    Type type = argumentTypes.get(j);
                    Candidates expressions = (parmas.filter(type));
                    argumentCandidates.add(expressions);
                }
                argumentCandidates = combine(argumentCandidates);
                if (argumentCandidates.size() == 0 && 0 == method.argumentTypeNames().size()) {
                    candidates.add(callMethod(threadRef, exp, method, Collections.EMPTY_LIST));
                }
                for (int j = 0; j < argumentCandidates.size(); j++) {
                    List<Expression> expressions = argumentCandidates.get(j);
                    if (expressions.size() != method.argumentTypeNames().size()) {
                        break;
                    }
                    candidates.add(callMethod(threadRef, exp, method, expressions));
                }
            } catch (ClassNotLoadedException e) {
                e.printStackTrace();
            }
        }
    }

    private Expression callMethod(ThreadReference threadRef, final ComplexTypeExpression exp, final Method method, List<Expression> expressions) {
        Expression expression = null;
        disableEventRequest();
        try {
            final List<Value> argumentValue = new ArrayList<>();
            for (int k = 0; k < expressions.size(); k++) {
                Expression e = expressions.get(k);
                java.lang.reflect.Method m = e.getClass().getMethod("getJdiValue");
                Value v = (Value) m.invoke(e);
                argumentValue.add(v);
            }
            final StackFrame stackFrame = threadRef.frame(0);
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Callable<Value> task = new Callable<Value>() {
                public Value call() {
                    try {
                        if (exp.getValue() instanceof ObjectReference) {
                            ObjectReference ref = ((ObjectReference) exp.getValue());
                            return ref.invokeMethod(stackFrame.thread(), method, argumentValue, ObjectReference.INVOKE_SINGLE_THREADED);
                        } else if (exp.getValue() instanceof ReferenceType) {
                            ClassType ref = ((ClassType) exp.getValue());
                            return ref.invokeMethod(stackFrame.thread(), method, argumentValue, ObjectReference.INVOKE_SINGLE_THREADED);
                        }
                    } catch (InvalidTypeException | ClassNotLoadedException | IncompatibleThreadStateException | InvocationException e) {
                        logger.warn("Unable to invoke the method " + method, e);
                    }
                    return null;
                }
            };

            Future<Value> future = executor.submit(task);
            try {
                Value result = future.get(Config.INSTANCE.getTimeoutMethodInvocation(), TimeUnit.MILLISECONDS);
                if (result != null) {
                    expression = ExpressionFacotry.create(exp, method, expressions, result);
                    logger.debug("[data] " + expression + "=" + expression.getValue());
                }
            } catch (TimeoutException ex) {
                // handle the timeout
            } catch (InterruptedException e) {
                // handle the interrupts
            } catch (ExecutionException e) {
                // handle other exceptions
            } finally {
                future.cancel(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        enableEventRequest();
        return expression;
    }

    private List<List<Expression>> combine(List<List<Expression>> expressions) {
        if (expressions.size() == 0) {
            return expressions;
        }
        if (expressions.size() == 2) {
            return combine(expressions.get(0), expressions.get(1));
        }
        List<List<Expression>> lists = new ArrayList<>();
        if (expressions.size() == 1) {
            for (int i = 0; i < expressions.get(0).size(); i++) {
                Expression expression = expressions.get(0).get(i);
                List<Expression> list = new ArrayList<>(1);
                list.add(expression);
                lists.add(list);
            }
            return lists;
        }
        List<Expression> last = expressions.get(expressions.size() - 1);
        for (int i = 0; i < last.size(); i++) {
            Expression a = last.get(i);
            List<List<Expression>> b = combine(expressions.subList(0, expressions.size() - 2));
            for (int j = 0; j < b.size(); j++) {
                List<Expression> expressionList = b.get(j);
                expressionList.add(a);
                lists.add(expressionList);
            }
        }
        return lists;
    }

    private List<List<Expression>> combine(List<Expression> a, List<Expression> b) {
        List<List<Expression>> lists = new ArrayList<>();
        for (int i = 0; i < a.size(); i++) {
            Expression expression = a.get(i);
            for (int j = 0; j < b.size(); j++) {
                Expression expression1 = b.get(j);
                List<Expression> list = new ArrayList<>();
                list.add(expression);
                list.add(expression1);
                lists.add(list);
            }
        }
        return lists;
    }

    private void disableEventRequest() {
        List<BreakpointRequest> breakpoint = threadRef.virtualMachine().eventRequestManager().breakpointRequests();
        for (int j = 0; j < breakpoint.size(); j++) {
            BreakpointRequest breakpointRequest = breakpoint.get(j);
            breakpointRequest.setEnabled(false);
        }

    }

    private void enableEventRequest() {
        List<BreakpointRequest> breakpoint = threadRef.virtualMachine().eventRequestManager().breakpointRequests();
        for (int j = 0; j < breakpoint.size(); j++) {
            BreakpointRequest breakpointRequest = breakpoint.get(j);
            breakpointRequest.setEnabled(true);
        }
    }
}
