package fr.inria.lille.repair.nopol.synth;

import fr.inria.lille.localization.TestResult;
import fr.inria.lille.repair.common.patch.Patch;
import fr.inria.lille.repair.nopol.spoon.NopolProcessor;
import xxl.java.junit.TestCase;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public interface Synthesizer {

    Synthesizer NO_OP_SYNTHESIZER = new Synthesizer() {
        @Override
        public List<Patch> buildPatch(URL[] classpath, List<TestResult> testClasses, Collection<TestCase> failures, long maxTimeBuildPatch) {
            return Collections.EMPTY_LIST;
        }

        @Override
        public NopolProcessor getProcessor() {
            return null;
        }
    };

    List<Patch> buildPatch(URL[] classpath, List<TestResult> testClasses, Collection<TestCase> failures, long maxTimeBuildPatch);

    NopolProcessor getProcessor();
}
