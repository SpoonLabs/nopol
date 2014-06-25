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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import sun.nio.ch.FileKey;
import fr.inria.lille.nopol.patch.Patch;
import fr.inria.lille.nopol.synth.DefaultSynthesizer;
import fr.inria.lille.nopol.synth.SynthesizerFactory;
import fr.inria.lille.nopol.synth.smt.SMTExecutionResult;
import fr.inria.lille.nopol.synth.smt.SolverFactory;
import fr.inria.lille.nopol.synth.smt.SolverFactory.Solver;
import fr.inria.lille.nopol.synth.smt.constraint.ConstraintSolver;

/**
 * @author Favio D. DeMarco
 *
 */
public class Main {

	private static int sourceFolderIndex = 0;
	private static int classPathIndex = 1;
	private static int minimalSizeArgs = 2;
	private static long startTime = -1;
	
	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		startTime = System.currentTimeMillis();

		if ( !handleArgs(args) ){
			return;
		}
		
		
		
		File sourceFolder = new File(args[sourceFolderIndex]);
		checkArgument(sourceFolder.exists(), "%s: does not exist.", sourceFolder);
		checkArgument(sourceFolder.isDirectory(), "%s: is not a directory.", sourceFolder);

		// XXX FIXME TODO this line adds the analyzed project classpath for the compiler, it should use another thread
		// with a URLClassLoader, for example.
		// see JDTCompiler.getLibraryAccess()...
		System.setProperty("java.class.path", System.getProperty("java.class.path") + File.pathSeparatorChar + args[classPathIndex]);

		
		
		String[] paths = args[classPathIndex].split(Character.toString(File.pathSeparatorChar));
		
		
		testSolver();
		new Main(sourceFolder, paths).run();
		
