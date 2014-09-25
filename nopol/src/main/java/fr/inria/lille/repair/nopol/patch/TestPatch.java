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
package fr.inria.lille.repair.nopol.patch;

import java.io.File;

import org.junit.runner.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xxl.java.junit.TestSuiteExecution;
import fr.inria.lille.commons.spoon.SpoonedClass;
import fr.inria.lille.commons.spoon.SpoonedProject;
import fr.inria.lille.repair.nopol.spoon.ConditionalProcessor;

/**
 * @author Favio D. DeMarco
 * 
 */
public final class TestPatch {

	private static final String SPOON_DIRECTORY = File.separator + ".." + File.separator + "spooned";

	private SpoonedProject spoonedProject;
	private final File sourceFolder;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	
	public TestPatch(final File sourceFolder, SpoonedProject spoonedProject) {
		this.sourceFolder = sourceFolder;
		this.spoonedProject = spoonedProject;
	}

	public static String getGeneratedPatchDirectorie(){
		return SPOON_DIRECTORY;
	}
	
	public boolean passesAllTests(Patch patch, String[] testClasses, ConditionalProcessor processor) {
		logger.info("Applying patch: {}", patch);
		String qualifiedName = patch.getRootClassName();
		SpoonedClass spoonedClass = spoonedProject.forked(qualifiedName);
		processor.setDefaultCondition(patch.asString());
		ClassLoader loader = spoonedClass.processedAndDumpedToClassLoader(processor);
		logger.info("Running test suite to check the patch \"{}\" is working", patch.asString());
		Result result = TestSuiteExecution.runCasesIn(testClasses, loader);
		if (result.wasSuccessful()) {
			spoonedClass.generateOutputFile(destinationFolder());
			return true;
		}
		return false;
	}

	private File destinationFolder() {
		return new File(sourceFolder, SPOON_DIRECTORY);
	}
}
