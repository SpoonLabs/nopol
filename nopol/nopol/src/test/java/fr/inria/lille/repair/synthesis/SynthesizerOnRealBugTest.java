package fr.inria.lille.repair.synthesis;

import fr.inria.lille.repair.common.Candidates;
import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.nopol.SourceLocation;
import fr.inria.lille.repair.expression.Expression;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import xxl.java.library.JavaLibrary;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by spirals on 06/03/15.
 * Start by cloning data in nopol-dataset:
 * git clone https://github.com/SpoonLabs/nopol-experiments
 */
@Ignore
public class SynthesizerOnRealBugTest {

    @Test
    public void CM1() throws InterruptedException {
        Map<String, Object[]> oracle = new HashMap<>();
        oracle.put("org.apache.commons.math.stat.univariate.rank.PercentileTest#testHighPercentile", new Object[]{true});
        oracle.put("org.apache.commons.math.stat.univariate.rank.PercentileTest#testEvaluation", new Object[]{false});

        SourceLocation location = new SourceLocation("org.apache.commons.math.stat.univariate.rank.Percentile", 151);
        boolean isMaven = false;
        test("cm1",
                isMaven,
                "",
                oracle,
                location,
                new String[] {},
                new NopolContext(),
                "n <= pos",
                "n == pos");
    }

    @Test
    public void CM2() throws InterruptedException {
        Map<String, Object[]> oracle = new HashMap<>();
        oracle.put("org.apache.commons.math.util.MathUtilsTest#test0Choose0_3", new Object[]{false});
        oracle.put("org.apache.commons.math.util.MathUtilsTest#testBinomialCoefficient1", new Object[]{false});
        oracle.put("org.apache.commons.math.util.MathUtilsTest#testBinomialCoefficient2", new Object[]{false});
        oracle.put("org.apache.commons.math.util.MathUtilsTest#testBinomialCoefficient3", new Object[]{false});
        oracle.put("org.apache.commons.math.util.MathUtilsTest#testBinomialCoefficient6", new Object[]{false});
        oracle.put("org.apache.commons.math.util.MathUtilsTest#testBinomialCoefficient14_1", new Object[]{false});
        oracle.put("org.apache.commons.math.util.MathUtilsTest#testBinomialCoefficient69", new Object[]{true});

        SourceLocation location = new SourceLocation("org.apache.commons.math.util.MathUtils", 254);
        boolean isMaven = false;
        test("cm2",
                isMaven,
                "",
                oracle,
                location,
                new String[] {},
                new NopolContext(),
                "n < 0", "n < 0.0");
    }

    @Test
    public void CM3() throws InterruptedException {
        Map<String, Object[]> oracle = new HashMap<>();
        oracle.put("org.apache.commons.math.util.MathUtilsTest#testFactorial", new Object[]{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false});
        oracle.put("org.apache.commons.math.util.MathUtilsTest#testFactorialFail_1", new Object[]{true});
        oracle.put("org.apache.commons.math.util.MathUtilsTest#testFactorialFail_2", new Object[]{true});
        oracle.put("org.apache.commons.math.util.MathUtilsTest#testFactorialFail_3", new Object[]{true});
        oracle.put("org.apache.commons.math.util.MathUtilsTest#testFactorialFail_4", new Object[]{false});
        oracle.put("org.apache.commons.math.util.MathUtilsTest#testFactorialFail_5", new Object[]{false});

        SourceLocation location = new SourceLocation("org.apache.commons.math.util.MathUtils", 419);
        boolean isMaven = false;
        test("cm3",
                isMaven,
                "",
                oracle,
                location,
                new String[] {"commons-discovery-SNAPSHOT.jar",
                        "commons-logging-1.0.3.jar"},
                new NopolContext(),
                "n < 0", "n < 0.0");
    }

    @Test
    public void CM5() throws InterruptedException {
        Map<String, Object[]> oracle = new HashMap<>();
        oracle.put("org.apache.commons.math.util.MathUtilsTest#test238", new Object[]{false});
        oracle.put("org.apache.commons.math.util.MathUtilsTest#testGcd", new Object[]{true, true, true, true, true, false, false, false, false, false, false, false, false});

        SourceLocation location = new SourceLocation("org.apache.commons.math.util.MathUtils", 413);
        boolean isMaven = true;
        test("cm5",
                isMaven,
                "java/",
                oracle,
                location,
                new String[] {},
                new NopolContext(),
                "(0 == u) || (0 == v)", "(u == 0) || (v == 0)",  "(v == 0) || (u == 0)", "(0 == v) || (0 == u)");
    }

