package fr.inria.lille.localization;

import fil.iagl.opl.cocospoon.processors.WatcherProcessor;
import fr.inria.lille.commons.spoon.SpoonedProject;
import fr.inria.lille.repair.common.config.Config;
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
