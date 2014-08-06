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
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.inria.lille.commons.compiler.DynamicCompilationException;
import fr.inria.lille.commons.spoon.SpoonClassLoaderBuilder;
import fr.inria.lille.commons.utils.TestClassesFinder;
import fr.inria.lille.nopol.patch.Patch;
import fr.inria.lille.nopol.patch.TestPatch;
import fr.inria.lille.nopol.sps.SuspiciousStatement;
import fr.inria.lille.nopol.sps.gzoltar.GZoltarSuspiciousProgramStatements;
import fr.inria.lille.nopol.synth.ConditionalValueHolder;
import fr.inria.lille.nopol.synth.Synthesizer;
import fr.inria.lille.nopol.synth.SynthesizerFactory;

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
	private final SpoonClassLoaderBuilder spooner;
	private final File sourceFolder;
	private static boolean singlePatch = true;

	/**
	 * @param rootPackage
	 * @param sourceFolder
	 * @param classpath
	 */
	public NoPol(final File sourceFolder, final URL[] classpath) {
		spooner = new SpoonClassLoaderBuilder(sourceFolder);
		this.classpath = classpath;
		gZoltar = GZoltarSuspiciousProgramStatements.create(this.classpath, sourceFolder);
		synthetizerFactory = new SynthesizerFactory(sourceFolder, spooner);
		testPatch = new TestPatch(sourceFolder, classpath);
		this.sourceFolder = sourceFolder;
	}

	public List<Patch> build() {
		String[] testClasses = new TestClassesFinder().findIn(classpath, false);
		return build(testClasses);
	}
	
	public List<Patch> build(String[] testClasses) {
		Collection<SuspiciousStatement> statements = gZoltar.sortBySuspiciousness(testClasses);
		if (statements.isEmpty()) {
			System.out.println("No suspicious statements found.");
		}
		List<Patch> patches;
		if ( oneBuild ){
			patches = solveWithOneBuild(statements, testClasses);
		}else{
			patches = solveWithMultipleBuild(statements, testClasses);
		}
		getPatchList().addAll(patches);
		return patches;
	}
	
	/*
	 * Optimization of Nopol
	 * build
	 * apply all the modifications
	 * try to find all patches
	 */
	private List<Patch> solveWithOneBuild(Collection<SuspiciousStatement> statements, String[] testClasses){
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
		List<Patch> patches = new ArrayList<Patch>();
		for ( Synthesizer synth : synthList ){
			try {
				Patch newRepair = synth.buildPatch(classpath, testClasses);
				if (isOk(newRepair, testClasses)) {
					patches.add(newRepair);
					if ( isSinglePatch() ){
						break;
					}
				}
			}
			catch (DynamicCompilationException dce) {}
		}
		return patches;
	}
	
	
	/*
	 * First algorithm of Nopol,
	 * build the initial model
	 * apply only one modification
	 * build
	 * try to find patch
	 */
	private List<Patch> solveWithMultipleBuild(Collection<SuspiciousStatement> statements, String[] testClasses){
		List<Patch> patches = new ArrayList<Patch>();
		for (SuspiciousStatement statement : statements) {
			try {
				if ( !statement.getSourceLocation().getContainingClassName().contains("Test")){ // Avoid modification on test cases
					logger.debug("Analysing {}", statement);
					Synthesizer synth = new SynthesizerFactory(sourceFolder, spooner).getFor(statement.getSourceLocation());
					Patch patch = synth.buildPatch(classpath, testClasses);
					if (isOk(patch, testClasses)) {
						patches.add(patch);
						if ( isSinglePatch() ){
							break;
						}
					}	
				}
			}
			catch (DynamicCompilationException dce) {}
		}
		return patches;
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
