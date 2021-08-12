package fr.inria.lille.localization;

import fr.inria.lille.localization.metric.Metric;
import fr.inria.lille.localization.metric.Ochiai;
import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.nopol.SourceLocation;
import fr.spoonlabs.flacoco.core.config.FlacocoConfig;
import fr.spoonlabs.flacoco.core.coverage.CoverageMatrix;
import fr.spoonlabs.flacoco.core.coverage.CoverageRunner;
import fr.spoonlabs.flacoco.core.coverage.framework.JUnit4Strategy;
import fr.spoonlabs.flacoco.core.coverage.framework.JUnit5Strategy;
import fr.spoonlabs.flacoco.core.test.TestContext;
import fr.spoonlabs.flacoco.core.test.TestDetector;
import fr.spoonlabs.flacoco.core.test.TestMethod;
import fr.spoonlabs.flacoco.localization.spectrum.SpectrumSuspiciousComputation;
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
		// Because Nopol's usage of fault localization requires more information than the one returned by the API
		// we need to make use of internal APIs
		FlacocoConfig config = FlacocoConfig.getInstance();
		config.setClasspath(Arrays.stream(nopolContext.getProjectClasspath()).map(URL::getPath)
				.reduce((x, y) -> x + File.pathSeparator + y).orElse(""));

		System.out.println(nopolContext);
		// TODO: Fix this
		config.setProjectPath(new File("../test-projects/").getAbsolutePath());
		config.setComplianceLevel(nopolContext.getComplianceLevel());
		config.setTestRunnerVerbose(true);

		// Set tests
		TestDetector testDetector = new TestDetector();
		List<TestContext> tests = testDetector.getTests();

		for (TestContext testContext : tests) {
			if (testContext.getTestFrameworkStrategy() instanceof JUnit4Strategy) {
				config.setjUnit4Tests(
						testContext.getTestMethods().stream()
								.filter(x -> Arrays.asList(nopolContext.getProjectTests()).contains(x.getFullyQualifiedClassName()))
								.map(TestMethod::getFullyQualifiedMethodName)
								.collect(Collectors.toList())
				);
			}
			if (testContext.getTestFrameworkStrategy() instanceof JUnit5Strategy) {
				config.setjUnit5Tests(
						testContext.getTestMethods().stream()
								.filter(x -> Arrays.asList(nopolContext.getProjectTests()).contains(x.getFullyQualifiedClassName()))
								.map(TestMethod::getFullyQualifiedMethodName)
								.collect(Collectors.toList())
				);
			}
		}

		// Get CoverageMatrix
		CoverageRunner coverageRunner = new CoverageRunner();
		CoverageMatrix coverageMatrix = coverageRunner.getCoverageMatrix(new TestDetector().getTests());

		for (String line : coverageMatrix.getResultExecution().keySet()) {
			SourceLocation sourceLocation = new SourceLocation(
					line.split("@-@")[0].replace("/", "."),
					Integer.parseInt(line.split("@-@")[1])
			);
			StatementSourceLocation statementSourceLocation = new StatementSourceLocation(metric, sourceLocation);
			int ef = 0;
			int ep = 0;
			int nf = 0;
			int np = 0;
			for (TestMethod testMethod : coverageMatrix.getTests().keySet()) {
				boolean iTestPassing = coverageMatrix.getTests().get(testMethod);
				boolean nrExecuted = coverageMatrix.getResultExecution().get(line).contains(testMethod);
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
					coverageMatrix.getResultExecution().get(line).stream()
					.map(x -> new TestResultImpl(TestCase.from(x.getFullyQualifiedMethodName()), coverageMatrix.getTests().get(x)))
					.collect(Collectors.toList())
			);
		}

		// Sort statements line in CocoSpoonBasedSpectrumBasedFaultLocalizer
		statementSourceLocations.sort((o1, o2) -> Double.compare(o2.getSuspiciousness(), o1.getSuspiciousness()));
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
