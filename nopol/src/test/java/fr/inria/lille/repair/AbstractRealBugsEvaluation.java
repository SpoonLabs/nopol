package fr.inria.lille.repair;

import fr.inria.lille.commons.synthesis.smt.solver.SolverFactory;
import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.common.patch.Patch;
import fr.inria.lille.repair.common.synth.StatementType;
import fr.inria.lille.repair.nopol.NoPol;
import org.junit.Ignore;
import org.junit.Test;
import xxl.java.library.FileLibrary;
import xxl.java.library.JavaLibrary;

import java.io.File;
import java.util.List;

import static fr.inria.lille.commons.synthesis.smt.solver.Z3SolverFactory.isMac;
import static org.junit.Assert.assertEquals;

public abstract class AbstractRealBugsEvaluation {

    // git clone https://github.com/SpoonLabs/nopol-experiments
    private String realBugPath = "../nopol-experiments/dataset/";
    private String executionType;

    public AbstractRealBugsEvaluation(String executionType) {
        this.executionType = executionType;
    }

    @Test
    public void CM1() {
        NopolContext config = new NopolContext();
        StatementType statementType = StatementType.CONDITIONAL;
        String projectName = "cm1";
        List<Patch> patches = setupAndRun(
                projectName,
                statementType,
                new String[] {},
                config,
                "commons-beanutils-1.7.0.jar",
                "commons-collections-2.0.jar",
                "commons-discovery-0.4.jar",
                "commons-lang-2.1.jar",
                "commons-logging-1.1.1.jar",
                "junit-3.8.jar");

        assertEquals(patches.toString(), 1, patches.size());
        Patch patch = patches.get(0);
        assertEquals(patch.getType(), statementType);
        System.out.println(String.format("Patch for real bug %s: %s",
                projectName, patch.asString()));

        TestUtility.assertAgainstKnownPatches(patch, "(intPos)==(sorted.length), (fpos)==(n)",
                "(dif)==(begin)","(dif)==(0)", "0 == difis", "(intPos)==(3)", "n == pos", "sorted.length <= pos", "sorted.length <= intPos", "sorted.length <= fpos", "length <= fpos", "n <= intPos");

        String rootFolder = realBugPath + projectName + "/";
        String srcFolder = rootFolder + "src";
        clean(srcFolder); // TODO
    }

    @Test
    public void CM2() {
        StatementType statementType = StatementType.PRECONDITION;
        String projectName = "cm2";
        NopolContext config = new NopolContext();

        List<Patch> patches = setupAndRun(
                projectName,
                statementType,
                new String[] {"org.apache.commons.math.util.MathUtilsTest"},
                config,
                "commons-beanutils-1.6.1.jar",
                "commons-collections-3.0.jar",
                "commons-discovery-0.2.jar",
                "commons-lang-2.0.jar",
                "commons-logging-1.0.3.jar",
                "junit-3.8.jar");

        assertEquals(patches.toString(), 1, patches.size());
        Patch patch = patches.get(0);
        assertEquals(patch.getType(), statementType);
        System.out.println(String.format("Patch for real bug %s: %s",
                projectName, patch.asString()));

        TestUtility.assertAgainstKnownPatches(patch, "(k)==(org.apache.commons.math.util.MathUtils.NB)",
                "(org.apache.commons.math.util.MathUtils.NB)==(k)",
                "k <= org.apache.commons.math.util.MathUtils.NB",
                "n < 0",
                "(n) != (org.apache.commons.math.util.MathUtils.ZS)");

        String rootFolder = realBugPath + projectName + "/";
        String srcFolder = rootFolder + "src";
        clean(srcFolder); // TODO
    }

