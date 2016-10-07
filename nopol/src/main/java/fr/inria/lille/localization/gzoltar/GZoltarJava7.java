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

import com.gzoltar.core.GZoltar;
import com.gzoltar.core.agent.AgentCreator;
import com.gzoltar.core.agent.RegistrySingleton;
import com.gzoltar.core.components.Component;
import com.gzoltar.core.components.Statement;
import com.gzoltar.core.diag.SFL;
import com.gzoltar.core.instr.message.IMessage;
import com.gzoltar.core.instr.message.Message;
import com.gzoltar.core.instr.message.Response;
import com.gzoltar.core.instr.testing.TestResult;
import com.gzoltar.core.spectra.Spectra;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.io.File.separator;

/**
 * We need to override run to add JVM parameter "-XX:-UseSplitVerifier" so GZoltar works with Java 7.
 *
 * @author Favio D. DeMarco
 */
@Deprecated
public final class GZoltarJava7 extends GZoltar {

    private static final String JAVA_EXECUTABLE = System.getProperty("java.home") + separator + "bin" + separator
            + "java";
    private static final String PATH_SEPARATOR = File.pathSeparator;
    private static final String RUNNER = "com.gzoltar.core.instr.Runner";

    private transient final File agent;
    private transient Spectra spectra;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public GZoltarJava7() throws IOException {
        this(System.getProperty("user.dir"));
    }

    public GZoltarJava7(final String paramString) throws IOException {
        super(paramString);
        this.agent = AgentCreator.extract(new String[]{"com/gzoltar/core/components", "com/gzoltar/core/instr",
                "com/gzoltar/core/exec", "junit", "org/junit", "org/hamcrest", "org/objectweb/asm"});
    }

    @Override
    public Spectra getSpectra() {
        return this.spectra;
    }

    @Override
    public List<Component> getSuspiciousComponents() {
        return this.spectra.getComponents();
    }

    @Override
    public List<Statement> getSuspiciousStatements() {
        Iterable<Component> components = this.spectra.getComponents();
        List<Statement> localArrayList = new ArrayList<>();
        for (Component localComponent : components) {
            if (localComponent instanceof Statement) {
                localArrayList.add((Statement) localComponent);
            }
        }
        return localArrayList;
    }

    @Override
    public List<TestResult> getTestResults() {
        return this.spectra.getTestResults();
    }

    private Response launch() {
        RegistrySingleton.createSingleton();
        Response localResponse;
        try {
            IMessage message = new Message();
            message.setClassParameters(this.getClassParameters());
            message.setTestParameters(this.getTestParameters());
            String uuid = UUID.randomUUID().toString();
            RegistrySingleton.register(uuid, message);
            StringBuilder classpath = new StringBuilder();
            Iterable<String> classPaths = this.getClasspaths();
            for (String path : classPaths) {
                classpath.append(PATH_SEPARATOR + path);
            }
            classpath.append(PATH_SEPARATOR).append(System.getProperty("java.class.path")).append(PATH_SEPARATOR).append(this.getWorkingDirectory());

            List<String> parameters = new ArrayList<>();
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                parameters.add(JAVA_EXECUTABLE + ".exe");
            } else {
                parameters.add(JAVA_EXECUTABLE);
            }

            // aaall this class to add this line... See http://stackoverflow.com/q/7936006
            parameters.add("-XX:-UseSplitVerifier");

            parameters.add("-javaagent:" + this.agent.getAbsolutePath());
            parameters.add("-cp");
            parameters.add(classpath.toString());
            parameters.add(RUNNER);
            parameters.add(Integer.toString(RegistrySingleton.getPort()));
            parameters.add(uuid);

            ProcessBuilder processBuilder = new ProcessBuilder(parameters);
            processBuilder.directory(new File(this.getWorkingDirectory()));
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            InputStream stream = new BufferedInputStream(process.getInputStream());
            byte[] byteArray = new byte[1024];
            this.logger.debug(">>> Begin subprocess output");
            int i;
            while ((i = stream.read(byteArray)) != -1) {
                this.logger.debug(new String(byteArray, 0, i).replace("\n", ""));
            }
            this.logger.debug("<<< End subprocess output");
            process.waitFor();
            localResponse = message.getResponse();
        } catch (InterruptedException | IOException localException) {
            throw new RuntimeException(localException);
        }
        RegistrySingleton.unregister();
        return localResponse;
    }

    private Response launchAgent() {
        checkNotNull(this.agent);
        return this.launch();
    }

    @Override
    public void run() {
        Response localResponse = this.launchAgent();
        this.spectra = new Spectra();
        this.spectra.registerResults(localResponse.getTestResults());
        SFL.sfl(this.spectra);
    }
}
