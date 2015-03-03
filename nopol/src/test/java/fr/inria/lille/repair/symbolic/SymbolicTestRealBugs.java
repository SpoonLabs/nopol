package fr.inria.lille.repair.symbolic;

import fr.inria.lille.repair.TestUtility;
import fr.inria.lille.repair.common.patch.Patch;
import fr.inria.lille.repair.common.synth.StatementType;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by Thomas Durieux on 03/03/15.
 */
public class SymbolicTestRealBugs extends TestUtility {
    private String executionType = "symbolic";

    public SymbolicTestRealBugs() {
        super("symbolic");
    }

    @Test
    public void CM1() {
        boolean isMaven = false;
        Patch patch = testRealBug(
                "cm1",
                isMaven,
                StatementType.CONDITIONAL,
                new String[] {"org.apache.commons.math.stat.univariate.rank.PercentileTest" },
                "commons-beanutils-1.7.0.jar",
                "commons-collections-2.0.jar",
                "commons-discovery-0.4.jar",
                "commons-lang-2.1.jar",
                "commons-logging-1.1.1.jar");

        fixComparison(patch, "(intPos)==(sorted.length), (fpos)==(n)",
                "(dif)==(begin)","(dif)==(0)", "(intPos)==(3)");
    }

    @Test
    public void CM2() {
        boolean isMaven = false;
        Patch patch = testRealBug(
                "cm2",
                isMaven,
                StatementType.CONDITIONAL,
                new String[] {"org.apache.commons.math.util.MathUtilsTest"},
                "commons-beanutils-1.5.jar",
                "commons-collections-3.0.jar",
                "commons-discovery-SNAPSHOT.jar",
                "commons-lang-2.0.jar",
                "commons-logging-1.0.3.jar");
        fixComparison(patch, "(k)==(org.apache.commons.math.util.MathUtils.NB)", "(org.apache.commons.math.util.MathUtils.NB)==(k)");
    }

    @Test
    public void CM3() {
        boolean isMaven = false;
        Patch patch = testRealBug(
                "cm3",
                isMaven,
                StatementType.CONDITIONAL,
                new String[] {"org.apache.commons.math.util.MathUtilsTest"},
                "commons-discovery-SNAPSHOT.jar",
                "commons-logging-1.0.3.jar");
        fixComparison(patch, "(n)==(org.apache.commons.math.util.MathUtils.NS)",
                "(n)<=(-1)");
    }

    @Test
    public void CM4() {
        boolean isMaven = false;
        Patch patch = testRealBug(
                "cm4",
                isMaven,
                StatementType.CONDITIONAL,
                new String[] {},
                "commons-discovery-SNAPSHOT.jar",
                "commons-logging-1.0.3.jar");
        fixComparison(patch, "((!((5)<=(org.apache.commons.math.analysis.PolynomialSplineFunction.this.knots.length)))&&((2)<(v)))||((v < (knots[0])))");
    }

    @Test
    public void CM5() {
        boolean isMaven = true;
        Patch patch = testRealBug(
                "cm5",
                isMaven,
                StatementType.CONDITIONAL,
                new String[] {"org.apache.commons.math.util.MathUtilsTest"});
        fixComparison(patch, "((org.apache.commons.math.util.MathUtils.ZS)==(v))||((!((0)<(u)))&&((u * v) == 0))",
                "((u * v) == 0)&&((!((0)!=(u)))||((v)<(org.apache.commons.math.util.MathUtils.PS)))");
    }

    @Test
    @Ignore
    public void CM6() {
        boolean isMaven = true;
        Patch patch = testRealBug(
                "cm6",
                isMaven,
                StatementType.CONDITIONAL,
                new String[] {});
        fixComparison(patch, "(ns)!=(n)");
    }

    @Test
    public void CM7() {
        boolean isMaven = true;
        Patch patch = testRealBug(
                "cm7",
                isMaven,
                StatementType.CONDITIONAL,
                new String[] {});
        fixComparison(patch, "(mean)<(1)");
    }

    @Test
    @Ignore
    public void CM8() {
        boolean isMaven = true;
        Patch patch = testRealBug(
                "cm8",
                isMaven,
                StatementType.CONDITIONAL,
                new String[] {"org.apache.commons.math3.fraction.FractionTest"});
        fixComparison(patch, "(ns)!=(n)");
    }

    @Test
    @Ignore
    public void CM9() {
        boolean isMaven = true;
        Patch patch = testRealBug(
                "cm9",
                isMaven,
                StatementType.CONDITIONAL,
                new String[]{});
        fixComparison(patch, "(ns)!=(n)");
    }

    @Test
    public void CM10() {
        boolean isMaven = true;
        Patch patch = testRealBug(
                "cm10",
                isMaven,
                StatementType.CONDITIONAL,
                new String[]{"org.apache.commons.math3.stat.correlation.CovarianceTest"});
        fixComparison(patch, "(ns)!=(n)");
    }


    @Test
    public void CL1() {
        boolean isMaven = false;
        Patch patch = testRealBug(
                "cl1",
                isMaven,
                StatementType.CONDITIONAL,
                new String[]{"org.apache.commons.lang.StringUtilsTest" });
        fixComparison(patch, "(text.length())==(3)", "(text.length())==(with.length())");
    }