    @Test
    public void CM3() {
        String projectName = "cm3";
        StatementType statementType = StatementType.PRECONDITION;
        NopolContext config = new NopolContext();
        List<Patch> patches = setupAndRun(
                projectName,
                statementType,
                new String[] {},
                config,
                "commons-discovery-0.2.jar",
                "commons-logging-1.0.3.jar",
                "junit-3.8.jar");

        assertEquals(patches.toString(), 1, patches.size());
        Patch patch = patches.get(0);
        assertEquals(patch.getType(), statementType);
        System.out.println(String.format("Patch for real bug %s: %s",
                projectName, patch.asString()));

        TestUtility.assertAgainstKnownPatches(patch, "(n)==(org.apache.commons.math.util.MathUtils.NS)",
                "(n)<=(-1)",
                "n < 0",
                "(org.apache.commons.math.util.MathUtils.ZS) != (n)");

        String rootFolder = realBugPath + projectName + "/";
        String srcFolder = rootFolder + "src";
        clean(srcFolder); // TODO
    }

    @Test
    public void CM4() {
        String projectName = "cm4";
        StatementType statementType = StatementType.CONDITIONAL;
        NopolContext config = new NopolContext();
        List<Patch> patches = setupAndRun(
                projectName,
                statementType,
                new String[] {},
                config,
                "commons-discovery-0.4.jar",
                "commons-logging-1.0.3.jar",
                "junit-3.8.jar");

        assertEquals(patches.toString(), 1, patches.size());
        Patch patch = patches.get(0);
        assertEquals(patch.getType(), statementType);
        System.out.println(String.format("Patch for real bug %s: %s",
                projectName, patch.asString()));

        TestUtility.assertAgainstKnownPatches(patch, "((!((5)<=(org.apache.commons.math.analysis.PolynomialSplineFunction.this.knots.length)))&&((2)<(v)))||((v < (knots[0])))",
                "((v < (knots[0]))) || ((knots[n]) < v)",
                "((v < (knots[0]))) || (((org.apache.commons.math.analysis.PolynomialSplineFunction.this.n + org.apache.commons.math.analysis.PolynomialSplineFunction.this.polynomials.length) - (v) < org.apache.commons.math.analysis.PolynomialSplineFunction.this.knots.length) && (!(org.apache.commons.math.analysis.PolynomialSplineFunction.this.polynomials.length <= v)))");

        String rootFolder = realBugPath + projectName + "/";
        String srcFolder = rootFolder + "src";
        clean(srcFolder); // TODO
    }

    @Test
    public void CM5() {
        String projectName = "cm5";
        StatementType statementType = StatementType.CONDITIONAL;
        NopolContext config = new NopolContext();
        List<Patch> patches = setupAndRun(
                projectName,
                statementType,
                new String[] {"org.apache.commons.math.util.MathUtilsTest"},
                config,
                "commons-beanutils-1.7.0.jar",
                "commons-collections-3.0.jar",
                "commons-discovery-0.4.jar",
                "commons-lang-2.1.jar",
                "commons-logging-1.1.1.jar",
                "junit-4.10.jar");

        assertEquals(patches.toString(), 1, patches.size());
        Patch patch = patches.get(0);
        assertEquals(patch.getType(), statementType);
        System.out.println(String.format("Patch for real bug %s: %s",
                projectName, patch.asString()));


        TestUtility.assertAgainstKnownPatches(patch, "((org.apache.commons.math.util.MathUtils.ZS)==(v))||((!((0)<(u)))&&((u * v) == 0))",
                "((u * v) == 0)&&((!((0)!=(u)))||((v)<(org.apache.commons.math.util.MathUtils.PS)))",
                "(0 == u) || (0 == v)",
                "(0 == v) || (!((u) != (org.apache.commons.math.util.MathUtils.ZB)))",
                "!(((org.apache.commons.math.util.MathUtils.EPSILON <= u) || (u < org.apache.commons.math.util.MathUtils.NB)) && ((v) != (org.apache.commons.math.util.MathUtils.ZB)))",
                "((!((org.apache.commons.math.util.MathUtils.ZB) != (v))) || (org.apache.commons.math.util.MathUtils.ZB == u)) && ((u * v) == 0)");

        String rootFolder = realBugPath + projectName + "/";
        String srcFolder = rootFolder + "src";
        clean(srcFolder); // TODO
    }