    @Test
    public void CM6() throws InterruptedException {
        Map<String, Object[]> oracle = new HashMap<>();
        oracle.put("org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtilsTest#testBracketSin", new Object[]{true});
        oracle.put("org.apache.commons.math.distribution.NormalDistributionTest#testMath280", new Object[]{false});

        SourceLocation location = new SourceLocation("org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils", 200);
        boolean isMaven = true;
        test("cm6",
                isMaven,
                "java/",
                oracle,
                location,
                new String[] {},
                new NopolContext(),
                "0.0 < (fa * fb)");
    }

    @Test
    public void CM7() throws InterruptedException {
        Map<String, Object[]> oracle = new HashMap<>();
        oracle.put("org.apache.commons.math.random.RandomDataTest#testNextExponential", new Object[]{true});
        oracle.put("org.apache.commons.math.random.RandomDataTest#testNextExponential2", new Object[]{true, true});
        Object[] values = new Object[151];
        Arrays.fill(values, false);
        oracle.put("org.apache.commons.math.random.RandomDataTest#testNextExponential3", values);

        SourceLocation location = new SourceLocation("org.apache.commons.math.random.RandomDataImpl", 465);
        boolean isMaven = true;
        test("cm7",
                isMaven,
                "main/java/",
                oracle,
                location,
                new String[] {},
                new NopolContext(),
                "mean < 1", "mean <= 0", "mean <= 0.0", "mean == 0.0");
    }

    @Test
    public void CM8() throws InterruptedException {
        Map<String, Object[]> oracle = new HashMap<>();
        oracle.put("org.apache.commons.math3.fraction.FractionTest#testIntegerOverflow", new Object[]{true, true, true, true});
        oracle.put("org.apache.commons.math3.fraction.FractionTest#testConstructor", new Object[]{false,false,false,false,false,false,false,false});

        NopolContext config = new NopolContext();
        config.setCollectStaticMethods(true);

        SourceLocation location = new SourceLocation("org.apache.commons.math3.fraction.Fraction", 210);
        boolean isMaven = true;
        test("cm8",
                isMaven,
                "main/java/",
                oracle,
                location,
                new String[] {},
                config,
                "overflow < FastMath.abs(p2) || overflow < FastMath.abs(q2)");
    }

    @Test
    public void CM9() throws InterruptedException {
        Map<String, Object[]> oracle = new HashMap<>();
        oracle.put("org.apache.commons.math3.util.FastMathTest#testMath904_1", new Object[]{false, false});
        oracle.put("org.apache.commons.math3.util.FastMathTest#testMath904_2", new Object[]{false, false});

        SourceLocation location = new SourceLocation("org.apache.commons.math3.util.FastMath", 1543);
        boolean isMaven = true;
        test("cm9",
                isMaven,
                "main/java/",
                oracle,
                location,
                new String[] {},
                new NopolContext(),
                "y >= TWO_POWER_53 || y <= -TWO_POWER_53");
    }

    @Test
    public void CM10() throws InterruptedException {
        Map<String, Object[]> oracle = new HashMap<>();
        oracle.put("org.apache.commons.math3.stat.correlation.CovarianceTest#testSwissFertility", new Object[]{false});
        oracle.put("org.apache.commons.math3.stat.correlation.CovarianceTest#test_Jifeng_2", new Object[]{false});
        oracle.put("org.apache.commons.math3.stat.correlation.CovarianceTest#test_Jifeng_4", new Object[]{true});

        oracle.put("org.apache.commons.math3.stat.correlation.CovarianceTest#testLongly", new Object[]{false});
        oracle.put("org.apache.commons.math3.stat.correlation.CovarianceTest#testOneColumn", new Object[]{false});

        SourceLocation location = new SourceLocation("org.apache.commons.math3.stat.correlation.Covariance", 291);
        boolean isMaven = true;
        test("cm10",
                isMaven,
                "main/java/",
                oracle,
                location,
                new String[] {},
                new NopolContext(),
                "(nRows < 2) || (nCols < 1)", "matrix.isSquare()");
    }

