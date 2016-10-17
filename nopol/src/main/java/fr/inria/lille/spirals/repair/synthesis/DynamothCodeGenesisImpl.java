package fr.inria.lille.spirals.repair.synthesis;

import com.sun.jdi.*;
import com.sun.jdi.event.*;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.EventRequestManager;
import fr.inria.lille.commons.spoon.SpoonedProject;
import fr.inria.lille.repair.common.config.Config;
import fr.inria.lille.repair.nopol.SourceLocation;
import fr.inria.lille.spirals.repair.commons.Candidates;
import fr.inria.lille.spirals.repair.expression.Expression;
import fr.inria.lille.spirals.repair.expression.access.*;
import fr.inria.lille.spirals.repair.expression.factory.AccessFactory;
import fr.inria.lille.spirals.repair.synthesis.collect.DataCollector;
import fr.inria.lille.spirals.repair.synthesis.collect.DataCombiner;
import fr.inria.lille.spirals.repair.synthesis.collect.SpoonElementsCollector;
import fr.inria.lille.spirals.repair.synthesis.collect.spoon.*;
import fr.inria.lille.spirals.repair.vm.DebugJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Thomas Durieux on 06/03/15.
 */
public class DynamothCodeGenesisImpl implements DynamothCodeGenesis {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final File[] projectRoots;
    private final SourceLocation location;
    private final URL[] classpath;
    private final Map<String, Object[]> oracle;
    private final String[] tests;
    
    /** key: test name, value: list of runtime contexts (if a statement is executed several times in the same test */
    private final SortedMap<String, List<Candidates>> values;
    private final Config config;
    private SpoonedProject spoon;
    private final Set<String> checkedExpression = new HashSet<>();
    private int nbExpressionEvaluated = 0;

    private String currentTestClass;
    private String currentTestMethod;
    private int currentIteration;
    private VirtualMachine vm;
    private Candidates constants;
    private Set<String> classes;
    private int nbBreakPointCalls = 0;
    private long startTime;
    private long initExecutionTime;
    private long collectExecutionTime;
    private String buggyMethod;
    private SpoonElementsCollector spoonElementsCollector;
    private StatCollector statCollector;
    private Map<String, String> variableType;
    private Set<String> calledMethods;
    private long remainingTime;

	private final int dataCollectionTimeoutInSeconds;

	/**
     * Create a new DynaMoth synthesizer
     * @param spoon the spoon instance of the project
     * @param projectRoots the root folders of the project
     * @param location the location of the code to synthesizer
     * @param classpath the classpath of the project
     * @param oracle the oracle of the project Map<testClass#testMethod, {value iteration 1, value iteration 2, ...}>
     * @param tests tests to execute
     */
    public DynamothCodeGenesisImpl(SpoonedProject spoon, File[] projectRoots, SourceLocation location, URL[] classpath, Map<String, Object[]> oracle, String[] tests, Config config) {
        this(projectRoots, location, classpath, oracle, tests,5*60 /* 5 minutes, default in repair mode */, config);
        this.spoon = spoon;
    }

	/**
	 * Create a new DynaMoth synthesizer
     * @param projectRoots the root folders of the project
     * @param location the location of the code to synthesiz
     * @param classpath the classpath of the project
     * @param oracle the oracle of the project Map<testClass#testMethod, {value iteration 1, value iteration 2, ...}>
     * @param tests tests to execute
     */
    public DynamothCodeGenesisImpl(File[] projectRoots, SourceLocation location, URL[] classpath, Map<String, Object[]> oracle, String[] tests, int dataCollectionTimeoutInSeconds, Config config) {
        this.projectRoots = projectRoots;
        this.location = location;
        this.dataCollectionTimeoutInSeconds=dataCollectionTimeoutInSeconds;
        this.oracle = oracle;
        this.tests = tests;
        this.values = new TreeMap<>();
        this.config = config;

        this.constants = new Candidates();
        this.classes = new HashSet<>();
        ClassLoader cl = ClassLoader.getSystemClassLoader();

        URL[] urls = ((URLClassLoader) cl).getURLs();
        ArrayList<URL> liClasspath = new ArrayList<>();
        for (int i = 0; i < classpath.length; i++) {
            URL url = classpath[i];
            File file = new File(url.getFile());
            if (file.exists()) {
                liClasspath.add(url);
            }
        }
        for (int i = 0; i < urls.length; i++) {
            URL url = urls[i];
            File file = new File(url.getFile());
            if (file.exists()) {
                liClasspath.add(url);
            }
        }
        this.classpath = liClasspath.toArray(new URL[0]);
    }

