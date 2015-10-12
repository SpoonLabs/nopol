package fr.inria.lille.repair.nopol.synth;

import com.gzoltar.core.instr.testing.TestResult;
import fr.inria.lille.repair.common.patch.Patch;
import fr.inria.lille.repair.nopol.spoon.NopolProcessor;
import xxl.java.junit.TestCase;

import java.net.URL;
import java.util.Collection;
import java.util.List;

import static fr.inria.lille.repair.common.patch.Patch.NO_PATCH;

public interface Synthesizer {

    Synthesizer NO_OP_SYNTHESIZER = new Synthesizer() {
        @Override
        public Patch buildPatch(URL[] classpath, List<TestResult> testClasses, Collection<TestCase> failures, long maxTimeBuildPatch) {
            return NO_PATCH;
        }

        @Override
        public NopolProcessor getProcessor() {
            return null;
        }
    };

    Patch buildPatch(URL[] classpath, List<TestResult> testClasses, Collection<TestCase> failures, long maxTimeBuildPatch);

    NopolProcessor getProcessor();
}