    @Test
    public void CL1() throws InterruptedException {
        Map<String, Object[]> oracle = new HashMap<>();
        oracle.put("org.apache.commons.lang.StringUtilsTest#testReplaceFunctions1", new Object[]{false});
        oracle.put("org.apache.commons.lang.StringUtilsTest#testReplaceFunctions2", new Object[]{false});
        oracle.put("org.apache.commons.lang.StringUtilsTest#testReplaceFunctions3", new Object[]{false});
        oracle.put("org.apache.commons.lang.StringUtilsTest#testReplaceFunctions4", new Object[]{false});
        oracle.put("org.apache.commons.lang.StringUtilsTest#testReplaceFunctions5", new Object[]{true});
        oracle.put("org.apache.commons.lang.StringUtilsTest#testReplaceFunctions6", new Object[]{true});
        oracle.put("org.apache.commons.lang.StringUtilsTest#testReplaceFunctions7", new Object[]{true});
        oracle.put("org.apache.commons.lang.StringUtilsTest#testReplaceFunctions8", new Object[]{true});
        oracle.put("org.apache.commons.lang.StringUtilsTest#testReplaceFunctions9", new Object[]{true});

        SourceLocation location = new SourceLocation("org.apache.commons.lang.StringUtils", 630);
        boolean isMaven = false;
        test("cl1",
                isMaven,
                "",
                oracle,
                location,
                new String[] {},
                new NopolContext(),
                "text == null || repl == null || with == null || repl.length() == 0", "(repl == null) || (0 == repl.length())",
                "(null == repl) || (0 == repl.length())",
                "(with == null) || (with.length() != 0)",
                "(null == repl) || (0 == ((java.lang.String)repl).length())");
        // tests are simplified
    }

    @Test
    public void CL2() throws InterruptedException {
        Map<String, Object[]> oracle = new HashMap<>();
        oracle.put("org.apache.commons.lang.StringUtilsTest#testChopNewLine0", new Object[]{false});
        oracle.put("org.apache.commons.lang.StringUtilsTest#testChopNewLine1", new Object[]{false});
        oracle.put("org.apache.commons.lang.StringUtilsTest#testChopNewLine2", new Object[]{false});
        oracle.put("org.apache.commons.lang.StringUtilsTest#testChopNewLine3", new Object[]{false});
        oracle.put("org.apache.commons.lang.StringUtilsTest#testChopNewLine4", new Object[]{false});
        oracle.put("org.apache.commons.lang.StringUtilsTest#testChopNewLine5", new Object[]{false});
        oracle.put("org.apache.commons.lang.StringUtilsTest#testChopNewLine6", new Object[]{true});
        oracle.put("org.apache.commons.lang.StringUtilsTest#testChopNewLine7", new Object[]{true});
        oracle.put("org.apache.commons.lang.StringUtilsTest#testChopNewLine8", new Object[]{false});

        SourceLocation location = new SourceLocation("org.apache.commons.lang.StringUtils", 1051);
        boolean isMaven = false;
        test("cl2",
                isMaven,
                "",
                oracle,
                location,
                new String[] {},
                new NopolContext(),
                "lastIdx <= 0");
    }

    @Test
    public void CL3() throws InterruptedException {
        Map<String, Object[]> oracle = new HashMap<>();
        oracle.put("org.apache.commons.lang.StringUtilsSubstringTest#testMid_String1", new Object[]{true});
        oracle.put("org.apache.commons.lang.StringUtilsSubstringTest#testMid_String2", new Object[]{true});
        oracle.put("org.apache.commons.lang.StringUtilsSubstringTest#testMid_String3", new Object[]{false});
        oracle.put("org.apache.commons.lang.StringUtilsSubstringTest#testMid_String4", new Object[]{false});
        oracle.put("org.apache.commons.lang.StringUtilsSubstringTest#testMid_String5", new Object[]{false});
        oracle.put("org.apache.commons.lang.StringUtilsSubstringTest#testMid_String6", new Object[]{false});
        oracle.put("org.apache.commons.lang.StringUtilsSubstringTest#testMid_String7", new Object[]{false});
        oracle.put("org.apache.commons.lang.StringUtilsSubstringTest#testMid_String8", new Object[]{false});
        oracle.put("org.apache.commons.lang.StringUtilsSubstringTest#testMid_String9", new Object[]{false});
        oracle.put("org.apache.commons.lang.StringUtilsSubstringTest#testMid_String10", new Object[]{true});
        oracle.put("org.apache.commons.lang.StringUtilsSubstringTest#testMid_String11", new Object[]{false});
        oracle.put("org.apache.commons.lang.StringUtilsSubstringTest#testMid_String12", new Object[]{false});
        oracle.put("org.apache.commons.lang.StringUtilsSubstringTest#testMid_String13", new Object[]{true});

        SourceLocation location = new SourceLocation("org.apache.commons.lang.StringUtils", 1522);
        boolean isMaven = false;
        test("cl3",
                isMaven,
                "",
                oracle,
                location,
                new String[] {},
                new NopolContext(),
                "(len < 0) || (str.length() < pos)",
                "(str.length() < pos) || (len < 0)",
                "(len < 0) || (((java.lang.String)str).length() < pos)",
                "(((java.lang.String)str).length() < pos) || (len < 0)");
    }