    @Override
    public Candidates run(long remainingTime) {
        this.remainingTime = remainingTime;
        this.startTime = System.currentTimeMillis();
        Iterator<Object[]> iterator = oracle.values().iterator();
        Object last = null;
        boolean same = false;
        while (iterator.hasNext() && !same) {
            Object[] next = iterator.next();

            for (int i = 0; i < next.length; i++) {
                Object o = next[i];
                if (last == null) {
                    last = o;
                    continue;
                }
                if (o != last) {
                    same = false;
                    break;
                }
            }
        }
        if (same) {
            Candidates candidates = new Candidates();
            candidates.add(AccessFactory.literal(last, config));
            return candidates;
        }
        try {
            vm = DebugJUnitRunner.run(tests, classpath);
            watchBuggyClass();
            vm.resume();
            processVMEvents();
            this.collectExecutionTime = System.currentTimeMillis();
            if (values.size()==0) {
            	throw new RuntimeException("should not happen, no value collected");
            }
            validExpressions = combineValues();
            DebugJUnitRunner.shutdown(vm);
            return validExpressions;
        } catch (IOException e) {
            throw new RuntimeException("Unable to communicate with the project", e);
        }
    }

    private void processVMEvents() {
        try {
            // process events
            final EventQueue eventQueue = vm.eventQueue();
            while (true) {
                EventSet eventSet = eventQueue.remove(TimeUnit.SECONDS.toMillis(this.dataCollectionTimeoutInSeconds ));
            	if (eventSet==null) return; // timeout
                for (Event event : eventSet) {
                    if (event instanceof VMDeathEvent || event instanceof VMDisconnectEvent) {
                        // exit
                        DebugJUnitRunner.process.destroy();
                        logger.debug("Exit");
                        return;
                    } else if (event instanceof ClassPrepareEvent) {
                        logger.debug("ClassPrepareEvent");
                        processClassPrepareEvent();
                    } else if (event instanceof BreakpointEvent) {
                        logger.debug("BreakpointEvent");
                        processBreakPointEvents((BreakpointEvent) event);
                    }
                }
                eventSet.resume();
            } //end while true
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
            DebugJUnitRunner.process.destroy();
        }
    }

    private void processClassPrepareEvent() throws AbsentInformationException {
        EventRequestManager erm = vm.eventRequestManager();
        List<ReferenceType> referenceTypes = vm.classesByName(this.location.getContainingClassName());
        List listOfLocations = referenceTypes.get(0).locationsOfLine(this.location.getLineNumber());
        if (listOfLocations.size() == 0) {
            throw new RuntimeException("Buggy class not found " + this.location);
        }
        com.sun.jdi.Location jdiLocation = (com.sun.jdi.Location) listOfLocations.get(0);
        this.buggyMethod = jdiLocation.method().name();
        breakpointSuspicious = erm.createBreakpointRequest(jdiLocation);
        breakpointSuspicious.setEnabled(true);
        initSpoon();
        this.initExecutionTime = System.currentTimeMillis();
    }

    private boolean jumpEnabled = false;
    private BreakpointRequest breakpointJump;
    private BreakpointRequest breakpointSuspicious;

	private Candidates validExpressions;

