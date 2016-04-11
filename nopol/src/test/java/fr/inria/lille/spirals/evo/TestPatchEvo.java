package fr.inria.lille.spirals.evo;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TestPatchEvo {

	@Test
	public void test() throws IOException {
		String cpClassFolder = "../test-projects/target/classes";
		String cpTestFolder = "../test-projects/target/test-classes";
		String srcClassFolder = "../test-projects/src/main/java";
		String srcTestFolder = "../test-projects/src/test/java";
		String destSrcTestFolder = "src/test/resources/evo/destSrcTest";
		String destCpTestFolder = "src/test/resources/evo/destCpTest";
		String newTestFolder = "src/test/resources/evo/generated";
		//String dependencies = "junit-4.11.jar";
		String dependencies = "misc/nopol-example/junit-4.11.jar";
		boolean generateTest = false;
		String[] testClasses = new String[] {"evo_examples.evo_example_1.EvoExampleTest"};

		/*String classPath = cpClassFolder+File.pathSeparator+dependencies;
		Launcher spoon = new Launcher();
		spoon.getEnvironment().setAutoImports(true);
		spoon.addInputResource(newTestFolder);
		spoon.getEnvironment().setSourceClasspath(classPath.split(File.pathSeparator));
		spoon.buildModel();*/
		
		
		//remove old java tests
		FileUtils.deleteDirectory(new File(destSrcTestFolder));
		FileUtils.deleteDirectory(new File(destCpTestFolder));
		
		Main.solverPath = "lib/z3/z3_for_linux";
		Main.tryAllTests(cpClassFolder, cpTestFolder, srcClassFolder, srcTestFolder, destSrcTestFolder, destCpTestFolder, newTestFolder, dependencies, generateTest, testClasses);
		
		for(Map.Entry entry : Main.patches.entrySet()){
			System.out.println(entry.getValue()+" "+entry.getKey());
		}

		//check if we got the rights patches
		assertEquals("evo_examples.evo_example_1.EvoExample:9: CONDITIONAL number < evo_examples.evo_example_1.EvoExample.this.value", Main.patches.get("basic").get(0).toString());
		assertEquals("evo_examples.evo_example_1.EvoExample:9: CONDITIONAL number < 1",Main.patches.get("test_evo_example_generated_0").get(0).toString());
		assertEquals(0,Main.patches.get("test_evo_example_generated_1").size());
		assertEquals(1,Main.keptMethods.size());

		//remove java tests
		FileUtils.deleteDirectory(new File(destSrcTestFolder));
		FileUtils.deleteDirectory(new File(destCpTestFolder));
	}

}