    @Test
    public void CL4() throws InterruptedException {
        Map<String, Object[]> oracle = new HashMap<>();

        oracle.put("org.apache.commons.lang.text.StrBuilderTest#testDeleteString", new Object[]{false,false,false,false,true,false,true});

        oracle.put("org.apache.commons.lang.text.StrBuilderTest#testIndexOfStringInt1", new Object[]{false});
        oracle.put("org.apache.commons.lang.text.StrBuilderTest#testIndexOfStringInt2", new Object[]{false});
        oracle.put("org.apache.commons.lang.text.StrBuilderTest#testIndexOfStringInt12", new Object[]{true});

        oracle.put("org.apache.commons.lang.text.StrBuilderTest#testIndexOfString2", new Object[]{false});
        oracle.put("org.apache.commons.lang.text.StrBuilderTest#testIndexOfString5", new Object[]{false});
        oracle.put("org.apache.commons.lang.text.StrBuilderTest#testIndexOfString10", new Object[]{true});

        SourceLocation location = new SourceLocation("org.apache.commons.lang.text.StrBuilder", 1460);
        boolean isMaven = false;
        test("cl4",
                isMaven,
                "",
                oracle,
                location,
                new String[] {},
                new NopolContext(),
                "str == null || startIndex >= size", "(startIndex == this.size) || (str == null)");
    }

    @Test
    public void CL5() throws InterruptedException {
        Map<String, Object[]> oracle = new HashMap<>();
        oracle.put("org.apache.commons.lang.ClassUtilsTest#test_getPackageName_Object1", new Object[]{false});
        oracle.put("org.apache.commons.lang.ClassUtilsTest#test_getPackageName_Object2", new Object[]{false});
        oracle.put("org.apache.commons.lang.ClassUtilsTest#test_getPackageName_Object3", new Object[]{false});
        oracle.put("org.apache.commons.lang.ClassUtilsTest#test_getPackageName_Class1", new Object[]{false});
        oracle.put("org.apache.commons.lang.ClassUtilsTest#test_getPackageName_Class2", new Object[]{false});
        oracle.put("org.apache.commons.lang.ClassUtilsTest#test_getPackageName_Class3", new Object[]{true});
        oracle.put("org.apache.commons.lang.ClassUtilsTest#test_getPackageName_Class4", new Object[]{false});
        oracle.put("org.apache.commons.lang.ClassUtilsTest#test_getPackageName_String4", new Object[]{true});

        SourceLocation location = new SourceLocation("org.apache.commons.lang.ClassUtils", 261);
        boolean isMaven = true;
        test("cl5",
                isMaven,
                "java/",
                oracle,
                location,
                new String[] {},
                new NopolContext(),
                "className == null || className.length() == 0");
    }
    @Test
    public void CL6() throws InterruptedException {
        Map<String, Object[]> oracle = new HashMap<>();
        oracle.put("org.apache.commons.lang3.StringUtilsIsTest#testIsAlpha0", new Object[]{true});
        oracle.put("org.apache.commons.lang3.StringUtilsIsTest#testIsAlpha1", new Object[]{true});
        oracle.put("org.apache.commons.lang3.StringUtilsIsTest#testIsAlpha2", new Object[]{false});
        oracle.put("org.apache.commons.lang3.StringUtilsIsTest#testIsAlpha3", new Object[]{false});
        oracle.put("org.apache.commons.lang3.StringUtilsIsTest#testIsAlpha4", new Object[]{false});
        oracle.put("org.apache.commons.lang3.StringUtilsIsTest#testIsAlpha5", new Object[]{false});
        oracle.put("org.apache.commons.lang3.StringUtilsIsTest#testIsAlpha6", new Object[]{false});
        oracle.put("org.apache.commons.lang3.StringUtilsIsTest#testIsAlpha7", new Object[]{false});
        oracle.put("org.apache.commons.lang3.StringUtilsIsTest#testIsAlpha8", new Object[]{false});
        oracle.put("org.apache.commons.lang3.StringUtilsIsTest#testIsAlpha9", new Object[]{false});
        oracle.put("org.apache.commons.lang3.StringUtilsIsTest#testIsAlpha10", new Object[]{false});

        SourceLocation location = new SourceLocation("org.apache.commons.lang3.StringUtils", 5217);
        boolean isMaven = true;
        test("cl6",
                isMaven,
                "main/java/",
                oracle,
                location,
                new String[] {},
                new NopolContext(),
                "(cs == null) || (cs.length() == 0)",
                "(cs == null) || (0 == cs.length())");
    }

