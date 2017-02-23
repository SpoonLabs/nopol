package fr.inria.lille.localization;

import fr.inria.lille.repair.nopol.SourceLocation;

import java.util.List;
import java.util.Map;

/**
 * Created by bdanglot on 9/30/16.
 */
public interface FaultLocalizer {

	Map<SourceLocation, List<TestResult>> getTestListPerStatement();

	List<AbstractStatement> getStatements();
}
