package fr.inria.lille.localization;

import com.gzoltar.core.components.Statement;
import com.gzoltar.core.instr.testing.TestResult;
import fr.inria.lille.repair.nopol.SourceLocation;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by bdanglot on 9/30/16.
 */
public interface FaultLocalizer {

	Map<SourceLocation, List<TestResult>> getTestListPerStatement();

	Collection<Statement> getStatements();

}
