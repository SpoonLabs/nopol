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
package fr.inria.lille.nopol;

import static fr.inria.lille.nopol.patch.Patch.NO_PATCH;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spoon.Launcher;
import spoon.compiler.SpoonCompiler;
import fr.inria.lille.nopol.patch.Patch;
import fr.inria.lille.nopol.sps.SuspiciousStatement;
import fr.inria.lille.nopol.sps.gzoltar.GZoltarSuspiciousProgramStatements;
import fr.inria.lille.nopol.synth.ConditionalValueHolder;
import fr.inria.lille.nopol.synth.Synthesizer;
import fr.inria.lille.nopol.synth.SynthesizerFactory;
import fr.inria.lille.nopol.test.junit.TestClassesFinder;
import fr.inria.lille.nopol.test.junit.TestPatch;

/**
 * @author Favio D. DeMarco
 * 
 */
final class NoPol {

	private final URL[] classpath;
	private final GZoltarSuspiciousProgramStatements gZoltar;
	private final SynthesizerFactory synthetizerFactory;
	private final TestPatch testPatch;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final Logger patchLogger = LoggerFactory.getLogger("patch");
	private static List<Patch> patchList = new ArrayList<>();
	private final SpoonClassLoader scl;
	private final File sourceFolder;

	/**
	 * @param rootPackage
	 * @param sourceFolder
	 * @param classpath
	 */
	public NoPol(final File sourceFolder, final URL[] classpath) {
		scl = new SpoonClassLoader();
		this.classpath = classpath;
		gZoltar = GZoltarSuspiciousProgramStatements.create(this.classpath);
		synthetizerFactory = new SynthesizerFactory(sourceFolder, scl);
		testPatch = new TestPatch(sourceFolder, classpath);
		this.sourceFolder = sourceFolder;
	}

	public List<Patch> build() {
		String[] testClasses = new TestClassesFinder().findIn(classpath, false);			
		
		
		if (testClasses.length == 0) {
			System.out.printf("No test classes found in classpath: %s%n", Arrays.toString(classpath));
			return null;
		}

		Collection<SuspiciousStatement> statements = gZoltar.sortBySuspiciousness(testClasses);

		if (statements.isEmpty()) {
			System.out.println("No suspicious statements found.");
		}

		/*
		 * Build the model only once at the beginning
		 */
		try {
			SpoonCompiler builder;
			builder = new Launcher().createCompiler(scl.getFactory());
			builder.addInputSource(sourceFolder);
			builder.build();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}		
		
		/*
		 * Apply spoon modification on each statement
		 */
		List<Synthesizer> generatedSynthesizer = new ArrayList<>();
		for (SuspiciousStatement statement : statements) {
			if ( !statement.getSourceLocation().getContainingClassName().contains("Test")){ // Avoid modification on test cases
				logger.debug("Analysing {}", statement);
				Synthesizer synth = synthetizerFactory.getFor(statement.getSourceLocation());
				generatedSynthesizer.add(synth);					
			}

		}
		/*
		 * Create the static array
		 */
		ConditionalValueHolder.createEnableConditionalTab();


		
		/*
		 * Try to synthesis patch
		 */
		for ( Synthesizer synth : generatedSynthesizer ){
			Patch newRepair = synth.buildPatch(classpath, testClasses);
			if (isOk(newRepair, testClasses)) {
				patchList.add(newRepair);
			}
		}
		
		
		return patchList;
	}

	private boolean isOk(final Patch newRepair, final String[] testClasses) {
		if (newRepair == NO_PATCH) {
			return false;
		}
		patchLogger.trace("Suggested patch: {}", newRepair);
		return passesAllTests(newRepair, testClasses);
	}

	private boolean passesAllTests(final Patch newRepair, final String[] testClasses) {
		return testPatch.passesAllTests(newRepair, testClasses);
	}

	public static List<Patch> getPatchList() {
		return patchList;
	}
	
	
	
}