		displayResult();
	}
	
	private static void testSolver(){
		InputStream testFile = Main.class.getResourceAsStream("/smt_test");
		File smt_test;
		try {
			smt_test = Files.createTempFile("smt_test", "").toFile();
			OutputStream os = new FileOutputStream(smt_test);
			
			byte[] buffer = new byte[1024];
			int length = testFile.read(buffer);

			while (length > 0) {
				os.write(buffer, 0, length);
				length = testFile.read(buffer);
			}
			
			testFile.close();
			os.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		
		
		if ( !SolverFactory.getCurrentSolver().isWorking(smt_test.getAbsolutePath()) ){
			System.out.println("Solver Issue :\n"
					+ "\tMay doesn't work properly.\n"
					+ "\tMay doesn't exist in location : "+SolverFactory.getCurrentSolver().getBinaryPath()+".\n"
							+ "\tMay doesn't match the defined Solver.");

			System.exit(0);
		}
		smt_test.delete();
	}
	
	private static void displayResult(){
		System.out.println("----Information----");
		System.out.println("Nb Statements Analysed : "+SynthesizerFactory.getNbStatementsAnalysed());
		System.out.println("Nb Statements with Angelic Value Found : "+DefaultSynthesizer.getNbStatementsWithAngelicValue());
		System.out.println("Nb Solver Execution : "+ConstraintSolver.getExecResult().size());
		for ( SMTExecutionResult result : ConstraintSolver.getExecResult() ){
			System.out.println(result);
		}
		for ( Patch patch : NoPol.getPatchList() ){
			System.out.println(patch);
		}
		System.out.println("Total Execution time : "+(System.currentTimeMillis()-startTime)+"ms");
	}

	private static boolean handleArgs(String[] args){
		if (args.length < 1 ) {
			printUsage();
			return false;
		}
		
		
		
		
		for ( String arg : args ){
			if ( arg.equals("-o") || arg.equals("--onebuild") ){
				NoPol.setOneBuild(true);
				increaseDefaultValue();
			}else if ( arg.equals("-m") || arg.equals("--multiplebuild") ){
				NoPol.setOneBuild(false);
				increaseDefaultValue();
			}else if ( arg.startsWith("-solver=")){
				String solver = arg.substring(arg.indexOf("=")+1);
				if ( solver.equals("")){
					System.out.println("Missing Specified SMT Solver name.");
					return false;
				}
				boolean solverChanged = false;
				for ( Solver tmp : SolverFactory.Solver.values() ){
					if ( solver.equals(tmp.toString())){
						SolverFactory.changeSolver(tmp);
						solverChanged = true;
					}
				}
				if ( !solverChanged ){
					System.out.println("Specified SMT Solver is not recognized by Nopol : "+solver+".");
					return false;
				}
				increaseDefaultValue();
			}else if ( arg.startsWith("-solver_path=")){
				String solver_path = arg.substring(arg.indexOf("=")+1);
				if ( solver_path.equals("")){
					System.out.println("Missing Specified SMT Solver path.");
					return false;
				}
				SolverFactory.getCurrentSolver().setBinaryPath(solver_path);
				increaseDefaultValue();
			}else if ( arg.equals("--example") || arg.equals("-ex")){
				runExample();
			}
			
		}
		
		
		if ( minimalSizeArgs > args.length ){
			printUsage();
			return false;
		}
		
		return true;
	}

	
	private static void runExample() {	
		try {
			InputStream[] inputInsideJar = new InputStream[]{
					Main.class.getResourceAsStream("/example/src/nopol_example/NopolExample.java"),
					Main.class.getResourceAsStream("/example/bin/nopol_example/NopolExample.class"),
					Main.class.getResourceAsStream("/example/src/nopol_example/NopolExampleTest.java"),
					Main.class.getResourceAsStream("/example/bin/nopol_example/NopolExampleTest.class")
					};
			
			File tmpFolder = Files.createTempDirectory("NopolExample").toFile();
			File tmpSrc = new File(tmpFolder.getAbsolutePath()+File.separatorChar+"src"+File.separatorChar+"nopol_example");
			File tmpBin = new File(tmpFolder.getAbsolutePath()+File.separatorChar+"bin"+File.separatorChar+"nopol_example");
			tmpSrc.mkdirs();
			tmpBin.mkdirs();
			
			
			File[] outputFile = new File[]{ 
					new File(tmpSrc.getAbsolutePath()+File.separatorChar+"NopolExample.java"), 
					new File(tmpBin.getAbsolutePath()+File.separatorChar+"NopolExample.class"), 
					new File(tmpSrc.getAbsolutePath()+File.separatorChar+"NopolExampleTest.java"), 
					new File(tmpBin.getAbsolutePath()+File.separatorChar+"NopolExampleTest.class") 
					};
			
			for (int i = 0 ; i < 4 ; i++) {
				OutputStream os = new FileOutputStream(outputFile[i]);

				byte[] buffer = new byte[1024];
				int length = inputInsideJar[i].read(buffer);

				while (length > 0) {
					os.write(buffer, 0, length);
					length = inputInsideJar[i].read(buffer);
				}
				inputInsideJar[i].close();
				os.close();
			}
			
			testSolver();
			new Main(tmpSrc.getParentFile(), new String[]{tmpBin.getParent()}).run();
			displayResult();
			
			System.exit(0);
		} catch (IOException  e) {
			throw new RuntimeException(e);
		}
		
		
	}

	private static void increaseDefaultValue(){
		sourceFolderIndex++;
		classPathIndex++;
		minimalSizeArgs++;
	}
	
	
	private static void printUsage() {
		System.out.println("usage : [OPTIONS] <source folder> <binary folder>\n"
				+ "options :\n"
				+ "\t -o, --onebuild, -m, --multiplebuild\n"
				+ "\t\t Set nopol behaviour to build the model only once or not. The onebuild option should optimized time execution but some compilation errors can appears, still experimental.\n"
				+ "\t\t Default value is onebuild\n"
				+ "\n"
				+ "\t-solver=SOLVER\n"
				+ "\t\t Set the solver, for now Nopol can handle two solvers : CVC4 or Z3\n"
				+ "\t\t Default value is Z3\n"
				+ "\n"
				+ "\t-solver_path=PATH\n"
				+ "\t\t Set the solver path to PATH\n"
				+ "\t\t Default location is /usr/bin/SOLVER_NAME\n"
				+ "\n"
				+ "\t-ex, --example\n"
				+ "\t\t Run Nopol with toy example\n");
	}


	private final String[] classpath;

	private final File sourceFolder;

	/**
	 * 
	 */
	private Main(final File sourceFolder, final String[] classpath) {
		this.sourceFolder = checkNotNull(sourceFolder);
		this.classpath = checkNotNull(classpath);
		
		
		
	}

	void run() {

		List<URL> urls = new ArrayList<URL>();
		for (String path : this.classpath) {
			try {
				urls.add(new File(path).toURI().toURL());
			} catch (MalformedURLException e) {
				printUsage();
				throw new RuntimeException(e);
			}
		}
		new NoPol(this.sourceFolder, urls.toArray(new URL[urls.size()])).build();
	}
}