    @Test
    public void CM6() {
        NopolContext config = new NopolContext();
        config.setComplianceLevel(5);
        String projectName = "cm6";
        StatementType statementType = StatementType.PRECONDITION;
        List<Patch> patches = setupAndRun(
                projectName,
                statementType,
                new String[] {},
                config,
                "junit-4.10.jar");

        assertEquals(patches.toString(), 1, patches.size());
        Patch patch = patches.get(0);
        assertEquals(patch.getType(), statementType);
        System.out.println(String.format("Patch for real bug %s: %s",
                projectName, patch.asString()));

        TestUtility.assertAgainstKnownPatches(patch, "(ns)!=(n)",
                "(0)<=(fa)",
                "upperBound == 0.0",
                "-1 == b");

        String rootFolder = realBugPath + projectName + "/";
        String srcFolder = rootFolder + "src";
        clean(srcFolder); // TODO
    }

    @Test
    public void CM7() {
        String projectName = "cm7";
        StatementType statementType = StatementType.CONDITIONAL;
        NopolContext config = new NopolContext();
        List<Patch> patches = setupAndRun(
                projectName,
                statementType,
                new String[] {},
                config,
                "junit-4.10.jar");

        assertEquals(patches.toString(), 1, patches.size());
        Patch patch = patches.get(0);
        assertEquals(patch.getType(), statementType);
        System.out.println(String.format("Patch for real bug %s: %s",
                projectName, patch.asString()));

        TestUtility.assertAgainstKnownPatches(patch, "(mean)<(1)", "mean <= 0", "mean <= 0.0");
        // Execution time 10min 40sec break point in a multiples 100 000 iterations slowdown the execution

        String rootFolder = realBugPath + projectName + "/";
        String srcFolder = rootFolder + "src";
        clean(srcFolder); // TODO
    }

    @Test
    @Ignore
    public void CM8() {
        NopolContext config = new NopolContext();
        StatementType statementType = StatementType.CONDITIONAL;
        String projectName = "cm8";
        List<Patch> patches = setupAndRun(
                projectName,
                statementType,
                new String[] {"org.apache.commons.math3.fraction.FractionTest"},
                config,
                "junit-4.10.jar");

        assertEquals(patches.toString(), 1, patches.size());
        Patch patch = patches.get(0);
        assertEquals(patch.getType(), statementType);
        System.out.println(String.format("Patch for real bug %s: %s",
                projectName, patch.asString()));

        TestUtility.assertAgainstKnownPatches(patch, "(ns)!=(n)",
                "((org.apache.commons.math3.fraction.Fraction)((org.apache.commons.math3.fraction.Fraction)this).getReducedFraction(maxDenominator, maxIterations)).percentageValue() < maxDenominator");

        String rootFolder = realBugPath + projectName + "/";
        String srcFolder = rootFolder + "src";
        clean(srcFolder); // TODO
    }

    @Test
    @Ignore
    public void CM9() {
        String projectName = "cm9";
        StatementType statementType = StatementType.CONDITIONAL;
        NopolContext config = new NopolContext();
        List<Patch> patches = setupAndRun(
                projectName,
                statementType,
                new String[]{
                        //"org.apache.commons.math3.util.FastMathTest"
                },
                config,
                "junit-4.10.jar");

        assertEquals(patches.toString(), 1, patches.size());
        Patch patch = patches.get(0);
        assertEquals(patch.getType(), statementType);
        System.out.println(String.format("Patch for real bug %s: %s",
                projectName, patch.asString()));

        TestUtility.assertAgainstKnownPatches(patch, "(ns)!=(n)");

        String rootFolder = realBugPath + projectName + "/";
        String srcFolder = rootFolder + "src";
        clean(srcFolder); // TODO
    }

