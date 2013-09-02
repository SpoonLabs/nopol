package fr.inria.lille.jefix.synth;

import static fr.inria.lille.jefix.patch.Patch.NO_PATCH;

import java.net.URL;

import fr.inria.lille.jefix.patch.Patch;

public interface Synthesizer {

	static final Synthesizer NO_OP_SYNTHESIZER = new Synthesizer() {
		@Override
		public Patch buildPatch(final URL[] classpath, final String[] testClasses) {
			return NO_PATCH;
		}
	};

	/**
	 */
	Patch buildPatch(URL[] classpath, String[] testClasses);
}