    @Test
    public void CL2() {
        boolean isMaven = false;
        Patch patch = testRealBug(
                "cl2",
                isMaven,
                StatementType.CONDITIONAL,
                new String[]{"org.apache.commons.lang.StringUtilsTest" });
        fixComparison(patch,
                "(lastIdx)<(org.apache.commons.lang.StringUtils.blanks.length())",
                "(lastIdx)<=(0)");
    }

    @Test
    public void CL3() {
        boolean isMaven = false;
        Patch patch = testRealBug(
                "cl3",
                isMaven,
                StatementType.CONDITIONAL,
                new String[]{"org.apache.commons.lang.StringUtilsSubstringTest" },
                "junit-3.8.jar");
        fixComparison(patch, "(!((0)<=(len)))||((5)<(pos))",
                "((len)<=(-1))||((str.length())<(pos))");
    }

    @Test
    public void CL4() {
        boolean isMaven = false;
        Patch patch = testRealBug(
                "cl4",
                isMaven,
                StatementType.CONDITIONAL,
                new String[]{"org.apache.commons.lang.text.StrBuilderTest" });
        fixComparison(
                patch,
                "((!(str!=null))||(startIndex >= (size)))&&((!(str!=null))||(startIndex >= (size)))",
                "(!(str!=null))||(startIndex >= (size))");
    }

    @Test
    public void CL5() {
        boolean isMaven = true;
        Patch patch = testRealBug(
                "cl5",
                isMaven,
                StatementType.CONDITIONAL,
                new String[]{});
        fixComparison(patch, "(specific)!=(null)", "className.length()==0");
    }

    @Test
    @Ignore
    public void CL6() {
        boolean isMaven = true;
        Patch patch = testRealBug(
                "cl6",
                isMaven,
                StatementType.PRECONDITION,
                new String[]{},
                "easymock-2.5.2.jar");
        fixComparison(patch, "(specific)!=(null)");
    }

    @Test
    public void PM1() {
        boolean isMaven = true;
        Patch patch = testRealBug(
                "pm1",
                isMaven,
                StatementType.PRECONDITION,
                new String[]{},
                "commons-discovery-0.4.jar",
                "commons-logging-1.1.1.jar");
        fixComparison(patch, "(specific)!=(null)");
    }

    @Test
    public void PM2() {
        boolean isMaven = true;
        Patch patch = testRealBug(
                "pm2",
                isMaven,
                StatementType.PRECONDITION,
                new String[]{"org.apache.commons.math.exception.util.MessageFactoryTest"});
        fixComparison(patch, "specific!=null");
    }

    @Test
    public void PL1() {
        boolean isMaven = true;
        Patch patch = testRealBug(
                "pl1",
                isMaven,
                StatementType.PRECONDITION,
                new String[]{"org.apache.commons.lang.time.StopWatchTest"});
        fixComparison(
                patch,
                "(org.apache.commons.lang.time.StopWatch.STATE_RUNNING)==(org.apache.commons.lang.time.StopWatch.this.runningState)");
    }

    @Test
    public void PL2() {
        boolean isMaven = true;
        Patch patch = testRealBug(
                "pl2",
                isMaven,
                StatementType.PRECONDITION,
                new String[]{"org.apache.commons.lang.StringEscapeUtilsTest"});
        fixComparison(patch, "escapeForwardSlash");
    }

    @Test
    @Ignore
    public void PL3() {
        boolean isMaven = true;
        Patch patch = testRealBug(
                "pl3",
                isMaven,
                StatementType.PRECONDITION,
                new String[]{"org.apache.commons.lang.WordUtilsTest"});
        fixComparison(patch, "lower > str.length()");
    }

    @Test
    public void PL4() {
        boolean isMaven = true;
        Patch patch = testRealBug(
                "pl4",
                isMaven,
                StatementType.PRECONDITION,
                new String[]{"org.apache.commons.lang3.text.translate.NumericEntityUnescaperTest"},
                "easymock-2.5.2.jar");
        fixComparison(patch, "(start)==(seqEnd)");
    }

    @Ignore
    @Test
    public void AM1() {
        boolean isMaven = true;
        Patch patch = testRealBug(
                "am1",
                isMaven,
                StatementType.DOUBLE_LITERAL,
                new String[]{"org.apache.commons.math3.complex.QuaternionTest"});
        fixComparison(patch, "start == seqEnd");
    }

    @Ignore
    @Test
    public void AM2() {
        boolean isMaven = true;
        Patch patch = testRealBug(
                "am2",
                isMaven,
                StatementType.DOUBLE_LITERAL,
                new String[]{"org.apache.commons.math.optimization.direct.BOBYQAOptimizerTest"});
        fixComparison(patch, "stopFitness;");
    }

    @Ignore
    @Test
    public void AM3() {
        boolean isMaven = true;
        Patch patch = testRealBug(
                "am3",
                isMaven,
                StatementType.DOUBLE_LITERAL,
                new String[]{"org.apache.commons.math.stat.descriptive.moment.FirstMomentTest"});
        fixComparison(patch, "dest.nDev = source.nDev");
    }

    @Ignore
    @Test
    public void AM4() {
        boolean isMaven = true;
        Patch patch = testRealBug(
                "am4",
                isMaven,
                StatementType.DOUBLE_LITERAL,
                new String[]{});
        fixComparison(patch, "dest.nDev = source.nDev");
    }
}