    @Test
    public void CM10() {
        String projectName = "cm10";
        StatementType statementType = StatementType.PRECONDITION;
        NopolContext config = new NopolContext();
        List<Patch> patches = setupAndRun(
                projectName,
                statementType,
                new String[]{"org.apache.commons.math3.stat.correlation.CovarianceTest"},
                config,
                "junit-4.10.jar");

        assertEquals(patches.toString(), 1, patches.size());
        Patch patch = patches.get(0);
        assertEquals(patch.getType(), statementType);
        System.out.println(String.format("Patch for real bug %s: %s",
                projectName, patch.asString()));

        TestUtility.assertAgainstKnownPatches(patch, "(nRows < 2)", "nRows < 2", "((org.apache.commons.math3.linear.AbstractRealMatrix)matrix).isSquare()", "(1) != (nCols)");

        String rootFolder = realBugPath + projectName + "/";
        String srcFolder = rootFolder + "src";
        clean(srcFolder); // TODO
    }


    @Test
    public void CL1() {
        NopolContext config = new NopolContext();
        config.setComplianceLevel(4);
        String projectName = "cl1";
        StatementType statementType = StatementType.CONDITIONAL;
        config.setSolver(NopolContext.NopolSolver.Z3);
        config.setSolverPath("lib/z3/z3_for_linux");

        List<Patch> patches = setupAndRun(
                projectName,
                statementType,
                new String[]{"org.apache.commons.lang.StringUtilsTest" },
                config,
                "junit-3.8.jar");

        assertEquals(patches.toString(), 1, patches.size());
        Patch patch = patches.get(0);
        assertEquals(patch.getType(), statementType);
        System.out.println(String.format("Patch for real bug %s: %s",
                projectName, patch.asString()));

        TestUtility.assertAgainstKnownPatches(patch,
                "(text.length())==(3)",
                "(text.length())==(with.length())",
                "(null == with) || (0 != ((java.lang.String)with).length())",
                "(with.length()) != (0)");

        String rootFolder = realBugPath + projectName + "/";
        String srcFolder = rootFolder + "src";
        clean(srcFolder); // TODO
    }

    @Test
    public void CL2() {
        NopolContext config = new NopolContext();
        config.setComplianceLevel(4);
        String projectName = "cl2";
        StatementType statementType = StatementType.CONDITIONAL;
        List<Patch> patches = setupAndRun(
                projectName,
                statementType,
                new String[]{"org.apache.commons.lang.StringUtilsTest" },
                config,
                "junit-3.8.jar");

        assertEquals(patches.toString(), 1, patches.size());
        Patch patch = patches.get(0);
        assertEquals(patch.getType(), statementType);
        System.out.println(String.format("Patch for real bug %s: %s",
                projectName, patch.asString()));

        TestUtility.assertAgainstKnownPatches(patch,
                "(lastIdx)<(org.apache.commons.lang.StringUtils.blanks.length())",
                "lastIdx < org.apache.commons.lang.StringUtils.blanks.length()",
                "(lastIdx)<=(0)",
                "lastIdx <= 0",
                "(lastIdx)<(1)",
                "str.length() <= 1");

        String rootFolder = realBugPath + projectName + "/";
        String srcFolder = rootFolder + "src";
        clean(srcFolder); // TODO
    }

