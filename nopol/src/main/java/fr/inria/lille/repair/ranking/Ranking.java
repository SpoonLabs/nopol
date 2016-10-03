package fr.inria.lille.repair.ranking;

import com.gzoltar.core.components.Statement;
import com.gzoltar.core.instr.testing.TestResult;
import fr.inria.lille.localization.StatementExt;
import fr.inria.lille.localization.gzoltar.GZoltarSuspiciousProgramStatements;
import fr.inria.lille.repair.ProjectReference;
import fr.inria.lille.repair.TestClassesFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.List;

public class Ranking {

    public Ranking(File[] sourceFile, URL[] classpath, String[] tests) {
        this(new ProjectReference(sourceFile, classpath, tests));
    }

    public Ranking(final File[] sourceFile, final URL[] classpath) {
        this(new ProjectReference(sourceFile, classpath));
    }

    public Ranking(ProjectReference project) {
        this.classpath = project.classpath();
        this.sourceFile = project.sourceFiles();
        this.testClasses = project.testClasses();

        // get all test classes of the current project
        if (this.testClasses.length == 0) {
            this.testClasses = new TestClassesFinder().findIn(classpath, false);
        }
        // init gzolor
        gZoltar = GZoltarSuspiciousProgramStatements.create(classpath, testClasses);
    }

    public String summary() {
        int nbFailingTest = 0;
        int successfulTests = 0;
        int nbTest = 0;

        List<Statement> suspiciousStatements = gZoltar.sortBySuspiciousness(this.testClasses);


        String output = "";
        output += "/************************/\n";
        output += "/******** Tests *********/\n";
        output += "/************************/\n";
        output += "Executed tests:   " + (nbTest) + "\n";
        output += "Successful tests: " + successfulTests + "\n";
        output += "Failed tests:     " + (nbFailingTest) + "\n\n";

        for (TestResult testResult : gZoltar.getGzoltar().getTestResults()) {
            if (!testResult.wasSuccessful()) {
                output += testResult.getName() + "\n";
                output += testResult.getTrace() + "\n";
            }
        }

        output += "\n/************************/\n";
        output += "/* Suspicious statement */\n";
        output += "/************************/\n";
        for (Statement st : suspiciousStatements) {
            StatementExt statement = (StatementExt) st;
            String cl = statement.getMethod().getParent().getLabel();
            int line = statement.getLineNumber();

            output += String.format(
                    "%s:%d -> %s (ep: %d, ef: %d, np: %d, nf: %d)\n",
                    cl,
                    line,
                    statement.getSuspiciousness(),
                    statement.getEp(),
                    statement.getEf(),
                    statement.getNp(),
                    statement.getNf());
        }
        return output;
    }

    public Collection<Statement> getSuspisiousStatements() {
        // get suspicious statement of the current project
        return gZoltar.sortBySuspiciousness(testClasses);
    }

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private URL[] classpath;
    private final File sourceFile[];
    private final GZoltarSuspiciousProgramStatements gZoltar;
    private String[] testClasses;
}