    @Test
    public void PL1() throws InterruptedException {
        Map<String, Object[]> oracle = new HashMap<>();
        oracle.put("org.apache.commons.lang.time.StopWatchTest#testStopWatchSimple", new Object[]{true});
        oracle.put("org.apache.commons.lang.time.StopWatchTest#testStopWatchSimple2", new Object[]{true});
        oracle.put("org.apache.commons.lang.time.StopWatchTest#testStopWatchSimple3", new Object[]{true});
        oracle.put("org.apache.commons.lang.time.StopWatchTest#testStopWatchSimple4", new Object[]{true});
        oracle.put("org.apache.commons.lang.time.StopWatchTest#testStopWatchSplit1", new Object[]{true});
        oracle.put("org.apache.commons.lang.time.StopWatchTest#testStopWatchSplit2", new Object[]{true});
        oracle.put("org.apache.commons.lang.time.StopWatchTest#testStopWatchSplit3", new Object[]{true});
        oracle.put("org.apache.commons.lang.time.StopWatchTest#testStopWatchSplit4", new Object[]{true});
        oracle.put("org.apache.commons.lang.time.StopWatchTest#testStopWatchSplit5", new Object[]{true});
        oracle.put("org.apache.commons.lang.time.StopWatchTest#testStopWatchSuspend1", new Object[]{true});
        oracle.put("org.apache.commons.lang.time.StopWatchTest#testStopWatchSuspend2", new Object[]{true});
        oracle.put("org.apache.commons.lang.time.StopWatchTest#testStopWatchSuspend3", new Object[]{true});
        oracle.put("org.apache.commons.lang.time.StopWatchTest#testStopWatchSuspend4", new Object[]{true});
        oracle.put("org.apache.commons.lang.time.StopWatchTest#testLang315", new Object[]{false});

        SourceLocation location = new SourceLocation("org.apache.commons.lang.time.StopWatch", 119);
        boolean isMaven = true;
        test("pl1",
                isMaven,
                "java/",
                oracle,
                location,
                new String[] {},
                new NopolContext(),
                "this.runningState == 1",
                "this.runningState == STATE_RUNNING",
                "this.stopTime <= 0");
    }
    @Test
    public void PL2() throws InterruptedException {
        Map<String, Object[]> oracle = new HashMap<>();
        oracle.put("org.apache.commons.lang.StringEscapeUtilsTest#testEscapeJavaWithSlash", new Object[]{false, false, false,true, true, true,true, true, true,true, true, true, true});
        oracle.put("org.apache.commons.lang.StringEscapeUtilsTest#testEscapeJavaScript", new Object[]{true, true, true,true, true, true,true, true, true,true, true, true, true});

        SourceLocation location = new SourceLocation("org.apache.commons.lang.StringEscapeUtils", 246);
        boolean isMaven = true;
        test("pl2",
                isMaven,
                "java/",
                oracle,
                location,
                new String[] {},
                new NopolContext(),
                "escapeSingleQuote",
                "escapeForwardSlash");
    }
    @Test
    public void PL3() throws InterruptedException {
        Map<String, Object[]> oracle = new HashMap<>();
        oracle.put("org.apache.commons.lang.WordUtilsTest#testAbbreviate3", new Object[]{false});
        oracle.put("org.apache.commons.lang.WordUtilsTest#testAbbreviate4", new Object[]{false});
        oracle.put("org.apache.commons.lang.WordUtilsTest#testAbbreviate5", new Object[]{false});
        oracle.put("org.apache.commons.lang.WordUtilsTest#testAbbreviate6", new Object[]{false});
        oracle.put("org.apache.commons.lang.WordUtilsTest#testAbbreviate7", new Object[]{false});
        oracle.put("org.apache.commons.lang.WordUtilsTest#testAbbreviate8", new Object[]{false});
        oracle.put("org.apache.commons.lang.WordUtilsTest#testAbbreviate9", new Object[]{false});
        oracle.put("org.apache.commons.lang.WordUtilsTest#testAbbreviate10", new Object[]{false});
        oracle.put("org.apache.commons.lang.WordUtilsTest#testAbbreviate11", new Object[]{false});
        oracle.put("org.apache.commons.lang.WordUtilsTest#testAbbreviate12", new Object[]{false});
        oracle.put("org.apache.commons.lang.WordUtilsTest#testAbbreviate13", new Object[]{false});
        oracle.put("org.apache.commons.lang.WordUtilsTest#testAbbreviate14", new Object[]{false});
        oracle.put("org.apache.commons.lang.WordUtilsTest#testAbbreviate15", new Object[]{false});
        oracle.put("org.apache.commons.lang.WordUtilsTest#testAbbreviate16", new Object[]{false});
        oracle.put("org.apache.commons.lang.WordUtilsTest#testAbbreviate17", new Object[]{true});
        oracle.put("org.apache.commons.lang.WordUtilsTest#testAbbreviate18", new Object[]{false});
        oracle.put("org.apache.commons.lang.WordUtilsTest#testAbbreviate19", new Object[]{false});
        oracle.put("org.apache.commons.lang.WordUtilsTest#testAbbreviate20", new Object[]{false});
        oracle.put("org.apache.commons.lang.WordUtilsTest#testAbbreviate21", new Object[]{false});
        oracle.put("org.apache.commons.lang.WordUtilsTest#testAbbreviate22", new Object[]{false});
        oracle.put("org.apache.commons.lang.WordUtilsTest#testAbbreviate23", new Object[]{false});

        SourceLocation location = new SourceLocation("org.apache.commons.lang.WordUtils", 617);
        boolean isMaven = true;
        test("pl3",
                isMaven,
                "java/",
                oracle,
                location,
                new String[] {},
                new NopolContext(),
                "str.length() < lower",
                "((java.lang.String)str).length() < lower",
                "str.length() <= lower");
    }
    @Test
    public void PL4() throws InterruptedException {
        Map<String, Object[]> oracle = new HashMap<>();
        oracle.put("org.apache.commons.lang3.text.translate.NumericEntityUnescaperTest#testOutOfBounds3", new Object[]{true});
        oracle.put("org.apache.commons.lang3.text.translate.NumericEntityUnescaperTest#testOutOfBounds4", new Object[]{true});
        oracle.put("org.apache.commons.lang3.text.translate.NumericEntityUnescaperTest#testUnfinishedEntity", new Object[]{false});

        SourceLocation location = new SourceLocation("org.apache.commons.lang3.text.translate.NumericEntityUnescaper", 52);
        boolean isMaven = true;
        test("pl4",
                isMaven,
                "main/java/",
                oracle,
                location,
                new String[] {},
                new NopolContext(),
                "start == seqEnd",
                "seqEnd == start");
    }

