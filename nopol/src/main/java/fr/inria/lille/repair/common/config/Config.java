package fr.inria.lille.repair.common.config;

import fr.inria.lille.localization.DumbFaultLocalizerImpl;
import fr.inria.lille.localization.FaultLocalizer;
import fr.inria.lille.localization.GZoltarFaultLocalizer;
import fr.inria.lille.localization.OchiaiFaultLocalizer;
import fr.inria.lille.repair.common.synth.StatementType;
import xxl.java.library.FileLibrary;
import xxl.java.library.JavaLibrary;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.Arrays;
import java.util.Properties;

/**
 * Created by Thomas Durieux on 23/03/15.
 */
public class Config implements Serializable {

	private static final long serialVersionUID = -2542128741040978263L;


	public enum NopolMode {
		REPAIR,
		RANKING
	}

	public enum NopolOracle {
		ANGELIC,
		SYMBOLIC
	}

	public enum NopolSynthesis {
		SMT,
		DYNAMOTH
	}

	public enum NopolSolver {
		Z3,
		CVC4
	}

	public enum NopolLocalizer {
		DUMB,
		GZOLTAR,
		OCHIAI
	}

	private final String filename = "config.ini";
	private final Properties p;

	private int synthesisDepth;
	private boolean collectStaticMethods;
	private boolean collectStaticFields;
	private boolean collectLiterals;
	private boolean collectOnlyUsedMethod;
	private boolean onlyOneSynthesisResult;
	private boolean sortExpressions;
	private int maxLineInvocationPerTest;
	private int timeoutMethodInvocation;
	private int maxTimeInMinutes = 10;

	private double addWeight;
	private double subWeight;
	private double mulWeight;
	private double divWeight;
	private double andWeight;
	private double orWeight;
	private double eqWeight;
	private double nEqWeight;
	private double lessEqWeight;
	private double lessWeight;
	private double methodCallWeight;
	private double fieldAccessWeight;
	private double constantWeight;
	private double variableWeight;
	private long timeoutTestExecution;
	private long maxTimeBuildPatch;
	private long maxTimeEachTypeOfFixInMinutes;

	private NopolMode mode = NopolMode.REPAIR;
	private StatementType type = StatementType.CONDITIONAL;
	private NopolSynthesis synthesis = NopolSynthesis.SMT;
	private NopolOracle oracle = NopolOracle.ANGELIC;
	private NopolSolver solver = NopolSolver.Z3;
	private NopolLocalizer localizer = NopolLocalizer.OCHIAI;
	private String solverPath;
	private String[] projectSourcePath;
	private String projectClasspath;
	private String[] projectTests;

	private int complianceLevel;

