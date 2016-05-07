package fr.inria.lille.repair;

import fr.inria.lille.commons.synthesis.smt.solver.SolverFactory;
import fr.inria.lille.repair.common.config.Config;
import fr.inria.lille.repair.common.patch.Patch;
import fr.inria.lille.repair.common.synth.StatementType;
import fr.inria.lille.repair.nopol.NoPolLauncher;
import org.junit.Ignore;
import org.junit.Test;
import xxl.java.library.FileLibrary;
import xxl.java.library.JavaLibrary;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;

public abstract class AbstractRealBugsEvaluation extends TestUtility {

    // git clone https://github.com/SpoonLabs/nopol-experiments
    private String realBugPath = "../../nopol-experiments/dataset/";

    public AbstractRealBugsEvaluation(String executionType) {
        super(executionType);
    }

    @Test
    public void CM1() {
        Patch patch = testRealBug(
                "cm1",
                StatementType.CONDITIONAL,
                new String[] {},
                "commons-beanutils-1.7.0.jar",
                "commons-collections-2.0.jar",
                "commons-discovery-0.4.jar",
                "commons-lang-2.1.jar",
                "commons-logging-1.1.1.jar",
                "junit-3.8.jar");

        fixComparison(patch, "(intPos)==(sorted.length), (fpos)==(n)",
                "(dif)==(begin)","(dif)==(0)", "0 == difis", "(intPos)==(3)", "n == pos", "sorted.length <= pos", "sorted.length <= fpos", "length <= fpos", "n <= intPos");
    }

    @Test
    public void CM2() {
        Patch patch = testRealBug(
                "cm2",
                StatementType.PRECONDITION,
                new String[] {"org.apache.commons.math.util.MathUtilsTest"},
                "commons-beanutils-1.6.1.jar",
                "commons-collections-3.0.jar",
                "commons-discovery-0.2.jar",
                "commons-lang-2.0.jar",
                "commons-logging-1.0.3.jar",
                "junit-3.8.jar");
        fixComparison(patch, "(k)==(org.apache.commons.math.util.MathUtils.NB)",
                "(org.apache.commons.math.util.MathUtils.NB)==(k)",
                "k <= org.apache.commons.math.util.MathUtils.NB",
                "n < 0",
                "(n) != (org.apache.commons.math.util.MathUtils.ZS)");
    }

    @Test
    public void CM3() {
        Patch patch = testRealBug(
                "cm3",
                StatementType.PRECONDITION,
                new String[] {},
                "commons-discovery-0.2.jar",
                "commons-logging-1.0.3.jar",
                "junit-3.8.jar");
        fixComparison(patch, "(n)==(org.apache.commons.math.util.MathUtils.NS)",
                "(n)<=(-1)",
                "n < 0",
                "(org.apache.commons.math.util.MathUtils.ZS) != (n)");
    }

    @Test
    public void CM4() {
        Patch patch = testRealBug(
                "cm4",
                StatementType.CONDITIONAL,
                new String[] {},
                "commons-discovery-0.4.jar",
                "commons-logging-1.0.3.jar",
                "junit-3.8.jar");
        fixComparison(patch, "((!((5)<=(org.apache.commons.math.analysis.PolynomialSplineFunction.this.knots.length)))&&((2)<(v)))||((v < (knots[0])))",
                "((v < (knots[0]))) || ((knots[n]) < v)",
                "((v < (knots[0]))) || (((org.apache.commons.math.analysis.PolynomialSplineFunction.this.n + org.apache.commons.math.analysis.PolynomialSplineFunction.this.polynomials.length) - (v) < org.apache.commons.math.analysis.PolynomialSplineFunction.this.knots.length) && (!(org.apache.commons.math.analysis.PolynomialSplineFunction.this.polynomials.length <= v)))");
    }