    @Test
    public void CL3() {
        NopolContext config = new NopolContext();
        config.setComplianceLevel(4);
        String projectName = "cl3";
        StatementType statementType = StatementType.CONDITIONAL;
        List<Patch> patches = setupAndRun(
                projectName,
                statementType,
                new String[]{"org.apache.commons.lang.StringUtilsSubstringTest" },
                config,
                "junit-3.8.jar");

        assertEquals(patches.toString(), 1, patches.size());
        Patch patch = patches.get(0);
        assertEquals(patch.getType(), statementType);
        System.out.println(String.format("Patch for real bug %s: %s",
                projectName, patch.asString()));

        TestUtility.assertAgainstKnownPatches(patch, "(!(0 <= len)) || (str.length() < pos)",
                "((len)<=(-1))||((str.length())<(pos))",
                "(len < 0) || (((java.lang.String)str).length() < pos)",
                "(((java.lang.String)str).length() < pos) || (len < 0)",
                "(len <= -1) || (str.length() < pos)");

        String rootFolder = realBugPath + projectName + "/";
        String srcFolder = rootFolder + "src";
        clean(srcFolder); // TODO
    }

    @Test
    public void CL4() {
        NopolContext config = new NopolContext();
        config.setComplianceLevel(4);
        String projectName = "cl4";
        StatementType statementType = StatementType.CONDITIONAL;
        List<Patch> patches = setupAndRun(
                projectName,
                statementType,
                new String[]{"org.apache.commons.lang.text.StrBuilderTest" },
                config,
                "junit-3.8.jar");

        assertEquals(patches.toString(), 1, patches.size());
        Patch patch = patches.get(0);
        assertEquals(patch.getType(), statementType);
        System.out.println(String.format("Patch for real bug %s: %s",
                projectName, patch.asString()));

        TestUtility.assertAgainstKnownPatches(
                patch,
                "((!(str!=null))||(startIndex >= (size)))&&((!(str!=null))||(startIndex >= (size)))",
                "(!(str!=null))||(startIndex >= (size))",
                "(startIndex >= (size)) || (!(str!=null))",
                "((startIndex >= (size)) || (!(str!=null))) && ((org.apache.commons.lang.text.StrBuilder.this.size) != (-1))");
        String rootFolder = realBugPath + projectName + "/";
        String srcFolder = rootFolder + "src";
        clean(srcFolder); // TODO
    }

    @Test
    public void CL5() {
        NopolContext config = new NopolContext();
        config.setComplianceLevel(6);
        String projectName = "cl5";
        StatementType statementType = StatementType.CONDITIONAL;
        List<Patch> patches = setupAndRun(
                projectName,
                statementType,
                new String[]{},
                config,
                "junit-3.8.jar");

        assertEquals(patches.toString(), 1, patches.size());
        Patch patch = patches.get(0);
        assertEquals(patch.getType(), statementType);
        System.out.println(String.format("Patch for real bug %s: %s",
                projectName, patch.asString()));

        TestUtility.assertAgainstKnownPatches(patch,
                "className.length()==0",
                "0 == ((java.lang.String)className).length()");

        String rootFolder = realBugPath + projectName + "/";
        String srcFolder = rootFolder + "src";
        clean(srcFolder); // TODO
    }

    @Test
    @Ignore
    public void CL6() {
        String projectName = "cl6";
        StatementType statementType = StatementType.CONDITIONAL;
        NopolContext config = new NopolContext();
        List<Patch> patches = setupAndRun(
                projectName,
                statementType,
                new String[]{},
                config,
                "easymock-2.5.2.jar",
                "junit-4.10.jar");

        assertEquals(patches.toString(), 1, patches.size());
        Patch patch = patches.get(0);
        assertEquals(patch.getType(), statementType);
        System.out.println(String.format("Patch for real bug %s: %s",
                projectName, patch.asString()));

        TestUtility.assertAgainstKnownPatches(patch, "(cs == null) || (0 == ((java.lang.String)cs).length())");

        String rootFolder = realBugPath + projectName + "/";
        String srcFolder = rootFolder + "src";
        clean(srcFolder); // TODO
    }

