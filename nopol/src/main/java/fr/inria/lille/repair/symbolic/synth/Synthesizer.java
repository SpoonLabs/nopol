package fr.inria.lille.repair.symbolic.synth;

import static fr.inria.lille.repair.common.patch.Patch.NO_PATCH;

import java.net.URL;
import java.util.Collection;

import xxl.java.junit.TestCase;
import fr.inria.lille.commons.spoon.SpoonedProject;
import fr.inria.lille.repair.common.patch.Patch;
import fr.inria.lille.repair.symbolic.spoon.SymbolicProcessor;

public interface Synthesizer {

	static final Synthesizer NO_OP_SYNTHESIZER = new Synthesizer() {
		@Override
		public Patch buildPatch(URL[] classpath, String[] testClasses, Collection<TestCase> failures,SpoonedProject cleanSpoon, String s) {
			return NO_PATCH;
		}

		@Override
		public SymbolicProcessor getSymbolicProcessor() {
			return null;
		}
	};

	Patch buildPatch(URL[] classpath, String[] testClasses, Collection<TestCase> failures, SpoonedProject cleanSpoon, String mainClass);
	
	SymbolicProcessor getSymbolicProcessor();
}