    @Test
    public void PM1() throws InterruptedException {
        Map<String, Object[]> oracle = new HashMap<>();
        oracle.put("org.apache.commons.math.stat.descriptive.SummaryStatisticsTest#testSetterIllegalState", new Object[]{true});
        oracle.put("org.apache.commons.math.stat.descriptive.SummaryStatisticsTest#testSetterInjection1", new Object[]{false});
        oracle.put("org.apache.commons.math.stat.descriptive.SummaryStatisticsTest#testSetterInjection2", new Object[]{false});
        oracle.put("org.apache.commons.math.stat.descriptive.SummaryStatisticsTest#testSetterInjection3", new Object[]{false});
        oracle.put("org.apache.commons.math.stat.descriptive.SummaryStatisticsTest#testSetterInjection4", new Object[]{false});

        oracle.put("org.apache.commons.math.stat.descriptive.moment.GeometricMeanTest#testSetSumOfLog", new Object[]{false});
        oracle.put("org.apache.commons.math.stat.descriptive.moment.GeometricMeanTest#testSetSumOfLogFail", new Object[]{true});

        SourceLocation location = new SourceLocation("org.apache.commons.math.stat.descriptive.moment.GeometricMean", 154);
        boolean isMaven = true;
        test("pm1",
                isMaven,
                "java/",
                oracle,
                location,
                new String[] {"commons-discovery-0.4.jar",
                        "commons-logging-1.1.1.jar"},
                new NopolContext(),
                "0 < getN()", "0 < this.getResult()", "0 <= this.getResult()",
                "0 <= this.sumOfLogs.getResult()",
                "0 < ((org.apache.commons.math.stat.descriptive.moment.GeometricMean)this).getResult()");
    }