    @Test
    @Ignore
    public void PM1() {
        String projectName = "pm1";
        StatementType statementType = StatementType.PRECONDITION;
        NopolContext config = new NopolContext();
        List<Patch> patches = setupAndRun(
                projectName,
                statementType,
                new String[]{},
                config,
                "commons-beanutils-1.6.1.jar",
                "commons-collections-3.0.jar",
                "commons-discovery-0.2.jar",
                "commons-lang-2.0.jar",
                "commons-logging-1.0.3.jar",
                "junit-3.8.jar");

        assertEquals(patches.toString(), 1, patches.size());
        Patch patch = patches.get(0);
        assertEquals(patch.getType(), statementType);
        System.out.println(String.format("Patch for real bug %s: %s",
                projectName, patch.asString()));

        TestUtility.assertAgainstKnownPatches(patch, "0 <= ((org.apache.commons.math.stat.descriptive.moment.GeometricMean)this).getResult()");

        String rootFolder = realBugPath + projectName + "/";
        String srcFolder = rootFolder + "src";
        clean(srcFolder); // TODO
    }

    @Test
    public void PM2() {
        String projectName = "pm2";
        StatementType statementType = StatementType.PRECONDITION;
        NopolContext config = new NopolContext();
        List<Patch> patches = setupAndRun(
                projectName,
                statementType,
                new String[]{"org.apache.commons.math.exception.util.MessageFactoryTest"},
                config,
                "junit-4.10.jar");

        assertEquals(patches.toString(), 1, patches.size());
        Patch patch = patches.get(0);
        assertEquals(patch.getType(), statementType);
        System.out.println(String.format("Patch for real bug %s: %s",
                projectName, patch.asString()));


        TestUtility.assertAgainstKnownPatches(patch, "specific!=null", "null != specific", "0 != ((java.lang.StringBuilder)sb).length()");

        String rootFolder = realBugPath + projectName + "/";
        String srcFolder = rootFolder + "src";
        clean(srcFolder); // TODO
    }
    @Test
    public void PL1() {
        NopolContext config = new NopolContext();
        config.setComplianceLevel(4);
        String projectName = "pl1";
        StatementType statementType = StatementType.PRECONDITION;
        List<Patch> patches = setupAndRun(
                projectName,
                statementType,
                new String[]{"org.apache.commons.lang.time.StopWatchTest"},
                config,
                "junit-3.8.jar");

        assertEquals(patches.toString(), 1, patches.size());
        Patch patch = patches.get(0);
        assertEquals(patch.getType(), statementType);
        System.out.println(String.format("Patch for real bug %s: %s",
                projectName, patch.asString()));

        TestUtility.assertAgainstKnownPatches(
                patch,
                "(org.apache.commons.lang.time.StopWatch.this.runningState)==(org.apache.commons.lang.time.StopWatch.STATE_RUNNING)",
                "org.apache.commons.lang.time.StopWatch.this.runningState == org.apache.commons.lang.time.StopWatch.STATE_RUNNING",
                "org.apache.commons.lang.time.StopWatch.STATE_RUNNING == org.apache.commons.lang.time.StopWatch.this.runningState",
                "(org.apache.commons.lang.time.StopWatch.STATE_RUNNING)==(org.apache.commons.lang.time.StopWatch.this.runningState)",
                "org.apache.commons.lang.time.StopWatch.this.stopTime == -1",
                "-1 == org.apache.commons.lang.time.StopWatch.this.stopTime",
                "this.stopTime <= 0");

        String rootFolder = realBugPath + projectName + "/";
        String srcFolder = rootFolder + "src";
        clean(srcFolder); // TODO
    }

    @Test
    public void PL2() {
        NopolContext config = new NopolContext();
        config.setComplianceLevel(4);
        String projectName = "pl2";
        StatementType statementType = StatementType.PRECONDITION;
        List<Patch> patches = setupAndRun(
                projectName,
                statementType,
                new String[]{"org.apache.commons.lang.StringEscapeUtilsTest"},
                config,
                "junit-3.8.jar");

        assertEquals(patches.toString(), 1, patches.size());
        Patch patch = patches.get(0);
        assertEquals(patch.getType(), statementType);
        System.out.println(String.format("Patch for real bug %s: %s",
                projectName, patch.asString()));

        TestUtility.assertAgainstKnownPatches(patch, "escapeForwardSlash", "escapeSingleQuote");

        String rootFolder = realBugPath + projectName + "/";
        String srcFolder = rootFolder + "src";
        clean(srcFolder); // TODO
    }

