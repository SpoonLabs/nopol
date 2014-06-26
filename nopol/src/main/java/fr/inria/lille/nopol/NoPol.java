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
public class NoPol {

	private final URL[] classpath;
	private final GZoltarSuspiciousProgramStatements gZoltar;
	private final SynthesizerFactory synthetizerFactory;
	private final TestPatch testPatch;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final Logger patchLogger = LoggerFactory.getLogger("patch");
	private static List<Patch> patchList = new ArrayList<>();
	private static boolean oneBuild = true;
	private final SpoonClassLoader scl;
	private final File sourceFolder;
	private static boolean singlePatch = true;

	/**
	 * @param rootPackage
	 * @param sourceFolder
	 * @param classpath
	 */
	public NoPol(final File sourceFolder, final URL[] classpath) {
		scl = new SpoonClassLoader();
		this.classpath = classpath;
		gZoltar = GZoltarSuspiciousProgramStatements.create(this.classpath, sourceFolder);
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
		if ( oneBuild ){
			return solveWithOneBuild(statements, testClasses);
		}else{
			return solveWithMultipleBuild(statements, testClasses);
		}
	}
	
	/*
	 * Optimization of Nopol
	 * build
	 * apply all the modifications
	 * try to find all patches
	 */
	private List<Patch> solveWithOneBuild(Collection<SuspiciousStatement> statements, String[] testClasses){
		/*
		 * Build the model
		 */
		try {
			SpoonCompiler builder;
			builder = new Launcher().createCompiler(scl.getFactory());
			builder.addInputSource(sourceFolder);
			builder.build();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		
		List<Synthesizer> generatedSynthesizer = createSynthesizers(statements);
		/*
		 * Create the static array
		 */
		ConditionalValueHolder.createEnableConditionalTab();
		return findPatches(generatedSynthesizer, testClasses);
	}
	
	/*
	 * Apply spoon modification on each statement
	 */
	private List<Synthesizer> createSynthesizers(Collection<SuspiciousStatement> statements){
		List<Synthesizer> synthList = new ArrayList<>();
		for (SuspiciousStatement statement : statements) {
			if ( !statement.getSourceLocation().getContainingClassName().contains("Test")){ // Avoid modification on test cases
				logger.debug("Analysing {}", statement);
				Synthesizer synth = synthetizerFactory.getFor(statement.getSourceLocation());
				synthList.add(synth);					
			}
		}	
		return synthList;
	}
	
	private List<Patch> findPatches(List<Synthesizer> synthList, String[] testClasses){
		/*
		 * Try to synthesis patch
		 */
		for ( Synthesizer synth : synthList ){
			Patch newRepair = synth.buildPatch(classpath, testClasses);
			if (isOk(newRepair, testClasses)) {
				patchList.add(newRepair);
				if ( isSinglePatch() ){
					return patchList;
				}
			}
		}
		return patchList;
	}
	
	
	/*
	 * First algorithm of Nopol,
	 * build the initial model
	 * apply only one modification
	 * build
	 * try to find patch
	 */
	private List<Patch> solveWithMultipleBuild(Collection<SuspiciousStatement> statements, String[] testClasses){
		
		for (SuspiciousStatement statement : statements) {
			if ( !statement.getSourceLocation().getContainingClassName().contains("Test")){ // Avoid modification on test cases
				logger.debug("Analysing {}", statement);
				Synthesizer synth = new SynthesizerFactory(sourceFolder, scl).getFor(statement.getSourceLocation());
				Patch patch = synth.buildPatch(classpath, testClasses);
				if (isOk(patch, testClasses)) {
					patchList.add(patch);
					if ( isSinglePatch() ){
						return patchList;
					}
				}	
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
	
	public static boolean isOneBuild() {
		return oneBuild;
	}
	
	public static boolean setOneBuild(boolean oneBuild) {
		return NoPol.oneBuild = oneBuild;
	}
	
	public static boolean isSinglePatch() {
		return singlePatch;
	}
	
	public static boolean setSinglePatch(boolean singlePatch) {
		return NoPol.singlePatch = singlePatch;
	}
}