    private void jumpEndTest(ThreadReference threadRef) {
        try {
            List<StackFrame> frames = threadRef.frames();
            for (StackFrame stackFrame : frames) {
                for (String test : tests) {
                    String[] splitted = test.split("#");
                    test = splitted[0];
                    ObjectReference thisObject = stackFrame.thisObject();
                    if (thisObject == null) {
                        continue;
                    }
                    String frameClass = thisObject.referenceType().name();
                    if (frameClass.equals(test)) {
                        String frameMethod = stackFrame.location().method().name();
                        if (oracle.containsKey(test + "#" + frameMethod)) {
                            EventRequestManager erm = vm.eventRequestManager();
                            try {
                                List listOfLocations = stackFrame.location().declaringType().locationsOfLine(stackFrame.location().lineNumber());
                                if (listOfLocations.size() == 0) {
                                    continue;
                                }
                                Location jdiLocation = (Location) listOfLocations.get(listOfLocations.size() - 1);
                                breakpointJump = erm.createBreakpointRequest(jdiLocation);
                                breakpointJump.setEnabled(true);
                                jumpEnabled = true;
                                breakpointSuspicious.setEnabled(false);
                                return;
                            } catch (AbsentInformationException e) {
                                // ignore
                            }
                        }
                    }
                }
            }
        } catch (IncompatibleThreadStateException e) {
            e.printStackTrace();
        }
    }

    private void processBreakPointEvents(BreakpointEvent breakpointEvent) throws IncompatibleThreadStateException {
        if (jumpEnabled) {
            breakpointJump.setEnabled(false);
            jumpEnabled = false;
            breakpointSuspicious.setEnabled(true);
            return;
        }
        nbBreakPointCalls++;
        ThreadReference threadRef = breakpointEvent.thread();
        try {
            getCurrentTest(threadRef);
        } catch (RuntimeException e) {
            return;
        }
        if (!oracle.containsKey(currentTestClass + "#" + currentTestMethod)) {
            return;
        }
        if (values.containsKey(currentTestClass + "#" + currentTestMethod)) {
            if (values.get(currentTestClass + "#" + currentTestMethod).size() > config.getMaxLineInvocationPerTest()) {
                jumpEndTest(threadRef);
                return;
            }
        }
        
        if (!values.containsKey(currentTestClass + "#" + currentTestMethod)) {
            values.put(currentTestClass + "#" + currentTestMethod, new ArrayList<Candidates>());
        }

        Candidates allValues = new Candidates();
        
        Candidates expressionCollectedBySpoon = spoonElementsCollector.collect(threadRef);        
        Candidates expressionsCollectedAtRuntime = collectRuntimeValues(threadRef);
        
        allValues.addAll(expressionCollectedBySpoon);
        allValues.addAll(expressionsCollectedAtRuntime);
        
//        System.out.println("expressionCollectedBySpoon "+expressionCollectedBySpoon);
//        System.out.println("expressionsCollectedAtRuntime "+expressionsCollectedAtRuntime);
        values.get(currentTestClass + "#" + currentTestMethod).add(allValues);
    }

