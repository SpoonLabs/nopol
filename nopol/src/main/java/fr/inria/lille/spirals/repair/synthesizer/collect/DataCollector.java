package fr.inria.lille.spirals.repair.synthesizer.collect;

import com.sun.jdi.*;
import com.sun.jdi.request.BreakpointRequest;
import fr.inria.lille.repair.common.config.Config;
import fr.inria.lille.repair.nopol.SourceLocation;
import fr.inria.lille.spirals.repair.commons.Candidates;
import fr.inria.lille.spirals.repair.expression.*;
import fr.inria.lille.spirals.repair.synthesizer.collect.factory.ExpressionFacotry;
import fr.inria.lille.spirals.repair.synthesizer.collect.filter.FieldFilter;
import fr.inria.lille.spirals.repair.synthesizer.collect.filter.MethodFilter;
import fr.inria.lille.spirals.repair.synthesizer.collect.spoon.StatCollector;
import fr.inria.lille.spirals.repair.vm.DebugJUnitRunner;
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
    private final Map<String, String> variableType;
    private final Set<String> calledMethods;
    private long executionTime;
    private long startTime;
    private long maxTime;

    public DataCollector(ThreadReference threadRef,
                         Candidates constants,
                         SourceLocation location,
                         String buggyMethod,
                         Set<String> classes,
                         StatCollector statCollector,
                         Map<String, String> variableType,
                         Set<String> calledMethods) {
        this.threadRef = threadRef;

        this.constants = constants;
        this.importedClasses = classes;
        this.buggyMethod = buggyMethod;
        this.location = location;
        this.statCollector = statCollector;
        this.variableType = variableType;
        this.calledMethods = calledMethods;
    }

    public Candidates collect(long maxTime) {
        this.maxTime = maxTime;
        this.startTime = System.currentTimeMillis();
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
            executionTime = System.currentTimeMillis() - startTime;
            // collect static fields
            if (Config.INSTANCE.isCollectStaticFields()) {
                List<Field> fields = stackFrame.location().declaringType().visibleFields();
                for (int i = 0; i < fields.size(); i++) {
                    Field field = fields.get(i);
                    Value value = stackFrame.location().declaringType().getValue(field);
                    ComplexTypeExpression complexConstant = new ComplexConstantImpl(stackFrame.location().declaringType().name(), stackFrame.location().declaringType());
                    Expression expression = ExpressionFacotry.create(complexConstant, field, value);
                    logger.debug("[data] " + expression);
                    candidates.add(expression);
                }
            }
            executionTime = System.currentTimeMillis() - startTime;
            stackFrame = threadRef.frame(0);

            collectVariables(stackFrame, candidates);
            candidates.add(new PrimitiveConstantImpl(0, threadRef.virtualMachine().mirrorOf(0), Integer.class));
            candidates.add(new PrimitiveConstantImpl(1, threadRef.virtualMachine().mirrorOf(1), Integer.class));
            // collect literals
            if (Config.INSTANCE.isCollectLiterals()) {
                candidates.addAll(constants);
            }
            Candidates previousLevel = new Candidates();
            previousLevel.addAll(candidates);
            // collect static methods
            if (Config.INSTANCE.isCollectStaticMethods()) {
                collectStaticMethods(statCollector.getStatMethod().keySet(), threadRef, candidates, previousLevel);
            }
            executionTime = System.currentTimeMillis() - startTime;
            for (int depth = 1; depth < Config.INSTANCE.getSynthesisDepth() && executionTime <= maxTime; depth++) {
                Candidates nextLevel = new Candidates();
                logger.debug("Collect Level " + (depth + 1));
                for (int i = 0; i < previousLevel.size() && executionTime <= maxTime; i++) {
                    executionTime = System.currentTimeMillis() - startTime;
                    Expression expression = previousLevel.get(i);
                    // collect fields and methods
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
                executionTime = System.currentTimeMillis() - startTime;
                // add collected results in the final results
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
                    executionTime = System.currentTimeMillis() - startTime;
                }
                executionTime = System.currentTimeMillis() - startTime;
            }
        } catch (IncompatibleThreadStateException e) {
            logger.error("Unable to collect eexp", e);
        }
        executionTime = System.currentTimeMillis() - startTime;
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
            executionTime = System.currentTimeMillis() - startTime;
            for (int i = 0; i < variables.size() && executionTime < maxTime; i++) {
                LocalVariable localVariable = variables.get(i);
                Value value = stackFrame.getValue(localVariable);
                Expression expression = ExpressionFacotry.create(localVariable, value);
                logger.debug("[data] " + expression);
                candidates.add(expression);
                executionTime = System.currentTimeMillis() - startTime;
            }
        } catch (AbsentInformationException e) {
            logger.error("Unable to collect variable on " + stackFrame, e);
        }
    }

    private void collectFields(ComplexTypeExpression exp, Candidates candidates) {
        if (exp instanceof Constant) {
            return;
        }
        ObjectReference ref = ((ObjectReference) exp.getValue());
        Map<Field, Value> fieldValues;
        try {
            List<Field> fields = ref.referenceType().visibleFields();
            fieldValues = ref.getValues(fields);
        } catch (Exception e) {
            return;
        }

        executionTime = System.currentTimeMillis() - startTime;
        for (Iterator<Field> iterator = fieldValues.keySet().iterator(); iterator.hasNext() && executionTime < maxTime; ) {
            Field field = iterator.next();
            // collect only public fields
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
            logger.debug("[data] " + expression);
            candidates.add(expression);
            executionTime = System.currentTimeMillis() - startTime;
        }
    }

    private List<List<Expression>> createAllPossibleArgsListForMethod(Method method, Candidates argsCandidates) {
        try {
            List<Type> argumentTypes = method.argumentTypes();
            List<List<Expression>> argumentCandidates = new ArrayList<>();
            for (int j = 0; j < argumentTypes.size(); j++) {
                Type type = argumentTypes.get(j);
                Candidates expressions = (argsCandidates.filter(type));
                argumentCandidates.add(expressions);
            }
            return combine(argumentCandidates);
        } catch (ClassNotLoadedException e) {
            List<String> strings = method.argumentTypeNames();
            boolean isClassLoaded = true;
            for (int i = 0; i < strings.size() && isClassLoaded; i++) {
                String s = strings.get(i);
                isClassLoaded = isClassLoaded && DebugJUnitRunner.loadClass(s, method.virtualMachine());
            }
            if (isClassLoaded) {
                return createAllPossibleArgsListForMethod(method, argsCandidates);
            }
            return new ArrayList<>();
        }
    }

    private ReferenceType getReferenceType(Expression exp) {
        if (exp.getValue() instanceof ObjectReference) {
            return ((ObjectReference) exp.getValue()).referenceType();
        } else if (exp.getValue() instanceof ReferenceType) {
            return (ReferenceType) exp.getValue();
        }
        return null;
    }

    private void collectMethods(ComplexTypeExpression exp, ThreadReference threadRef, Candidates candidates, Candidates argsCandidates) {
        ReferenceType ref = getReferenceType(exp);
        if (ref == null) {
            return;
        }
        boolean isStatic = exp.getValue() instanceof ReferenceType;

        // get all method of the reference
        List<Method> methods = getMethods(exp, ref, isStatic, threadRef);

        callMethods(exp, threadRef, methods, candidates, argsCandidates);
    }

    /**
     * Call static methods on imported class
     *
     * @param ctExecutableReferences
     * @param threadRef
     * @param candidates
     * @param argsCandidates
     */
    private void collectStaticMethods(Set<CtExecutableReference> ctExecutableReferences, ThreadReference threadRef, Candidates candidates, Candidates argsCandidates) {
        Iterator<CtExecutableReference> it = ctExecutableReferences.iterator();
        while (it.hasNext()) {
            CtExecutableReference next = it.next();
            List<ReferenceType> refs = threadRef.virtualMachine().classesByName(next.getDeclaringType().getQualifiedName());
            if (refs.size() == 0) {
                continue;
            }

            ReferenceType ref = refs.get(0);
            ComplexValue exp = new ComplexValueImpl(next.getDeclaringType().getQualifiedName(), ref);
            List<Method> methods = getMethods(exp, ref, true, threadRef, next.getSimpleName());
            callMethods(exp, threadRef, methods, candidates, argsCandidates);
        }
    }

    private List<Method> getMethods(ComplexTypeExpression exp, ReferenceType ref, boolean isStatic, ThreadReference threadRef) {
        return getMethods(exp, ref, isStatic, threadRef, null);
    }

    private List<Method> getMethods(ComplexTypeExpression exp, ReferenceType ref, boolean isStatic, ThreadReference threadRef, String methodNameFilter) {
        List<Method> methods = new ArrayList<>();
        // if the expression is a List use a specific method invocation
        List<ReferenceType> listReferences = threadRef.virtualMachine().classesByName(List.class.getCanonicalName());
        if (listReferences.size() > 0) {
            InterfaceType listReference = (InterfaceType) listReferences.get(0);
            if (exp.isAssignableTo(listReference)) {
                methods.addAll(ref.methodsByName("size"));
                methods.addAll(ref.methodsByName("contains"));
                methods.addAll(ref.methodsByName("equals"));
                methods.addAll(ref.methodsByName("isEmpty"));
                return methods;
            }
        }
        if (variableType.containsKey(exp.toString())) {
            List<ReferenceType> referenceTypes = threadRef.virtualMachine().classesByName(String.valueOf(variableType.get(exp.toString())));
            if (referenceTypes.size() > 0) {
                ref = referenceTypes.get(0);
            }
        }
        List<Method> visibleMethods = ref.visibleMethods();
        for (int i = 0; i < visibleMethods.size(); i++) {
            Method method = visibleMethods.get(i);
            if (!isToCall(exp, ref, method, isStatic))
                continue;
            if (methodNameFilter == null || method.name().equals(methodNameFilter))
                methods.add(method);
        }
        Collections.sort(methods, new Comparator<Method>() {
            @Override
            public int compare(Method m1, Method m2) {
                return m2.argumentTypeNames().size() - m1.argumentTypeNames().size();
            }
        });
        return methods;
    }

    private boolean isToCall(Expression exp, ReferenceType ref, Method method, boolean isStaticCall) {
        if (!exp.toString().equals("this") && !method.isPublic()) {
            return false;
        }
        // ignore recursive call
        if ((exp.toString().equals("this") || this.location.getContainingClassName().equals(ref.name()))
                && method.name().equals(buggyMethod)) {
            return false;
        }
        if (!MethodFilter.toProcess(method)) {
            return false;
        }
        // don't call static method on object reference
        if (isStaticCall && !method.isStatic()) {
            return false;
        }
        // ignore obsolete method
        if (method.isObsolete()) {
            return false;
        }
        // ignore all methods not previously used
        String className = method.declaringType().name();
        String qualifiedMethodName = className.substring(0, className.lastIndexOf(".")) + "." + method.name();
        if (!calledMethods.contains(qualifiedMethodName)) {
            return false;
        }
        return true;
    }

    private void callMethods(ComplexTypeExpression exp, ThreadReference threadRef, List<Method> methods, Candidates candidates, Candidates argsCandidates) {
        executionTime = System.currentTimeMillis() - startTime;
        // call all methods
        for (int i = 0; i < methods.size() && executionTime < maxTime; i++) {
            Method method = methods.get(i);
            int numberOfArgs = method.argumentTypeNames().size();
            if (0 == numberOfArgs) {
                candidates.add(callMethod(threadRef, exp, method, Collections.EMPTY_LIST));
                continue;
            }
            List<List<Expression>> allPossibleMethodArgs = createAllPossibleArgsListForMethod(method, argsCandidates);
            executionTime = System.currentTimeMillis() - startTime;
            int countFailCall = 0;
            for (int j = 0; j < allPossibleMethodArgs.size() && executionTime < maxTime; j++) {
                List<Expression> expressions = allPossibleMethodArgs.get(j);
                if (expressions.size() != numberOfArgs) {
                    break;
                }
                if (method.name().equals("equals")) {
                    Expression parameter = expressions.get(0);
                    if (parameter instanceof Constant) {
                        continue;
                    }
                }
                Expression expression = callMethod(threadRef, exp, method, expressions);
                if (expression == null) {
                    countFailCall++;
                    if (countFailCall >= 5) {
                        break;
                    }
                }
                candidates.add(expression);
                executionTime = System.currentTimeMillis() - startTime;
            }
            executionTime = System.currentTimeMillis() - startTime;
        }
    }

    private Expression callMethod(final ThreadReference threadRef, final ComplexTypeExpression exp, final Method method, List<Expression> expressions) {
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
                    } catch (InvalidTypeException | IncompatibleThreadStateException | InvocationException e) {
                        logger.error("Unable to invoke the method " + method + " " + argumentValue, e);
                    } catch (ClassNotLoadedException e) {
                        if (DebugJUnitRunner.loadClass(method.returnTypeName(), threadRef.virtualMachine())) {
                            call();
                        }
                    }
                    return null;
                }
            };
            boolean cast = !(variableType.containsKey(exp.toString()) || exp.toString().equals("this") || exp instanceof MethodInvocation);
            Future<Value> future = executor.submit(task);
            try {
                Value result = future.get(Config.INSTANCE.getTimeoutMethodInvocation(), TimeUnit.SECONDS);
                if (result != null) {
                    expression = ExpressionFacotry.create(exp, method, expressions, result, cast);
                    logger.debug("[data] " + expression);
                }
            } catch (Exception ex) {
                logger.error("Unable to call the method " + method, ex);
            } finally {
                future.cancel(true);
            }
        } catch (Exception e) {
            // ignore this exception cannot be handled
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
                if (expression.equals(expression1)) continue;
                if (expression instanceof Constant && expression1 instanceof Constant) continue;
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

    public long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }
}
