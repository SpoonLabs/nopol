package fr.inria.lille.repair.ranking;

import com.gzoltar.core.components.Statement;
import com.gzoltar.core.instr.testing.TestResult;
import fr.inria.lille.localization.AbstractStatement;
import fr.inria.lille.localization.GZoltarFaultLocalizer;
import fr.inria.lille.localization.StatementExt;
import fr.inria.lille.repair.common.finder.TestClassesFinder;
import fr.inria.lille.repair.common.config.NopolContext;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Ranking {

	private URL[] classpath;
	private final GZoltarFaultLocalizer gZoltar;
	private String[] testClasses;

	public Ranking(NopolContext nopolContext) {
		this.classpath = nopolContext.getProjectClasspath();
		this.testClasses = nopolContext.getProjectTests();

		// get all test classes of the current project
		if (this.testClasses.length == 0) {
			this.testClasses = new TestClassesFinder().findIn(classpath, false);
		}
		// init gzoltar
		try {
			gZoltar = new GZoltarFaultLocalizer(nopolContext);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String summary() {
		int nbFailingTest = 0;
		int successfulTests = 0;
		int nbTest = 0;

		List<AbstractStatement> suspiciousStatements = gZoltar.getStatements();


		String output = "";
		output += "/************************/\n";
		output += "/******** Tests *********/\n";
		output += "/************************/\n";
		output += "Executed tests:   " + (nbTest) + "\n";
		output += "Successful tests: " + successfulTests + "\n";
		output += "Failed tests:     " + (nbFailingTest) + "\n\n";

		for (TestResult testResult : gZoltar.getTestResults()) {
			if (!testResult.wasSuccessful()) {
				output += testResult.getName() + "\n";
				output += testResult.getTrace() + "\n";
			}
		}

		output += "\n/************************/\n";
		output += "/* Suspicious statement */\n";
		output += "/************************/\n";
		for (AbstractStatement abstractStatement: suspiciousStatements) {
			StatementExt statement = (StatementExt) abstractStatement;
			String cl = statement.getLabel();
			int line = statement.getLineNumber();

			output += String.format(
					"%s:%d -> %s (ep: %d, ef: %d, np: %d, nf: %d)\n",
					cl,
					line,
					statement.getSuspiciousness());
		}
		return output;
	}

	@Deprecated
	public Collection<Statement> getSuspisiousStatements() {
		// get suspicious statement of the current project
//        return gZoltar.sortBySuspiciousness(testClasses);
		return Collections.EMPTY_LIST;
	}


}