    @Test
    @Ignore
    public void PL3() {
        NopolContext config = new NopolContext();
        config.setComplianceLevel(4);
        String projectName = "pl3";
        StatementType statementType = StatementType.PRECONDITION;
        List<Patch> patches = setupAndRun(
                projectName,
                statementType,
                new String[]{"org.apache.commons.lang.WordUtilsTest"},
                config,
                "junit-3.8.jar");

        assertEquals(patches.toString(), 1, patches.size());
        Patch patch = patches.get(0);
        assertEquals(patch.getType(), statementType);
        System.out.println(String.format("Patch for real bug %s: %s",
                projectName, patch.asString()));

        TestUtility.assertAgainstKnownPatches(patch, "lower > str.length()", "((java.lang.String)str).length() <= lower", "str.length() <= lower");

        String rootFolder = realBugPath + projectName + "/";
        String srcFolder = rootFolder + "src";
        clean(srcFolder); // TODO
    }

    @Test
    public void PL4() {
        String projectName = "pl4";
        StatementType statementType = StatementType.PRECONDITION;
        NopolContext config = new NopolContext();
        List<Patch> patches = setupAndRun(
                projectName,
                statementType,
                new String[]{"org.apache.commons.lang3.text.translate.NumericEntityUnescaperTest"},
                config,
                "junit-4.10.jar",
                "easymock-2.5.2.jar");

        assertEquals(patches.toString(), 1, patches.size());
        Patch patch = patches.get(0);
        assertEquals(patch.getType(), statementType);
        System.out.println(String.format("Patch for real bug %s: %s",
                projectName, patch.asString()));

        TestUtility.assertAgainstKnownPatches(patch, "(start)==(seqEnd)",
                "1 == (seqEnd / index)",
                "seqEnd == start",
                "start == seqEnd",
                "input.length() == start");

        String rootFolder = realBugPath + projectName + "/";
        String srcFolder = rootFolder + "src";
        clean(srcFolder); // TODO
    }

    //@Ignore
    @Test
    public void AM1() {
        String projectName = "am1";
        StatementType statementType = StatementType.DOUBLE_LITERAL;
        NopolContext config = new NopolContext();
        config.setSolver(NopolContext.NopolSolver.Z3);
        config.setSolverPath("lib/z3/z3_for_linux");

        List<Patch> patches = setupAndRun(
                projectName,
                statementType,
                new String[]{"org.apache.commons.math3.complex.QuaternionTest"}, config);

        assertEquals(patches.toString(), 1, patches.size());
        Patch patch = patches.get(0);
        assertEquals(patch.getType(), statementType);
        System.out.println(String.format("Patch for real bug %s: %s",
                projectName, patch.asString()));

        TestUtility.assertAgainstKnownPatches(patch, "start == seqEnd");

        String rootFolder = realBugPath + projectName + "/";
        String srcFolder = rootFolder + "src";
        clean(srcFolder); // TODO
    }

    @Ignore
    @Test
    public void AM2() {
        String projectName = "am2";
        StatementType statementType = StatementType.DOUBLE_LITERAL;
        NopolContext config = new NopolContext();
        List<Patch> patches = setupAndRun(
                projectName,
                statementType,
                new String[]{"org.apache.commons.math.optimization.direct.BOBYQAOptimizerTest"}, config);

        assertEquals(patches.toString(), 1, patches.size());
        Patch patch = patches.get(0);
        assertEquals(patch.getType(), statementType);
        System.out.println(String.format("Patch for real bug %s: %s",
                projectName, patch.asString()));

        TestUtility.assertAgainstKnownPatches(patch, "stopFitness;");

        String rootFolder = realBugPath + projectName + "/";
        String srcFolder = rootFolder + "src";
        clean(srcFolder); // TODO
    }

