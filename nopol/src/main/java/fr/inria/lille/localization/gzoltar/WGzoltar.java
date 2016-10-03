package fr.inria.lille.localization.gzoltar;

import com.gzoltar.core.GZoltar;
import com.gzoltar.core.components.Statement;
import com.gzoltar.core.instr.testing.TestResult;
import fr.inria.lille.localization.StatementExt;
import fr.inria.lille.localization.metric.Metric;
import fr.inria.lille.localization.metric.Ochiai;

import java.io.IOException;
import java.util.*;

/**
 * GZoltar wrapper, this wrapper supports different metric
 */
public class WGzoltar extends GZoltar {
    private Metric metric;

    public WGzoltar(String wD) throws IOException {
        this(wD, new Ochiai());
    }

    public WGzoltar(String wD, Metric metric) throws IOException {
        super(wD);
        this.metric = metric;
    }

    @Override
    public List<Statement> getSuspiciousStatements() {
        return getSuspiciousStatements(metric);
    }

    private List<Statement> getSuspiciousStatements(Metric metric) {
        List<Statement> suspiciousStatements = super.getSuspiciousStatements();
        List<Statement> result = new ArrayList<>(suspiciousStatements.size());
        int successfulTests;
        int nbFailingTest = 0;
        for (int i = this.getTestResults().size() - 1 ; i >= 0; i--) {
            TestResult testResult = this.getTestResults().get(i);
            if(!testResult.wasSuccessful()) {
                nbFailingTest++;
            }
        }
        successfulTests = this.getTestResults().size() - nbFailingTest;
        for (int i = suspiciousStatements.size() - 1 ; i >= 0; i--) {
            Statement statement = suspiciousStatements.get(i);
            BitSet coverage = statement.getCoverage();
            int executedAndPassedCount = 0;
            int executedAndFailedCount = 0;
            int nextTest = coverage.nextSetBit(0);
            while(nextTest != -1) {
                TestResult testResult = this.getTestResults().get(nextTest);
                if(testResult.wasSuccessful()) {
                    executedAndPassedCount++;
                } else {
                    executedAndFailedCount++;
                }
                nextTest = coverage.nextSetBit(nextTest + 1);
            }
            StatementExt s = new StatementExt(statement, metric);
            s.setEf(executedAndFailedCount);
            s.setEp(executedAndPassedCount);
            s.setNp(successfulTests - executedAndPassedCount);
            s.setNf(nbFailingTest - executedAndFailedCount);
            result.add(s);
        }
        Collections.sort(result, new Comparator<Statement>() {
            @Override
            public int compare(final Statement o1, final Statement o2) {
                // reversed parameters because we want a descending order list
                return Double.compare(o2.getSuspiciousness(), o1.getSuspiciousness());
            }
        });
        return result;
    }
}