    private void getCurrentTest(ThreadReference threadRef) {
        try {
            List<StackFrame> frames = threadRef.frames();
            for (StackFrame stackFrame : frames) {
                for (String test : tests) {
                    String[] splitted = test.split("#");
                    test = splitted[0];
                    ObjectReference thisObject = stackFrame.thisObject();
                    if (thisObject == null) {
                        continue;
                    }
                    String frameClass = thisObject.referenceType().name();
                    if (frameClass.equals(test)) {
                        String frameMethod = stackFrame.location().method().name();
                        if (oracle.containsKey(test + "#" + frameMethod)) {
                            if (frameClass.equals(currentTestClass) && frameMethod.equals(currentTestMethod)) {
                                this.currentIteration++;
                            } else {
                                currentTestClass = test;
                                currentTestMethod = frameMethod;
                                this.currentIteration = 0;
                            }
                            logger.info("[test] " + currentTestClass + "#" + currentTestMethod + " iteration " + this.currentIteration);
                            return;
                        }
                    }
                }
            }
        } catch (IncompatibleThreadStateException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Unable to identify the current test");
    }

    private void initSpoon() {
        File classFile = null;
        for (File projectRoot : projectRoots) {
            classFile = new File(projectRoot.getAbsoluteFile() + "/" + this.location.getContainingClassName().replaceAll("\\.", "/") + ".java");
            if (classFile.exists()) {
                break;
            }
            classFile = null;
        }
        if (spoon == null) {
            try {
                spoon = new SpoonedProject(new File[]{classFile}, classpath, config);
            } catch (Exception e) {
                logger.warn("Unable to spoon the project", e);
                return;
            }
        }
        if (config.isCollectLiterals()) {
            constants = collectLiterals();
        }
        if (config.isCollectStaticMethods()) {
            classes = collectUsedClasses();
        }

        Map<String, String> variableType = collectVariableType();
        this.calledMethods = collectMethod();
        this.variableType = variableType;
        try {
            config.getComplianceLevel();
            StatCollector statCollector = new StatCollector(buggyMethod);
            spoon.processClass(location.getContainingClassName(), statCollector);
            this.statCollector = statCollector;
            VariablesInSuspiciousCollector variablesInSuspiciousCollector = new VariablesInSuspiciousCollector(location);
            spoon.processClass(location.getContainingClassName(), variablesInSuspiciousCollector);
            spoonElementsCollector = new SpoonElementsCollector(variablesInSuspiciousCollector.getVariables(), config);
        } catch (Exception e) {
            logger.warn("Unable to collect used classes", e);
        }
    }

    private void watchBuggyClass() {
        EventRequestManager erm = vm.eventRequestManager();
        ClassPrepareRequest classPrepareRequest = erm.createClassPrepareRequest();
        classPrepareRequest.addClassFilter(location.getContainingClassName());
        classPrepareRequest.setEnabled(true);
    }

    private Candidates collectRuntimeValues(ThreadReference threadRef) {
        if (values.containsKey(currentTestClass + "#" + currentTestMethod)) {
            if (values.get(currentTestClass + "#" + currentTestMethod).size() > config.getMaxLineInvocationPerTest()) {
                return new Candidates();
            }
        }
        DataCollector dataCollect = new DataCollector(threadRef, constants, location, buggyMethod, classes, statCollector, this.variableType, this.calledMethods, config);
        Candidates eexps = dataCollect.collect(TimeUnit.MINUTES.toMillis(7));
        return eexps;
    }

    private Candidates collectLiterals() {
        Candidates candidates = new Candidates();
        try {
            spoon.processClass(location.getContainingClassName(), new DynamothConstantCollector(candidates, buggyMethod, config));
        } catch (Exception e) {
            logger.warn("Unable to collect literals", e);
        }
        return candidates;
    }

    private Set<String> collectUsedClasses() {
        try {
            ClassCollector classCollector = new ClassCollector(buggyMethod);
            spoon.processClass(location.getContainingClassName(), classCollector);
            return classCollector.getClasses();
        } catch (Exception e) {
            logger.warn("Unable to collect used classes", e);
        }
        return new HashSet<>();
    }

    private Set<String> collectMethod() {
        try {
            MethodCollector methodCollector = new MethodCollector();
            spoon.process(methodCollector);

            return methodCollector.getMethods();
        } catch (Exception e) {
            logger.warn("Unable to collect method", e);
        }
        return new HashSet<>();
    }

    private Map<String, String> collectVariableType() {
        try {
            VariableTypeCollector variableTypeCollector = new VariableTypeCollector(buggyMethod, this.location.getLineNumber());
            spoon.processClass(location.getContainingClassName(), variableTypeCollector);
            return variableTypeCollector.getVariableType();
        } catch (Exception e) {
            logger.warn("Unable to collect used classes", e);
        }
        return new HashMap<>();
    }

    private boolean isConstant(Expression e) {
        if (e.getValue().isConstant()) {
            return true;
        }
        for (List<Candidates> candidates : values.values()) {
            loopCandidate : for (Candidates candidate : candidates) {
                for (Expression expression : candidate) {
                    if (e.sameExpression(expression)) {
                        if (expression.getValue().isConstant()) {
                            return true;
                        }
                        if (!e.getValue().equals(expression.getValue())) {
                            return false;
                        }
                        continue loopCandidate;
                    }
                }
                return false;
            }
        }
        return true;
    }

    private Candidates combineValues() {
        final Candidates result = new Candidates();
        List<String> collectedTests = new ArrayList<>(values.keySet());

        Collections.sort(collectedTests, new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {
                if (values.get(t1).isEmpty()) {
                    return -1;
                }
                if (values.get(s).isEmpty()) {
                    return 1;
                }
                return values.get(t1).get(0).size() - values.get(s).get(0).size();
            }
        });
        for (int i = 0; i < collectedTests.size(); i++) {
            final String key = collectedTests.get(i);
            List<Candidates> listValue = values.get(key);
            for (Candidates expressions : listValue) {
                for (Expression expression : expressions) {
                    expression.getValue().setConstant(isConstant(expression));
                }
            }
        }
        long currentTime = System.currentTimeMillis();
        Candidates lastCollectedValues = null;
        for (int k = 0; k < collectedTests.size() && currentTime - startTime <= remainingTime; k++) {
            final String key = collectedTests.get(k);
            List<Candidates> listValue = values.get(key);
            currentTime = System.currentTimeMillis();
            for (int i = 0; i < listValue.size() && currentTime - startTime <= remainingTime; i++) {
                Candidates eexps = listValue.get(i);
                if (eexps == null) {
                    continue;
                }
                if (lastCollectedValues != null && lastCollectedValues.intersection(eexps, false).size() == eexps.size()) {
                    continue;
                }
                lastCollectedValues = eexps;
                if (config.isSortExpressions()) {
                    Collections.sort(eexps, Collections.reverseOrder());
                }
                final Object angelicValue;
                if (i < oracle.get(key).length) {
                    angelicValue = oracle.get(key)[i];
                } else {
                    angelicValue = oracle.get(key)[oracle.get(key).length - 1];
                }
                currentTime = System.currentTimeMillis();
                // check if one of the collected value can be a patch
                for (int j = 0; j < eexps.size() && currentTime - startTime <= remainingTime; j++) {
                    Expression expression = eexps.get(j);
                    if (expression == null || expression.getValue() == null) {
                        continue;
                    }
                    if (angelicValue.equals(expression.getValue().getRealValue()) && checkExpression(key, i, expression)) {
                        result.add(expression);
                        if (config.isOnlyOneSynthesisResult()) {
                            printSummary(result);
                            return result;
                        }
                    }
                }

                DataCombiner combiner = new DataCombiner();
                final int iterationNumber = i;

                combiner.addCombineListener(new DataCombiner.CombineListener() {
                    @Override
                    public boolean check(Expression expression) {
                        if (!angelicValue.equals(expression.getValue().getRealValue())) {
                            return false;
                        }
                        if (checkExpression(key, iterationNumber, expression)) {
                            result.add(expression);
                            return true;
                        }
                        return false;
                    }
                });
                currentTime = System.currentTimeMillis();
                // combine eexps
                long maxCombinerTime = remainingTime - (currentTime - startTime);
                combiner.combine(eexps, angelicValue, maxCombinerTime, config);
                if (result.size() > 0) {
                    if (config.isOnlyOneSynthesisResult()) {
                        printSummary(result);
                        return result;
                    }
                }
                currentTime = System.currentTimeMillis();
            }
            currentTime = System.currentTimeMillis();
        }
        printSummary(result);
        return result;
    }