    @Test
    public void CM5() {
        Patch patch = testRealBug(
                "cm5",
                StatementType.CONDITIONAL,
                new String[] {"org.apache.commons.math.util.MathUtilsTest"},
                "commons-beanutils-1.7.0.jar",
                "commons-collections-3.0.jar",
                "commons-discovery-0.4.jar",
                "commons-lang-2.1.jar",
                "commons-logging-1.1.1.jar",
                "junit-4.10.jar");
        fixComparison(patch, "((org.apache.commons.math.util.MathUtils.ZS)==(v))||((!((0)<(u)))&&((u * v) == 0))",
                "((u * v) == 0)&&((!((0)!=(u)))||((v)<(org.apache.commons.math.util.MathUtils.PS)))",
                "(0 == u) || (0 == v)",
                "(0 == v) || (!((u) != (org.apache.commons.math.util.MathUtils.ZB)))",
                "!(((org.apache.commons.math.util.MathUtils.EPSILON <= u) || (u < org.apache.commons.math.util.MathUtils.NB)) && ((v) != (org.apache.commons.math.util.MathUtils.ZB)))",
                "((!((org.apache.commons.math.util.MathUtils.ZB) != (v))) || (org.apache.commons.math.util.MathUtils.ZB == u)) && ((u * v) == 0)");
    }

    @Test
    public void CM6() {
        Config.INSTANCE.setComplianceLevel(5);
        Patch patch = testRealBug(
                "cm6",
                StatementType.PRECONDITION,
                new String[] {},
                "junit-4.10.jar");
        Config.INSTANCE.setComplianceLevel(7);
        fixComparison(patch, "(ns)!=(n)",
                "(0)<=(fa)",
                "upperBound == 0.0",
                "-1 == b");
    }

    @Test
    public void CM7() {
        Patch patch = testRealBug(
                "cm7",
                StatementType.CONDITIONAL,
                new String[] {},
                "junit-4.10.jar");
        fixComparison(patch, "(mean)<(1)", "mean <= 0", "mean <= 0.0");
        // Execution time 10min 40sec break point in a multiples 100 000 iterations slowdown the execution
    }

    @Test
    @Ignore
    public void CM8() {
        Patch patch = testRealBug(
                "cm8",
                StatementType.CONDITIONAL,
                new String[] {"org.apache.commons.math3.fraction.FractionTest"},
                "junit-4.10.jar");
        fixComparison(patch, "(ns)!=(n)",
                "((org.apache.commons.math3.fraction.Fraction)((org.apache.commons.math3.fraction.Fraction)this).getReducedFraction(maxDenominator, maxIterations)).percentageValue() < maxDenominator");
    }

    @Test
    @Ignore
    public void CM9() {
        Patch patch = testRealBug(
                "cm9",
                StatementType.CONDITIONAL,
                new String[]{
                        //"org.apache.commons.math3.util.FastMathTest"
                },
                "junit-4.10.jar");
        fixComparison(patch, "(ns)!=(n)");
    }

    @Test
    public void CM10() {
        Patch patch = testRealBug(
                "cm10",
                StatementType.PRECONDITION,
                new String[]{"org.apache.commons.math3.stat.correlation.CovarianceTest"},
                "junit-4.10.jar");
        fixComparison(patch, "(nRows < 2)", "nRows < 2", "((org.apache.commons.math3.linear.AbstractRealMatrix)matrix).isSquare()", "(1) != (nCols)");
    }


    @Test
    public void CL1() {
        Config.INSTANCE.setComplianceLevel(4);
        Patch patch = testRealBug(
                "cl1",
                StatementType.CONDITIONAL,
                new String[]{"org.apache.commons.lang.StringUtilsTest" },
                "junit-3.8.jar");
        Config.INSTANCE.setComplianceLevel(7);
        fixComparison(patch,
                "(text.length())==(3)",
                "(text.length())==(with.length())",
                "(null == with) || (0 != ((java.lang.String)with).length())",
                "(with.length()) != (0)");
    }

    @Test
    public void CL2() {
        Config.INSTANCE.setComplianceLevel(4);
        Patch patch = testRealBug(
                "cl2",
                StatementType.CONDITIONAL,
                new String[]{"org.apache.commons.lang.StringUtilsTest" },
                "junit-3.8.jar");
        Config.INSTANCE.setComplianceLevel(7);
        fixComparison(patch,
                "(lastIdx)<(org.apache.commons.lang.StringUtils.blanks.length())",
                "lastIdx < org.apache.commons.lang.StringUtils.blanks.length()",
                "(lastIdx)<=(0)",
                "lastIdx <= 0",
                "(lastIdx)<(1)",
                "str.length() <= 1");
    }

