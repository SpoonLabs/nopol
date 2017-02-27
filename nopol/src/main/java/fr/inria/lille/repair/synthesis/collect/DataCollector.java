package fr.inria.lille.repair.synthesis.collect;

import com.sun.jdi.*;
import com.sun.jdi.request.BreakpointRequest;
import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.nopol.SourceLocation;
import fr.inria.lille.repair.common.Candidates;
import fr.inria.lille.repair.expression.Expression;
import fr.inria.lille.repair.expression.access.Literal;
import fr.inria.lille.repair.expression.access.Variable;
import fr.inria.lille.repair.expression.factory.AccessFactory;
import fr.inria.lille.repair.expression.value.ArrayValue;
import fr.inria.lille.repair.expression.value.TypeValue;
import fr.inria.lille.repair.synthesis.collect.filter.FieldFilter;
import fr.inria.lille.repair.synthesis.collect.filter.MethodFilter;
import fr.inria.lille.repair.synthesis.collect.spoon.StatCollector;
import fr.inria.lille.repair.vm.DebugJUnitRunner;
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
    private final NopolContext nopolContext;
    private long executionTime;
    private long startTime;
    private long maxTime;
    public final Candidates candidates = new Candidates();

    public DataCollector(ThreadReference threadRef,
                         Candidates constants,
                         SourceLocation location,
                         String buggyMethod,
                         Set<String> classes,
                         StatCollector statCollector,
                         Map<String, String> variableType,
                         Set<String> calledMethods,
                         NopolContext nopolContext) {
        this.threadRef = threadRef;
        this.nopolContext = nopolContext;

        this.constants = constants;
        this.importedClasses = classes;
        this.buggyMethod = buggyMethod;
        this.location = location;
        this.statCollector = statCollector;
        this.variableType = variableType;
        this.calledMethods = calledMethods;
    }
    
    /**
     * variables are collected at runtime
     * @param maxTime
     * @return
     */
    public Candidates collect(long maxTime) {
        this.maxTime = maxTime;
        this.startTime = System.currentTimeMillis();
        try {
            StackFrame stackFrame = threadRef.frame(0);

            logger.debug("Collect Level 1");
            // collect this
            if (stackFrame.thisObject() != null) {
                Variable variableThis = AccessFactory.variable("this", stackFrame.thisObject(), nopolContext);
                candidates.add(variableThis);
                logger.debug("[data] " + variableThis + "=" + variableThis.getValue());
            }
            executionTime = System.currentTimeMillis() - startTime;
            // collect static fields
            if (this.nopolContext.isCollectStaticFields()) {
                List<Field> fields = stackFrame.location().declaringType().visibleFields();
                for (Field field : fields) {
                    Value value = stackFrame.location().declaringType().getValue(field);
                    Variable complexConstant = AccessFactory.variable(stackFrame.location().declaringType().name(), stackFrame.location().declaringType(), nopolContext);
                    Variable expression = AccessFactory.variable(complexConstant, field.name(), value, nopolContext);
                    logger.debug("[data] " + expression);
                    candidates.add(expression);
                }
            }
            executionTime = System.currentTimeMillis() - startTime;
            stackFrame = threadRef.frame(0);

            // collect variables at runtime
            candidates.addAll(collectVariables(stackFrame));

            // special values;
            Literal literal0 = AccessFactory.literal(0, nopolContext);
            literal0.getValue().setJDIValue(threadRef.virtualMachine().mirrorOf(0));
            Literal literal1 = AccessFactory.literal(1, nopolContext);
            literal1.getValue().setJDIValue(threadRef.virtualMachine().mirrorOf(1));
            candidates.add(literal0);
            candidates.add(literal1);
            candidates.add(AccessFactory.literal(null, nopolContext));

            // collect literals
            if (this.nopolContext.isCollectLiterals()) {
                candidates.addAll(constants);
            }
            // collect static methods
            if (this.nopolContext.isCollectStaticMethods()) {
                candidates.addAll(collectStaticMethods(statCollector.getStatMethod().keySet(), threadRef, candidates));
            }

            recurse();

        } catch (IncompatibleThreadStateException e) {
            logger.error("Unable to collect eexp", e);
        }
        executionTime = System.currentTimeMillis() - startTime;
        return candidates;
    }


    private void recurse() {
        for (int depth = 1; depth < this.nopolContext.getSynthesisDepth() - 1 && executionTime <= maxTime; depth++) {
            Candidates copy = new Candidates(); // to avoid java.util.ConcurrentModificationException
            copy.addAll(candidates);
            executionTime = System.currentTimeMillis() - startTime;
            for (Expression expression : copy) {
            	if (!expression.getValue().isPrimitive() && expression.getValue().getJDIValue() instanceof ObjectReference) {
            		candidates.addAll(collectFieldAndMethodOnTheValueOf(expression, threadRef));
            	}
            }
        }

	}

	private Candidates collectFieldAndMethodOnTheValueOf(Expression exp, ThreadReference threadRef) {
    	Candidates results = new Candidates();
		if (!(exp.getValue().getJDIValue() instanceof ObjectReference)) {
			throw new IllegalArgumentException();
		}

		results.addAll(collectFields(exp));

		// the current set of expressions can be passed as parameters
		results.addAll(collectMethods(exp, threadRef, candidates));

        if (exp.getValue() instanceof ArrayValue) {
            int length = ((ArrayValue) exp.getValue()).length();
            results.add(AccessFactory.variable(exp, "length", length, nopolContext));
            if (length > 0) {
                results.add(AccessFactory.array(exp, AccessFactory.literal(0, nopolContext), nopolContext));
            }
        }
        return results;
    }

    private Candidates collectVariables(StackFrame stackFrame) {
    	Candidates results = new Candidates();
        try {
            List<LocalVariable> variables = stackFrame.visibleVariables();
            executionTime = System.currentTimeMillis() - startTime;
            for (int i = 0; i < variables.size() && executionTime < maxTime; i++) {
                LocalVariable localVariable = variables.get(i);
                Value value = stackFrame.getValue(localVariable);
                Expression expression = AccessFactory.variable(localVariable.name(), value, nopolContext);
                logger.debug("[data] " + expression);
                results.add(expression);
                executionTime = System.currentTimeMillis() - startTime;
            }
        } catch (AbsentInformationException e) {
            logger.error("Unable to collect variable on " + stackFrame, e);
        }
        return results;
    }

    private Candidates collectFields(Expression exp) {
    	Candidates results = new Candidates();
        if (exp.getValue().isPrimitive()) {
            return results;
        }
        ObjectReference ref = (ObjectReference) exp.getValue().getJDIValue();
        Map<Field, Value> fieldValues;
        try {
            List<Field> fields = ref.virtualMachine().classesByName(this.location.getContainingClassName()).get(0).visibleFields();
            fieldValues = ref.getValues(fields);
        } catch (Exception e) {
            return results;
        }

        executionTime = System.currentTimeMillis() - startTime;
        for (Iterator<Field> iterator = fieldValues.keySet().iterator(); iterator.hasNext() && executionTime < maxTime; ) {
            Field field = iterator.next();
            // collect only public fields
            if (!field.isPublic()) {
                if (exp.toString().equals("this")) {
                    if (!field.declaringType().name().equals(this.location.getContainingClassName())) {
                        if (field.isPrivate()) {
                            continue;
                        }
                    }
                } else {
                    continue;
                }
            }
            if (!FieldFilter.toProcess(field)) {
                continue;
            }

            Value value = fieldValues.get(field);
            Expression expression = AccessFactory.variable(exp, field.name(), value, nopolContext);
            if (expression == null) {
                continue;
            }
            if (field.isFinal() && field.isStatic()) {
                expression.getValue().setConstant(true);
            }
            logger.debug("[data] " + expression);
            results.add(expression);
            executionTime = System.currentTimeMillis() - startTime;
        }
        return results;

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
        if (exp.getValue().getJDIValue() instanceof ObjectReference) {
            return ((ObjectReference) exp.getValue().getJDIValue()).referenceType();
        } else if (exp.getValue() instanceof TypeValue) {
            return (ReferenceType) exp.getValue().getRealValue();
        }
        return null;
    }

    private Candidates collectMethods(Expression exp, ThreadReference threadRef, Candidates argsCandidates) {
    	Candidates results = new Candidates();
        ReferenceType ref = getReferenceType(exp);
        if (ref == null) {
            return results;
        }
        if (exp.getValue().isConstant()) {
            return results;
        }
        boolean isStatic = exp.getValue() instanceof TypeValue;

        // get all method of the reference
        List<Method> methods = getMethods(exp, ref, isStatic, threadRef);

        results.addAll(callMethods(exp, threadRef, methods, argsCandidates));
        return results;
    }

    /**
     * Call static methods on imported class
     *  @param ctExecutableReferences
     * @param threadRef
     * @param argsCandidates
     */
    private Candidates collectStaticMethods(Set<CtExecutableReference> ctExecutableReferences, ThreadReference threadRef, Candidates argsCandidates) {
        Candidates results = new Candidates();
        Iterator<CtExecutableReference> it = ctExecutableReferences.iterator();
        while (it.hasNext()) {
            CtExecutableReference next = it.next();
            List<ReferenceType> refs = threadRef.virtualMachine().classesByName(next.getDeclaringType().getQualifiedName());
            if (refs.size() == 0) {
                continue;
            }

            ReferenceType ref = refs.get(0);
            Expression exp = AccessFactory.variable(next.getDeclaringType().getQualifiedName(), ref, nopolContext);
            List<Method> methods = getMethods(exp, ref, true, threadRef, next.getSimpleName());
            results.addAll(callMethods(exp, threadRef, methods, argsCandidates));
        }
        return results;
    }

    private List<Method> getMethods(Expression exp, ReferenceType ref, boolean isStatic, ThreadReference threadRef) {
        return getMethods(exp, ref, isStatic, threadRef, null);
    }

    private List<Method> getMethods(Expression exp, ReferenceType ref, boolean isStatic, ThreadReference threadRef, String methodNameFilter) {
        List<Method> methods = new ArrayList<>();

        if (variableType.containsKey(exp.toString())) {
            List<ReferenceType> referenceTypes = threadRef.virtualMachine().classesByName(String.valueOf(variableType.get(exp.toString())));
            if (referenceTypes.size() > 0) {
                ref = referenceTypes.get(0);
            }
        }

        List<Method> visibleMethods = ref.visibleMethods();
        for (Method method : visibleMethods) {
            boolean toCall = isToCall(exp, ref, method, isStatic);
            if (!toCall)
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
        if(this.nopolContext.isCollectOnlyUsedMethod()) {
            // ignore all methods not previously used
            String className = method.declaringType().name();
            String qualifiedMethodName =  className.substring(0, className.lastIndexOf(".")) + "." + method.name();
            if (!calledMethods.contains(qualifiedMethodName)) {
                return false;
            }
        }
        return true;
    }

    /** modifiy "candidates" in place */
    private Candidates callMethods(Expression exp, ThreadReference threadRef, List<Method> methods, Candidates argsCandidates) {
    	Candidates results = new Candidates();
        executionTime = System.currentTimeMillis() - startTime;
        // call all methods
        for (int i = 0; i < methods.size() && executionTime < maxTime; i++) {
            Method method = methods.get(i);
            int numberOfArgs = method.argumentTypeNames().size();
            if (0 == numberOfArgs) {
                Expression returnValue = callMethod(threadRef, exp, method, Collections.EMPTY_LIST);
                results.add(returnValue);
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
                    if (parameter.getValue().isConstant()) {
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
                results.add(expression);
                executionTime = System.currentTimeMillis() - startTime;
            }
            executionTime = System.currentTimeMillis() - startTime;
        }
        return results;
    }

    private Expression callMethod(final ThreadReference threadRef, final Expression exp, final Method method, List<Expression> expressions) {
        Expression expression = null;
        disableEventRequest();
        try {
            final List<Value> argumentValue = new ArrayList<>();
            for (Expression e : expressions) {
                argumentValue.add(e.getValue().getJDIValue());
            }
            final StackFrame stackFrame = threadRef.frame(0);
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Callable<Value> task = new Callable<Value>() {
                public Value call() {
                    try {
                        Object jdiValue = exp.getValue().getJDIValue();
                        if (exp.getValue() instanceof TypeValue) {
                            jdiValue = exp.getValue().getRealValue();
                        }
                        if (jdiValue instanceof ObjectReference) {
                            ObjectReference ref = ((ObjectReference) jdiValue);
                            return ref.invokeMethod(stackFrame.thread(), method, argumentValue, ObjectReference.INVOKE_SINGLE_THREADED);
                        } else if (jdiValue instanceof ClassType) {
                            ClassType ref = ((ClassType) jdiValue);
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
            boolean cast = !(variableType.containsKey(exp.toString()) || exp.toString().equals("this") || exp instanceof fr.inria.lille.repair.expression.access.Method);
            Future<Value> future = executor.submit(task);
            try {
                executor.shutdown();
                Value result = future.get(this.nopolContext.getTimeoutMethodInvocation(), TimeUnit.SECONDS);
                if (result != null) {
                    List<String> argumentTypes = new ArrayList<String>();
                    try {
                        List<Type> types = method.argumentTypes();
                        for (Type type : types) {
                            argumentTypes.add(type.name());
                        }
                    } catch (ClassNotLoadedException e) {
                        e.printStackTrace();
                    }
                    expression = AccessFactory.method(method.name(), argumentTypes, method.declaringType().name(), exp, expressions, result, nopolContext);
                    logger.debug("[data] " + expression);
                }
            } catch (Exception ex) {
                logger.error("Unable to call the method " + method, ex);
            	throw new RuntimeException(ex);
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
                if (expression.getValue().isConstant() && expression1.getValue().isConstant()) continue;
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