    @Test
    public void PM2() throws InterruptedException {
        Map<String, Object[]> oracle = new HashMap<>();
        oracle.put("org.apache.commons.math.exception.util.MessageFactoryTest#testNullSpecific", new Object[]{false});
        oracle.put("org.apache.commons.math.exception.util.MessageFactoryTest#testSpecificGeneral", new Object[]{true});

        SourceLocation location = new SourceLocation("org.apache.commons.math.exception.util.MessageFactory", 87);
        boolean isMaven = true;
        test("pm2",
                isMaven,
                "main/java/",
                oracle,
                location,
                new String[] {},
                new NopolContext(),
                "specific != null",
                "null != specific",
                "0 != sb.length()");
    }

    @Test
    public void AM1() throws InterruptedException {
        Map<String, Object[]> oracle = new HashMap<>();
        oracle.put("org.apache.commons.math3.complex.QuaternionTest#testAccessors3", new Object[]{2.0});

        SourceLocation location = new SourceLocation("org.apache.commons.math3.complex.Quaternion", 94);
        boolean isMaven = true;
        test("am1",
                isMaven,
                "main/java/",
                oracle,
                location,
                new String[] {},
                new NopolContext(),
                "scalar");
    }

    private void test(String projectName, boolean isMaven, String sourceFolder, Map<String, Object[]> oracle, SourceLocation location, String[] dependencies, NopolContext nopolContext, String... patch) {
        String realBugPath = "../../nopol-dataset/";
        String rootFolder = realBugPath + projectName + "/";
        String srcFolder = rootFolder + "src/";
        String binFolder = rootFolder + "bin/";
        if (isMaven) {
            binFolder = rootFolder + "target/classes:" + rootFolder + "target/test-classes";
        }
        String libFolder = rootFolder + "lib/";

        String classpath = binFolder + ":/home/thomas/.m2/repository/junit/junit/4.11/junit-4.11.jar:/home/thomas/.m2/repository/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar:";
        for (int i = 0; i < dependencies.length; i++) {
            classpath += libFolder + dependencies[i];
            if (i < dependencies.length - 1) {
                classpath += ":";
            }
        }
        File[] sourceFiles = { new File(srcFolder + sourceFolder) };
        URL[] classpathURL = JavaLibrary.classpathFrom(classpath);
        String[] tests = oracle.keySet().toArray(new String[0]);
        DynamothCodeGenesis synthesizer = new DynamothCodeGenesisImpl(sourceFiles,location, classpathURL, oracle, tests, nopolContext);
        Candidates expression = synthesizer.run(TimeUnit.MINUTES.toMillis(15));
        check(expression, patch);
    }

    private void check(Candidates expression , String... patch) {
        if(expression == null || expression.size() == 0) {
            Assert.fail("No patch found");
        }
        int position = 0;
        for (Expression o : expression) {
            position++;
            for (String s : patch) {
                if (o.asPatch().equals(s)) {
                    printSummary(o, position);
                    return;
                }
            }
            /*for (int j = 0; j < o.getAlternatives().size(); j++) {
                Expression expression1 = o.getAlternatives().get(j);
                position++;
                for (int k = 0; k < patch.length; k++) {
                    String s = patch[k];
                    if (expression1.asPatch().equals(s)) {
                        printSummary(expression1, position);
                        return;
                    }
                }
            }*/
        }
        Assert.fail("No valid patch in candidates: " + expression.get(0).asPatch());
    }

    private void printSummary(Expression expression, int position) {
        int size  = 1;
        //size += expression.getAlternatives().size();
        System.out.println("# candidate: " + size);
        System.out.println("Candidate: " + expression.asPatch());
        //System.out.println("Alternatives: " + expression.getAlternatives());
        System.out.println("Position: " + position);
    }
}