    @Test
    public void CL3() {
        Config.INSTANCE.setComplianceLevel(4);
        Patch patch = testRealBug(
                "cl3",
                StatementType.CONDITIONAL,
                new String[]{"org.apache.commons.lang.StringUtilsSubstringTest" },
                "junit-3.8.jar");
        Config.INSTANCE.setComplianceLevel(7);
        fixComparison(patch, "(!(0 <= len)) || (str.length() < pos)",
                "((len)<=(-1))||((str.length())<(pos))",
                "(len < 0) || (((java.lang.String)str).length() < pos)",
                "(((java.lang.String)str).length() < pos) || (len < 0)",
                "(len <= -1) || (str.length() < pos)");
    }

    @Test
    public void CL4() {
        Config.INSTANCE.setComplianceLevel(4);
        Patch patch = testRealBug(
                "cl4",
                StatementType.CONDITIONAL,
                new String[]{"org.apache.commons.lang.text.StrBuilderTest" },
                "junit-3.8.jar");
        Config.INSTANCE.setComplianceLevel(7);
        fixComparison(
                patch,
                "((!(str!=null))||(startIndex >= (size)))&&((!(str!=null))||(startIndex >= (size)))",
                "(!(str!=null))||(startIndex >= (size))",
                "(startIndex >= (size)) || (!(str!=null))",
                "((startIndex >= (size)) || (!(str!=null))) && ((org.apache.commons.lang.text.StrBuilder.this.size) != (-1))");
    }

    @Test
    public void CL5() {
        Config.INSTANCE.setComplianceLevel(6);
        Patch patch = testRealBug(
                "cl5",
                StatementType.CONDITIONAL,
                new String[]{},
                "junit-3.8.jar");
        Config.INSTANCE.setComplianceLevel(7);
        fixComparison(patch,
                "className.length()==0",
                "0 == ((java.lang.String)className).length()");
    }

    @Test
    @Ignore
    public void CL6() {
        Patch patch = testRealBug(
                "cl6",
                StatementType.CONDITIONAL,
                new String[]{},
                "easymock-2.5.2.jar",
                "junit-4.10.jar");
        fixComparison(patch, "(cs == null) || (0 == ((java.lang.String)cs).length())");
    }

    @Test
    @Ignore
    public void PM1() {
        Patch patch = testRealBug(
                "pm1",
                StatementType.PRECONDITION,
                new String[]{},
                "commons-beanutils-1.6.1.jar",
                "commons-collections-3.0.jar",
                "commons-discovery-0.2.jar",
                "commons-lang-2.0.jar",
                "commons-logging-1.0.3.jar",
                "junit-3.8.jar");
        fixComparison(patch, "0 <= ((org.apache.commons.math.stat.descriptive.moment.GeometricMean)this).getResult()");
    }

    @Test
    public void PM2() {
        Patch patch = testRealBug(
                "pm2",
                StatementType.PRECONDITION,
                new String[]{"org.apache.commons.math.exception.util.MessageFactoryTest"},
                "junit-4.10.jar");
        fixComparison(patch, "specific!=null", "null != specific", "0 != ((java.lang.StringBuilder)sb).length()");
    }

    @Test
    public void PL1() {
        Config.INSTANCE.setComplianceLevel(4);
        Patch patch = testRealBug(
                "pl1",
                StatementType.PRECONDITION,
                new String[]{"org.apache.commons.lang.time.StopWatchTest"},
                "junit-3.8.jar");
        Config.INSTANCE.setComplianceLevel(7);
        fixComparison(
                patch,
                "(org.apache.commons.lang.time.StopWatch.this.runningState)==(org.apache.commons.lang.time.StopWatch.STATE_RUNNING)",
                "org.apache.commons.lang.time.StopWatch.this.runningState == org.apache.commons.lang.time.StopWatch.STATE_RUNNING",
                "org.apache.commons.lang.time.StopWatch.STATE_RUNNING == org.apache.commons.lang.time.StopWatch.this.runningState",
                "(org.apache.commons.lang.time.StopWatch.STATE_RUNNING)==(org.apache.commons.lang.time.StopWatch.this.runningState)",
                "org.apache.commons.lang.time.StopWatch.this.stopTime == -1",
                "-1 == org.apache.commons.lang.time.StopWatch.this.stopTime",
                "this.stopTime <= 0");
    }