    @Ignore
    @Test
    public void AM3() {
        String projectName = "am3";
        StatementType statementType = StatementType.DOUBLE_LITERAL;
        NopolContext config = new NopolContext();
        List<Patch> patches = setupAndRun(
                projectName,
                statementType,
                new String[]{"org.apache.commons.math.stat.descriptive.moment.FirstMomentTest"}, config);

        assertEquals(patches.toString(), 1, patches.size());
        Patch patch = patches.get(0);
        assertEquals(patch.getType(), statementType);
        System.out.println(String.format("Patch for real bug %s: %s",
                projectName, patch.asString()));

        TestUtility.assertAgainstKnownPatches(patch, "dest.nDev = source.nDev");

        String rootFolder = realBugPath + projectName + "/";
        String srcFolder = rootFolder + "src";
        clean(srcFolder); // TODO
    }

    @Ignore
    @Test
    public void AM4() {
        String projectName = "am4";
        StatementType statementType = StatementType.DOUBLE_LITERAL;
        NopolContext config = new NopolContext();
        List<Patch> patches = setupAndRun(
                projectName,
                statementType,
                new String[]{}, config);

        assertEquals(patches.toString(), 1, patches.size());
        Patch patch = patches.get(0);
        assertEquals(patch.getType(), statementType);
        System.out.println(String.format("Patch for real bug %s: %s",
                projectName, patch.asString()));

        TestUtility.assertAgainstKnownPatches(patch, "dest.nDev = source.nDev");

        String rootFolder = realBugPath + projectName + "/";
        String srcFolder = rootFolder + "src";
        clean(srcFolder); // TODO
    }

    private List<Patch> setupAndRun(String projectName, StatementType statementType, String[] tests, NopolContext nopolContext, String... dependencies) {
        String rootFolder = realBugPath + projectName + "/";
        String srcFolder = rootFolder + "src";
        String binFolder = rootFolder + "target/classes/" + File.pathSeparatorChar + rootFolder + "target/test-classes/";
        String libFolder = realBugPath + "../data/lib/";
        String classpath = binFolder + File.pathSeparatorChar;
        for (int i = 0; i < dependencies.length; i++) {
            classpath += libFolder + dependencies[i];
            if (i < dependencies.length - 1) {
                classpath += File.pathSeparatorChar;
            }
        }
        nopolContext.setProjectSourcePath(new File[]{new File(srcFolder)});

        nopolContext.setSolverPath("lib/z3/z3_for_" + (isMac() ? "mac" : "linux"));
        nopolContext.setSolver(NopolContext.NopolSolver.Z3);
        nopolContext.setLocalizer(NopolContext.NopolLocalizer.GZOLTAR);
        nopolContext.setProjectTests(tests);
        nopolContext.setType(statementType);
        nopolContext.setProjectClasspath(JavaLibrary.classpathFrom(classpath));
        SolverFactory.setSolver(nopolContext.getSolver(), nopolContext.getSolverPath());
        switch (this.executionType) {
            case "symbolic":
                nopolContext.setOracle(NopolContext.NopolOracle.SYMBOLIC);
                break;
            case "nopol":
                nopolContext.setOracle(NopolContext.NopolOracle.ANGELIC);
                break;
            default:
                throw new RuntimeException("Execution type not found");
        }

        return new NoPol(nopolContext).build().getPatches();
    }


    private static void clean(String folderPath) {
        String path = folderPath + "/spooned";
        if (FileLibrary.isValidPath(path)) {
            FileLibrary.deleteDirectory(path);
        }
    }
}
