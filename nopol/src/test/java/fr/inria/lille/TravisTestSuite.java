package fr.inria.lille;

import fr.inria.lille.repair.infinitel.InfinitelTest;
import fr.inria.lille.repair.nopol.NopolTest;
import fr.inria.lille.spirals.repair.synthesizer.SynthesizerTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        NopolTest.class,
        SynthesizerTest.class,
        InfinitelTest.class
})
public class TravisTestSuite {
}
