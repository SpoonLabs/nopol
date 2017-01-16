package fr.inria.lille.repair.nopol.synth;

import fr.inria.lille.commons.trace.Specification;
import fr.inria.lille.localization.TestResult;
import xxl.java.junit.TestCase;

import java.net.URL;
import java.util.Collection;
import java.util.List;

/**
 * Created by spirals on 19/05/15.
 */
public interface AngelicValue<T> {

    /**
     * Collects the runtime value and the angelic value required to pass the test suite
     * @param classpath the classpath of the project
     * @param testClasses the list of test to execute
     * @param failures the list of test that fail
     * @return the collection of runtime values associate to an angelic value
     */
    Collection<Specification<T>> collectSpecifications(final URL[] classpath, List<TestResult> testClasses, final Collection<TestCase> failures);

    //TODO UNUSED
    Collection<Specification<T>> collectSpecifications(final URL[] classpath, final String[] testClasses, final Collection<TestCase> failures);

    boolean isAViablePatch();
}
