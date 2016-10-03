/*
 * Copyright (C) 2013 INRIA
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package fr.inria.lille.localization.gzoltar;

import com.google.common.base.Predicate;
import com.gzoltar.core.GZoltar;
import com.gzoltar.core.components.Statement;
import fr.inria.lille.localization.SuspiciousProgramStatements;
import fr.inria.lille.localization.SuspiciousStatement;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A list of potential bug root-cause.
 *
 * @author Favio D. DeMarco
 */
public final class GZoltarSuspiciousProgramStatements implements SuspiciousProgramStatements {

    @Deprecated //unused
    private enum IsSuspicious implements Predicate<Statement> {
        INSTANCE;

        @Override
        public boolean apply(final Statement input) {
            return input.getSuspiciousness() > 0D;
        }
    }

    /**
     * @param classpath
     * @return
     */
    public static GZoltarSuspiciousProgramStatements create(URL[] classpath, Collection<String> packageNames) {
        return new GZoltarSuspiciousProgramStatements(checkNotNull(classpath), checkNotNull(packageNames));
    }

    /**
     * @param classpath
     * @return
     */
    public static GZoltarSuspiciousProgramStatements create(URL[] classpath, String[] tests) {
        return new GZoltarSuspiciousProgramStatements(checkNotNull(classpath), checkNotNull(Arrays.asList("")));//getRootPackage(tests))));
    }

    @Deprecated //unused
    private static String getRootPackage(String[] classes) {
        String rootPackage = classes[0].substring(0, classes[0].lastIndexOf('.'));
        for (int i = 1; i < classes.length; i++) {
            String aClass = classes[i];
            for (int j = 0; j < aClass.length(); j++) {
                if (j >= rootPackage.length()) {
                    break;
                }
                if (rootPackage.charAt(j) != aClass.charAt(j)) {
                    rootPackage = rootPackage.substring(0, j - 1);
                    break;
                }
            }
        }
        return rootPackage;
    }

    private final GZoltar gzoltar;

    private GZoltarSuspiciousProgramStatements(final URL[] classpath, Collection<String> packageNames) {
        try {
            //gzoltar = new GZoltarJava7();
            gzoltar = new WGzoltar(System.getProperty("user.dir"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ArrayList<String> classpaths = new ArrayList<>();
        for (URL url : classpath) {
            if ("file".equals(url.getProtocol())) {
                classpaths.add(url.getPath());
            } else {
                classpaths.add(url.toExternalForm());
            }
        }

        gzoltar.setClassPaths(classpaths);
        gzoltar.addPackageNotToInstrument("org.junit");
        gzoltar.addPackageNotToInstrument("junit.framework");
        gzoltar.addTestPackageNotToExecute("junit.framework");
        gzoltar.addTestPackageNotToExecute("org.junit");
        for (String packageName : packageNames) {
            gzoltar.addPackageToInstrument(packageName);
        }
    }

    /**
     * @param testClasses
     * @return a ranked list of potential bug root-cause.
     * @see SuspiciousProgramStatements#sortBySuspiciousness(String...)
     */

    public List<Statement> sortBySuspiciousness(final String... testClasses) {

        for (String className : checkNotNull(testClasses)) {
            gzoltar.addTestToExecute(className); // we want to execute the test
            gzoltar.addClassNotToInstrument(className); // we don't want to include the test as root-cause
            // candidate
        }
        gzoltar.run();

        List<Statement> statements = gzoltar.getSuspiciousStatements();

		/*Logger logger = LoggerFactory.getLogger(this.getClass());
		if (logger.isDebugEnabled()) {
			logger.debug("Suspicious statements:\n{}", Joiner.on('\n').join(statements));
		}*/

        return statements;
    }

    public GZoltar getGzoltar() {
        return gzoltar;
    }

    private List<SuspiciousStatement> sortByDescendingOrder(List<SuspiciousStatement> statements) {
        List<SuspiciousStatement> tmp = new ArrayList<>(statements);
        Collections.sort(tmp, new Comparator<SuspiciousStatement>() {
            @Override
            public int compare(final SuspiciousStatement o1, final SuspiciousStatement o2) {
                // reversed parameters because we want a descending order list
                return Double.compare(o2.getSuspiciousness(), o1.getSuspiciousness());
            }
        });
        return tmp;
    }
}
