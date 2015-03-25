package fr.inria.lille.repair.nopol.synth;

import static fr.inria.lille.repair.common.patch.Patch.NO_PATCH;

import java.net.URL;
import java.util.List;

import com.gzoltar.core.instr.testing.TestResult;
import fr.inria.lille.repair.common.patch.Patch;
import fr.inria.lille.repair.nopol.spoon.ConditionalProcessor;

public interface Synthesizer {

	static final Synthesizer NO_OP_SYNTHESIZER = new Synthesizer() {
		@Override
		public Patch buildPatch(URL[] classpath, List<TestResult> testClasses, List<TestResult> failures) {
			return NO_PATCH;
		}

		@Override
		public ConditionalProcessor getConditionalProcessor() {
			return null;
		}
	};

	Patch buildPatch(URL[] classpath, List<TestResult> testClasses, List<TestResult> failures);
	
	ConditionalProcessor getConditionalProcessor();
}
