package fr.inria.lille;

import fr.inria.lille.repair.nopol.NopolTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        NopolTest.class,
        //SynthesizerTest.class
})
public class TravisTestSuite {
}
