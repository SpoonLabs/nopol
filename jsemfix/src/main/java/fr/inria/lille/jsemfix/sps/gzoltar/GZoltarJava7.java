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
package fr.inria.lille.jsemfix.sps.gzoltar;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

/**
 * We need to override run to add JVM parameter "-XX:-UseSplitVerifier" so GZoltar works with Java 7.
 * 
 * @author Favio D. DeMarco
 */
public final class GZoltarJava7 extends GZoltar {

	private static final String DIR_SEPARATOR = System.getProperty("file.separator");
	private static final String JAVA_EXECUTABLE = System.getProperty("java.home") + DIR_SEPARATOR + "bin" + DIR_SEPARATOR
			+ "java";
	private static final String PATH_SEPARATOR = System.getProperty("path.separator");
	private static final String RUNNER = "com.gzoltar.core.instr.Runner";

	private final File agent;
	private Spectra spectra;

	public GZoltarJava7() throws IOException {
		this(System.getProperty("user.dir"));
	}

	public GZoltarJava7(final String paramString) throws IOException {
		super(paramString);
		this.agent = AgentCreator.extract(new String[] { "com/gzoltar/core/components", "com/gzoltar/core/instr",
				"com/gzoltar/core/exec", "junit", "org/junit", "org/hamcrest", "org/objectweb/asm" });
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
			String id = UUID.randomUUID().toString();
			RegistrySingleton.register(id, message);
			StringBuilder localStringBuilder = new StringBuilder(System.getProperty("java.class.path") + PATH_SEPARATOR
					+ this.getWorkingDirectory());
			Iterable<String> classPaths = this.getClasspaths();
			for (String path : classPaths) {
				localStringBuilder.append(PATH_SEPARATOR + path);
			}

			List<String> parameters = new ArrayList<>();
			if (System.getProperty("os.name").toLowerCase().contains("windows") == true) {
				parameters.add(JAVA_EXECUTABLE + ".exe");
			} else {
				parameters.add(JAVA_EXECUTABLE);
			}

			parameters.add("-XX:-UseSplitVerifier"); // aaall this class to add this line... See
			// http://stackoverflow.com/q/7936006

			parameters.add("-javaagent:" + this.agent.getAbsolutePath());
			parameters.add("-cp");
			parameters.add(localStringBuilder.toString());
			parameters.add(RUNNER);
			parameters.add(Integer.toString(RegistrySingleton.getPort()));
			parameters.add(id);

			ProcessBuilder processBuilder = new ProcessBuilder(parameters);
			processBuilder.directory(new File(this.getWorkingDirectory()));
			processBuilder.start().waitFor();
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