    @Test
    public void PL2() {
        Config.INSTANCE.setComplianceLevel(4);
        Patch patch = testRealBug(
                "pl2",
                StatementType.PRECONDITION,
                new String[]{"org.apache.commons.lang.StringEscapeUtilsTest"},
                "junit-3.8.jar");
        Config.INSTANCE.setComplianceLevel(7);
        fixComparison(patch, "escapeForwardSlash", "escapeSingleQuote");
    }

    @Test
    @Ignore
    public void PL3() {
        Config.INSTANCE.setComplianceLevel(4);
        Patch patch = testRealBug(
                "pl3",
                StatementType.PRECONDITION,
                new String[]{"org.apache.commons.lang.WordUtilsTest"},
                "junit-3.8.jar");
        Config.INSTANCE.setComplianceLevel(7);
        fixComparison(patch, "lower > str.length()", "((java.lang.String)str).length() <= lower", "str.length() <= lower");
    }

    @Test
    public void PL4() {
        Patch patch = testRealBug(
                "pl4",
                StatementType.PRECONDITION,
                new String[]{"org.apache.commons.lang3.text.translate.NumericEntityUnescaperTest"},
                "junit-4.10.jar",
                "easymock-2.5.2.jar");
        fixComparison(patch, "(start)==(seqEnd)",
                "1 == (seqEnd / index)",
                "seqEnd == start",
                "start == seqEnd",
                "input.length() == start");
    }

    //@Ignore
    @Test
    public void AM1() {
        Patch patch = testRealBug(
                "am1",
                StatementType.DOUBLE_LITERAL,
                new String[]{"org.apache.commons.math3.complex.QuaternionTest"});
        fixComparison(patch, "start == seqEnd");
    }

    @Ignore
    @Test
    public void AM2() {
        Patch patch = testRealBug(
                "am2",
                StatementType.DOUBLE_LITERAL,
                new String[]{"org.apache.commons.math.optimization.direct.BOBYQAOptimizerTest"});
        fixComparison(patch, "stopFitness;");
    }

    @Ignore
    @Test
    public void AM3() {
        Patch patch = testRealBug(
                "am3",
                StatementType.DOUBLE_LITERAL,
                new String[]{"org.apache.commons.math.stat.descriptive.moment.FirstMomentTest"});
        fixComparison(patch, "dest.nDev = source.nDev");
    }

    @Ignore
    @Test
    public void AM4() {
        Patch patch = testRealBug(
                "am4",
                StatementType.DOUBLE_LITERAL,
                new String[]{});
        fixComparison(patch, "dest.nDev = source.nDev");
    }

    private Patch testRealBug(String projectName, StatementType statementType, String[] tests, String... dependencies) {
        String rootFolder = realBugPath + projectName + "/";
        String srcFolder = rootFolder + "src";
        String binFolder = rootFolder + "target/classes" + File.pathSeparatorChar + rootFolder + "target/test-classes";

        String libFolder = realBugPath + "../data/lib/";

        String classpath = binFolder + File.pathSeparatorChar;
        for (int i = 0; i<dependencies.length; i++) {
            classpath += libFolder + dependencies[i];
            if(i<dependencies.length -1) {
                classpath += File.pathSeparatorChar;
            }
        }
        SolverFactory.setSolver(solver, solverPath);
        List<Patch> patches;
        switch (this.executionType) {
            case "symbolic":
                Config.INSTANCE.setOracle(Config.NopolOracle.SYMBOLIC);
                break;
            case "nopol":
                Config.INSTANCE.setOracle(Config.NopolOracle.ANGELIC);
                break;
            default:
                throw new RuntimeException("Execution type not found");
        }
        patches = NoPolLauncher
                .launch(new File[]{FileLibrary.openFrom(srcFolder)},
                        JavaLibrary.classpathFrom(classpath),
                        statementType,
                        tests);
        assertEquals(patches.toString(), 1, patches.size());
        Patch patch = patches.get(0);
        assertEquals(patch.getType(), statementType);
        System.out.println(String.format("Patch for real bug %s: %s",
                projectName, patch.asString()));
        clean(srcFolder);
        return patch;
    }

}
