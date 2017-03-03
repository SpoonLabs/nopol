package fr.inria.lille.evo;

import fr.inria.lille.repair.TestUtility;
import fr.inria.lille.repair.common.patch.Patch;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import xxl.java.container.classic.MetaSet;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
		String patchSaveFolder = "src/test/resources/evo/patch";
		String dependencies = "lib/junit-4.11.jar";
		boolean generateTest = false;
		boolean whetherSavePatch=true;
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
		
		Main.solverPath = TestUtility.solverPath;
		Main.tryAllTests(cpClassFolder, cpTestFolder, srcClassFolder, srcTestFolder, destSrcTestFolder, destCpTestFolder, newTestFolder, dependencies, generateTest, testClasses, whetherSavePatch, patchSaveFolder);
		
		for(Map.Entry entry : Main.patches.entrySet()){
			System.out.println(entry.getValue()+" "+entry.getKey());
		}
		assertFalse(Main.patches == null);
		assertFalse(Main.patches.isEmpty());
		assertFalse(Main.patches.get("test_evo_example_generated_0") == null || Main.patches.get("test_evo_example_generated_0").isEmpty());

		//check if we got the rights patches
		//assertEquals("evo_examples.evo_example_1.EvoExample:9: CONDITIONAL number < evo_examples.evo_example_1.EvoExample.this.value", Main.patches.get("basic").get(0).toString());
		fixComparison(Main.patches.get("basic").get(0), "number < evo_examples.evo_example_1.EvoExample.this.value", "number < 1", "number <= 0", "number < 0", "number <= -1");
		fixComparison(Main.patches.get("test_evo_example_generated_0").get(0), "number < 1", "number <= 0", "number < -1", "number <= -1" );
		assertEquals(0, Main.patches.get("test_evo_example_generated_1").size());
		assertEquals(1, Main.keptMethods.size());

		//remove java tests
		FileUtils.deleteDirectory(new File(destSrcTestFolder));
		FileUtils.deleteDirectory(new File(destCpTestFolder));
	}

	private void fixComparison(Patch foundPatch, String... expectedFixes) {
		Collection<String> possibleFixes = MetaSet.newHashSet(expectedFixes);
		assertTrue(foundPatch + " is not a valid patch",
				possibleFixes.contains(foundPatch.asString()));
	}

}