	public Config() {
		p = new Properties();
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			p.load(classLoader.getResourceAsStream(filename));

			synthesisDepth = Integer.parseInt(p.getProperty("depth", "3"));
			collectStaticMethods = Boolean.parseBoolean(p.getProperty("collectStaticMethod", "false"));
			collectStaticFields = Boolean.parseBoolean(p.getProperty("collectStaticFields", "false"));
			collectLiterals = Boolean.parseBoolean(p.getProperty("collectLiteral", "false"));
			collectOnlyUsedMethod = Boolean.parseBoolean(p.getProperty("collectOnlyUsedMethod", "true"));
			onlyOneSynthesisResult = Boolean.parseBoolean(p.getProperty("onlyOneSynthesisResult", "true"));
			sortExpressions = Boolean.parseBoolean(p.getProperty("sortExpression", "true"));
			maxLineInvocationPerTest = Integer.parseInt(p.getProperty("maxLineInvocationPerTest", "150"));
			timeoutMethodInvocation = Integer.parseInt(p.getProperty("timeoutMethodInvocation", "2000"));

			addWeight = Double.parseDouble(p.getProperty("addOp", "0"));
			subWeight = Double.parseDouble(p.getProperty("subOp", "0"));
			mulWeight = Double.parseDouble(p.getProperty("mulOp", "0"));
			divWeight = Double.parseDouble(p.getProperty("divOp", "0"));
			andWeight = Double.parseDouble(p.getProperty("andOp", "0"));
			orWeight = Double.parseDouble(p.getProperty("orOp", "0"));
			eqWeight = Double.parseDouble(p.getProperty("eqOp", "0"));
			nEqWeight = Double.parseDouble(p.getProperty("neqOpOp", "0"));
			lessEqWeight = Double.parseDouble(p.getProperty("lessEqOp", "0"));
			lessWeight = Double.parseDouble(p.getProperty("lessOp", "0"));
			methodCallWeight = Double.parseDouble(p.getProperty("methodCall", "0"));
			fieldAccessWeight = Double.parseDouble(p.getProperty("fieldAccess", "0"));
			constantWeight = Double.parseDouble(p.getProperty("constant", "0"));
			variableWeight = Double.parseDouble(p.getProperty("variable", "0"));
			timeoutTestExecution = Long.parseLong(p.getProperty("timeoutTestExecution", "5"));
			maxTimeBuildPatch = Long.parseLong(p.getProperty("maxTimeBuildPatch", "15L"));
			maxTimeEachTypeOfFixInMinutes = Long.parseLong(p.getProperty("maxTimeEachTypeOfFixInMinutes", "5"));
			complianceLevel = Integer.parseInt(p.getProperty("complianceLevel", "7"));
		} catch (IOException e) {
			throw new RuntimeException("Unable to load config file", e);
		}
	}

	public URL[] buildClasspath() {
		return JavaLibrary.classpathFrom(this.getProjectClasspath());
	}

	public File[] buildSourceFiles() {
		File[] sourceFiles = new File[this.getProjectSourcePath().length];
		for (int i = 0; i < this.getProjectSourcePath().length; i++) {
			String path = this.getProjectSourcePath()[i];
			File sourceFile = FileLibrary.openFrom(path);
			sourceFiles[i] = sourceFile;
		}
		return sourceFiles;
	}

	public long getMaxTimeEachTypeOfFixInMinutes() {
		return maxTimeEachTypeOfFixInMinutes;
	}

	public void setMaxTimeEachTypeOfFixInMinutes(long maxTimeEachTypeOfFixInMinutes) {
		this.maxTimeEachTypeOfFixInMinutes = maxTimeEachTypeOfFixInMinutes;
	}

	public int getSynthesisDepth() {
		return synthesisDepth;
	}

	public void setSynthesisDepth(int synthesisDepth) {
		this.synthesisDepth = synthesisDepth;
	}

	public boolean isCollectStaticMethods() {
		return collectStaticMethods;
	}

	public void setCollectStaticMethods(boolean collectStaticMethods) {
		this.collectStaticMethods = collectStaticMethods;
	}

	public boolean isCollectStaticFields() {
		return collectStaticFields;
	}

	public void setCollectStaticFields(boolean collectStaticFields) {
		this.collectStaticFields = collectStaticFields;
	}

	public boolean isCollectLiterals() {
		return collectLiterals;
	}

	public void setCollectLiterals(boolean collectLiterals) {
		this.collectLiterals = collectLiterals;
	}

	public boolean isCollectOnlyUsedMethod() {
		return collectOnlyUsedMethod;
	}

	public void setCollectOnlyUsedMethod(boolean collectOnlyUsedMethod) {
		this.collectOnlyUsedMethod = collectOnlyUsedMethod;
	}

	public boolean isOnlyOneSynthesisResult() {
		return onlyOneSynthesisResult;
	}

	public void setOnlyOneSynthesisResult(boolean onlyOneSynthesisResult) {
		this.onlyOneSynthesisResult = onlyOneSynthesisResult;
	}

	public boolean isSortExpressions() {
		return sortExpressions;
	}

	public void setSortExpressions(boolean sortExpressions) {
		this.sortExpressions = sortExpressions;
	}

	public int getMaxLineInvocationPerTest() {
		return maxLineInvocationPerTest;
	}

	public void setMaxLineInvocationPerTest(int maxLineInvocationPerTest) {
		this.maxLineInvocationPerTest = maxLineInvocationPerTest;
	}

	public int getTimeoutMethodInvocation() {
		return timeoutMethodInvocation;
	}

	public void setTimeoutMethodInvocation(int timeoutMethodInvocation) {
		this.timeoutMethodInvocation = timeoutMethodInvocation;
	}

	public double getAddWeight() {
		return addWeight;
	}

	public void setAddWeight(double addWeight) {
		this.addWeight = addWeight;
	}

	public double getSubWeight() {
		return subWeight;
	}

	public void setSubWeight(double subWeight) {
		this.subWeight = subWeight;
	}

	public double getMulWeight() {
		return mulWeight;
	}

	public void setMulWeight(double mulWeight) {
		this.mulWeight = mulWeight;
	}

	public double getDivWeight() {
		return divWeight;
	}

	public void setDivWeight(double divWeight) {
		this.divWeight = divWeight;
	}

	public double getAndWeight() {
		return andWeight;
	}

	public void setAndWeight(double andWeight) {
		this.andWeight = andWeight;
	}

	public double getOrWeight() {
		return orWeight;
	}

	public void setOrWeight(double orWeight) {
		this.orWeight = orWeight;
	}

	public double getEqWeight() {
		return eqWeight;
	}

	public void setEqWeight(double eqWeight) {
		this.eqWeight = eqWeight;
	}

	public double getnEqWeight() {
		return nEqWeight;
	}

	public void setnEqWeight(double nEqWeight) {
		this.nEqWeight = nEqWeight;
	}

	public double getLessEqWeight() {
		return lessEqWeight;
	}

	public void setLessEqWeight(double lessEqWeight) {
		this.lessEqWeight = lessEqWeight;
	}

	public double getLessWeight() {
		return lessWeight;
	}

	public void setLessWeight(double lessWeight) {
		this.lessWeight = lessWeight;
	}

	public double getMethodCallWeight() {
		return methodCallWeight;
	}

	public void setMethodCallWeight(double methodCallWeight) {
		this.methodCallWeight = methodCallWeight;
	}

	public double getFieldAccessWeight() {
		return fieldAccessWeight;
	}

	public void setFieldAccessWeight(double fieldAccessWeight) {
		this.fieldAccessWeight = fieldAccessWeight;
	}

	public double getConstantWeight() {
		return constantWeight;
	}

	public void setConstantWeight(double constantWeight) {
		this.constantWeight = constantWeight;
	}

	public double getVariableWeight() {
		return variableWeight;
	}

	public void setVariableWeight(double variableWeight) {
		this.variableWeight = variableWeight;
	}

	public NopolMode getMode() {
		return mode;
	}

	public void setMode(NopolMode mode) {
		this.mode = mode;
	}

	public StatementType getType() {
		return type;
	}

	public void setType(StatementType type) {
		this.type = type;
	}

	public NopolSynthesis getSynthesis() {
		return synthesis;
	}

	public void setSynthesis(NopolSynthesis synthesis) {
		this.synthesis = synthesis;
	}

	public NopolOracle getOracle() {
		return oracle;
	}

	public void setOracle(NopolOracle oracle) {
		this.oracle = oracle;
	}

	public NopolSolver getSolver() {
		return solver;
	}

	public void setSolver(NopolSolver solver) {
		this.solver = solver;
	}

	public String getSolverPath() {
		return solverPath;
	}

	public void setSolverPath(String solverPath) {
		this.solverPath = solverPath;
	}

	public String[] getProjectSourcePath() {
		return projectSourcePath;
	}

	public void setProjectSourcePath(String[] projectSourcePath) {
		this.projectSourcePath = projectSourcePath;
	}

	public String getProjectClasspath() {
		return projectClasspath;
	}

	public void setProjectClasspath(String projectClasspath) {
		this.projectClasspath = projectClasspath;
	}

	public String[] getProjectTests() {
		return projectTests;
	}

	public void setProjectTests(String[] projectTests) {
		this.projectTests = projectTests;
	}

	public int getComplianceLevel() {
		return complianceLevel;
	}

	public void setComplianceLevel(int complianceLevel) {
		this.complianceLevel = complianceLevel;
	}

	public int getMaxTimeInMinutes() {
		return maxTimeInMinutes;
	}

	public void setMaxTimeInMinutes(int maxTimeInMinutes) {
		this.maxTimeInMinutes = maxTimeInMinutes;
	}


	public long getTimeoutTestExecution() {
		return timeoutTestExecution;
	}

	public void setTimeoutTestExecution(long timeoutTestExecution) {
		this.timeoutTestExecution = timeoutTestExecution;
	}

	public long getMaxTimeBuildPatch() {
		return maxTimeBuildPatch;
	}

	public void setMaxTimeBuildPatch(long maxTimeBuildPatch) {
		this.maxTimeBuildPatch = maxTimeBuildPatch;
	}

	public FaultLocalizer getLocalizer(File[] sourceFiles, URL[] classpath, String[] testClasses) {
		switch (this.localizer) {
			case GZOLTAR:
				try {
					return new GZoltarFaultLocalizer(classpath, testClasses);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			case DUMB:
				return new DumbFaultLocalizerImpl(sourceFiles, classpath, testClasses, this);
			case OCHIAI:
			default:
				return new OchiaiFaultLocalizer(sourceFiles, classpath, testClasses, this);
		}
	}

	public void setLocalizer(NopolLocalizer localizer) {
		this.localizer = localizer;
	}


	@Override
	public String toString() {
		return "Config{" +
				"synthesisDepth=" + synthesisDepth +
				", collectStaticMethods=" + collectStaticMethods +
				", collectStaticFields=" + collectStaticFields +
				", collectLiterals=" + collectLiterals +
				", onlyOneSynthesisResult=" + onlyOneSynthesisResult +
				", sortExpressions=" + sortExpressions +
				", maxLineInvocationPerTest=" + maxLineInvocationPerTest +
				", timeoutMethodInvocation=" + timeoutMethodInvocation +
				", addWeight=" + addWeight +
				", subWeight=" + subWeight +
				", mulWeight=" + mulWeight +
				", divWeight=" + divWeight +
				", andWeight=" + andWeight +
				", orWeight=" + orWeight +
				", eqWeight=" + eqWeight +
				", nEqWeight=" + nEqWeight +
				", lessEqWeight=" + lessEqWeight +
				", lessWeight=" + lessWeight +
				", methodCallWeight=" + methodCallWeight +
				", fieldAccessWeight=" + fieldAccessWeight +
				", constantWeight=" + constantWeight +
				", variableWeight=" + variableWeight +
				", mode=" + mode +
				", type=" + type +
				", synthesis=" + synthesis +
				", oracle=" + oracle +
				", solver=" + solver +
				", solverPath='" + solverPath + '\'' +
				", projectSourcePath=" + Arrays.toString(projectSourcePath) +
				", projectClasspath='" + projectClasspath + '\'' +
				", projectTests=" + Arrays.toString(projectTests) +
				", complianceLevel=" + complianceLevel +
				'}';
	}
}
