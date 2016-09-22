package fr.inria.lille.repair.nopol.synth;

import com.gzoltar.core.instr.testing.TestResult;
import fr.inria.lille.commons.trace.Specification;
import xxl.java.junit.TestCase;

import java.net.URL;
import java.util.Collection;
import java.util.List;

/**
 * Created by spirals on 19/05/15.
 */
public interface AngelicValue<T> {

	Collection<Specification<T>> buildFor(final URL[] classpath, List<TestResult> testClasses, final Collection<TestCase> failures);

	Collection<Specification<T>> buildFor(final URL[] classpath, final String[] testClasses, final Collection<TestCase> failures);

	boolean isAViablePatch();
}
