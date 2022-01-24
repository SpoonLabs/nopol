package fr.inria.lille.localization;

import fr.inria.lille.localization.metric.Metric;
import fr.inria.lille.localization.metric.Ochiai;
import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.nopol.SourceLocation;
import fr.spoonlabs.flacoco.api.result.Location;
import fr.spoonlabs.flacoco.core.config.FlacocoConfig;
import fr.spoonlabs.flacoco.core.coverage.CoverageMatrix;
import fr.spoonlabs.flacoco.core.coverage.CoverageRunner;
import fr.spoonlabs.flacoco.core.coverage.framework.JUnit4Strategy;
import fr.spoonlabs.flacoco.core.coverage.framework.JUnit5Strategy;
import fr.spoonlabs.flacoco.core.test.TestContext;
import fr.spoonlabs.flacoco.core.test.TestDetector;
import fr.spoonlabs.flacoco.core.test.method.TestMethod;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtTypeInformation;
import xxl.java.junit.TestCase;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class FlacocoFaultLocalizer implements FaultLocalizer {

    private Map<SourceLocation, List<TestResult>> testListPerStatement = new HashMap<>();

    private List<StatementSourceLocation> statementSourceLocations = new ArrayList<>();

    public FlacocoFaultLocalizer(NopolContext nopolContext, Metric metric) {
        runFlacoco(nopolContext, metric);
    }

    public FlacocoFaultLocalizer(NopolContext nopolContext) {
        this(nopolContext, new Ochiai());
    }

    private void runFlacoco(NopolContext nopolContext, Metric metric) {
        FlacocoConfig config = new FlacocoConfig();

        Launcher spoon = new Launcher();
        List<String> javaSources = new ArrayList<>();
        for (File file : nopolContext.getProjectSources()) {
            spoon.addInputResource(file.getAbsolutePath());
            javaSources.add(file.getAbsolutePath());
        }
        CtModel model = spoon.buildModel();

        List<String> javaBin = new ArrayList<>();

        // Init FlacocoConfig
        config.setClasspath(Arrays.stream(nopolContext.getProjectClasspath()).map(URL::getPath)
                .reduce((x, y) -> x + File.pathSeparator + y).orElse(""));
        config.setJacocoIncludes(
                model.getAllTypes().stream().map(CtTypeInformation::getQualifiedName).collect(Collectors.toSet()));
        config.setComplianceLevel(nopolContext.getComplianceLevel());
        config.setTestRunnerJVMArgs("-Xms2048m -Xmx2048m");
        config.setSrcJavaDir(javaSources);
        config.setTestRunnerVerbose(true);
        System.out.println(nopolContext);

        // Set tests
        TestDetector testDetector = new TestDetector(config);
        List<TestContext> tests = testDetector.getTests();

        for (TestContext testContext : tests) {
            if (testContext.getTestFrameworkStrategy() instanceof JUnit4Strategy) {
                config.setjUnit4Tests(
                        testContext.getTestMethods().stream()
                                .filter(x -> Arrays.asList(nopolContext.getProjectTests())
                                        .contains(x.getFullyQualifiedClassName()))
                                .map(TestMethod::getFullyQualifiedMethodName)
                                .collect(Collectors.toSet())
                );
            }
            if (testContext.getTestFrameworkStrategy() instanceof JUnit5Strategy) {
                config.setjUnit5Tests(
                        testContext.getTestMethods().stream()
                                .filter(x -> Arrays.asList(nopolContext.getProjectTests())
                                        .contains(x.getFullyQualifiedClassName()))
                                .map(TestMethod::getFullyQualifiedMethodName)
                                .collect(Collectors.toSet())
                );
            }
        }

        // Get CoverageMatrix
        CoverageRunner coverageRunner = new CoverageRunner(config);
        CoverageMatrix coverageMatrix = coverageRunner.getCoverageMatrix(new TestDetector(config).getTests());

        for (Location location : coverageMatrix.getResultExecution().keySet()) {
            SourceLocation sourceLocation = new SourceLocation(
                    location.getClassName(),
                    location.getLineNumber()
            );
            StatementSourceLocation statementSourceLocation = new StatementSourceLocation(metric, sourceLocation);
            int ef = 0;
            int ep = 0;
            int nf = 0;
            int np = 0;
            for (TestMethod testMethod : coverageMatrix.getTests().keySet()) {
                boolean iTestPassing = coverageMatrix.getTests().get(testMethod);
                boolean nrExecuted = coverageMatrix.getResultExecution().get(location).contains(testMethod);
                if (iTestPassing && nrExecuted) {
                    ep++;
                } else if (!iTestPassing && nrExecuted) {
                    ef++;
                } else if (iTestPassing && !nrExecuted) {
                    np++;
                } else if (!iTestPassing && !nrExecuted) {
                    nf++;
                }
            }
            statementSourceLocation.setEp(ep);
            statementSourceLocation.setEf(ef);
            statementSourceLocation.setNf(nf);
            statementSourceLocation.setNp(np);

            statementSourceLocations.add(statementSourceLocation);
            testListPerStatement.put(
                    sourceLocation,
                    coverageMatrix.getResultExecution().get(location).stream()
                            .map(x -> new TestResultImpl(TestCase.from(x.getFullyQualifiedMethodName()), coverageMatrix.getTests().get(x)))
                            .collect(Collectors.toList())
            );
        }

        statementSourceLocations.sort(Comparator.comparing(x -> x.getLocation().getContainingClassName()));
        statementSourceLocations.sort((o1, o2) -> Integer.compare(o2.getLocation().getLineNumber(), o1.getLocation().getLineNumber()));
        statementSourceLocations.sort((o1, o2) -> Double.compare(o2.getSuspiciousness(), o1.getSuspiciousness()));

        LinkedHashMap<SourceLocation, List<fr.inria.lille.localization.TestResult>> map = new LinkedHashMap<>();
        for (StatementSourceLocation source : statementSourceLocations) {
            map.put(source.getLocation(), testListPerStatement.get(source.getLocation()));
        }
        testListPerStatement = map;
    }

    @Override
    public Map<SourceLocation, List<TestResult>> getTestListPerStatement() {
        return testListPerStatement;
    }

    @Override
    public List<? extends StatementSourceLocation> getStatements() {
        return statementSourceLocations;
    }

}
