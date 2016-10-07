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

    Collection<Specification<T>> buildFor(final URL[] classpath, List<TestResult> testClasses, final Collection<TestCase> failures);

    //TODO UNUSED
    Collection<Specification<T>> buildFor(final URL[] classpath, final String[] testClasses, final Collection<TestCase> failures);

    boolean isAViablePatch();
}