    private boolean checkExpression(String testName, int iterationNumber, Expression expression) {
        nbExpressionEvaluated++;
        if (checkedExpression.contains(expression.toString())) {
            return false;
        }
        checkedExpression.add(expression.toString());
        for (String test : values.keySet()) {
            List<Candidates> listCandidates = values.get(test);
            for (int i = 0; i < listCandidates.size(); i++) {
                Candidates valueOtherTest = listCandidates.get(i);
                if (test.equals(testName) && iterationNumber == i) {
                    continue;
                }
                Object v;
                if (i < oracle.get(test).length) {
                    v = oracle.get(test)[i];
                } else {
                    v = oracle.get(test)[oracle.get(test).length - 1];
                }
                try {
                    fr.inria.lille.spirals.repair.expression.value.Value expressionValue = expression.evaluate(valueOtherTest);
                    if (expressionValue == null) {
                        return false;
                    }
                    if (!v.equals(expressionValue.getRealValue())) {
                        //logger.debug(expression + " not valid for the test " + test);
                        return false;
                    }
                } catch (RuntimeException e) {
                    return false;
                }
            }
        }
        return true;
    }

    private void printSummary(Candidates result) {
        if (values.values().isEmpty()) return;
        List<Candidates> next = values.values().iterator().next();
        Candidates candidate = next.get(0);
        int nbValueToCombine = candidate.size();
        int nbConstant = 0;
        int nbMethodInvocation = 0;
        int nbFieldAccess = 0;
        int nbVariable = 0;
        for (Expression expression : candidate) {
            if (expression.getValue().isConstant()) {
                nbConstant++;
            } else if (expression instanceof Variable) {
                if (((Variable) expression).getTarget() != null) {
                    nbFieldAccess++;
                } else {
                    nbVariable++;
                }
            } else if (expression instanceof fr.inria.lille.spirals.repair.expression.access.Method) {
                nbMethodInvocation++;
            }
        }

        System.out.println();
        System.out.println();
        System.out.println("========= Info ==========");
        System.out.println("Nb constants             " + nbConstant);
        System.out.println("Nb method invocations    " + nbMethodInvocation);
        System.out.println("Nb field access          " + nbFieldAccess);
        System.out.println("Nb variables             " + nbVariable);
        System.out.println("Total                    " + nbValueToCombine);
        System.out.println("Nb evaluated expressions " + nbExpressionEvaluated);
        System.out.println("Init Execution time      " + (initExecutionTime - startTime) + " ms");
        System.out.println("Collect Execution time   " + (collectExecutionTime - initExecutionTime) + " ms");
        double combinationDuration = System.currentTimeMillis() - collectExecutionTime;
        System.out.println("Combine Execution time   " + combinationDuration + " ms");
        double nbCombinationPerMs = nbExpressionEvaluated/combinationDuration;
        System.out.println("Nb Combination par sec   " + Math.round(nbCombinationPerMs*1000) + " combinations/sec");
        System.out.println("Total Execution time     " + (System.currentTimeMillis() - startTime) + " ms");
        System.out.println("Nb line execution        " + nbBreakPointCalls);

        System.out.println("Nb results               " + result.size());
        System.out.println();
        System.out.println("Results:");
        for (int i = 0; i < result.size(); i++) {
            Expression expression = result.get(i);
            System.out.println((i + 1) + ". " + expression.toString());
        }

        System.out.println();
        System.out.println();

        System.out.println(this.statCollector);

        System.out.println(" & " + nbConstant + " & " + nbMethodInvocation + " & " + nbFieldAccess + " & " + nbVariable + " & " + nbValueToCombine + " & " + nbExpressionEvaluated + " & " + (System.currentTimeMillis() - startTime) + " ms" + " & " + nbBreakPointCalls  + " &");
    }

	@Override
	public Candidates getCollectedExpressions() {
        Iterator<String> it = values.keySet().iterator();
        if (!it.hasNext()) {
            return null;
        }

        Candidates intersection = new Candidates();
        List<Candidates> c = values.get(it.next());
        for (int i = 0; i < c.size(); i++) {
            Candidates candidates = c.get(i);
            intersection.addAll(candidates);
        }
        return intersection;
	}

	@Override
	public Candidates getValidExpressions() {
		return validExpressions;
	}
}
