package fr.inria.lille.spirals.diff;

import fr.inria.lille.repair.common.config.Config;
import fr.inria.lille.repair.common.patch.ExpressionPatch;
import fr.inria.lille.repair.common.synth.StatementType;
import fr.inria.lille.repair.nopol.SourceLocation;
import fr.inria.lille.spirals.repair.expression.access.LiteralImpl;
import fr.inria.lille.spirals.repair.expression.factory.ValueFactory;
import org.junit.Assert;
import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.factory.Factory;

public class PatchGeneratorTest {

	@Test
	public void simpleConditionChangeTest() {
		Config config = new Config();
		config.setProjectSourcePath(new String[] {"src/test/java/fr/inria/lille/spirals/diff/testclasses"});

		Launcher spoon = new Launcher();
		spoon.addInputResource("src/test/java/fr/inria/lille/spirals/diff/testclasses");
		spoon.buildModel();

		Factory factory = spoon.getFactory();
		SourceLocation pathLocation = new SourceLocation("fr.inria.lille.spirals.diff.testclasses.Bar", 6);
		pathLocation.setSourceStart(91);
		pathLocation.setSourceEnd(106);

		ExpressionPatch patch = new ExpressionPatch(
				new LiteralImpl(ValueFactory.create(false), config),
				pathLocation,
				StatementType.CONDITIONAL);
		PatchGenerator test = new PatchGenerator(
				patch,
				factory, config);

		Assert.assertEquals("--- src/test/java/fr/inria/lille/spirals/diff/testclasses/Bar.java\n"
				+ "+++ src/test/java/fr/inria/lille/spirals/diff/testclasses/Bar.java\n"
				+ "@@ -5,4 +5,4 @@\n"
				+ " \tpublic void m() {\n"
				+ "-\t\tif (true) {\n"
				+ "-\n"
				+ "+\t\tif (false) {\n"
				+ "+\t\t\t\n"
				+ " \t\t}\n", test.getPatch());
	}

	@Test
	public void conditionChangeElseIfTest() {
		Config config = new Config();
		config.setProjectSourcePath(new String[] {"src/test/java/fr/inria/lille/spirals/diff/testclasses"});

		Launcher spoon = new Launcher();
		spoon.addInputResource("src/test/java/fr/inria/lille/spirals/diff/testclasses");
		spoon.buildModel();

		Factory factory = spoon.getFactory();
		SourceLocation pathLocation = new SourceLocation("fr.inria.lille.spirals.diff.testclasses.Bar", 12);
		pathLocation.setSourceStart(133);
		pathLocation.setSourceEnd(148);

		ExpressionPatch patch = new ExpressionPatch(
				new LiteralImpl(ValueFactory.create(false), config),
				pathLocation,
				StatementType.CONDITIONAL);
		PatchGenerator test = new PatchGenerator(
				patch,
				factory, config);

		Assert.assertEquals("--- src/test/java/fr/inria/lille/spirals/diff/testclasses/Bar.java\n"
				+ "+++ src/test/java/fr/inria/lille/spirals/diff/testclasses/Bar.java\n"
				+ "@@ -11,4 +11,4 @@\n"
				+ " \n"
				+ "-\t\t} else if (true) {\n"
				+ "-\n"
				+ "+\t\t} else if (false) {\n"
				+ "+\t\t\t\n"
				+ " \t\t}\n", test.getPatch());
	}


	@Test
	public void simplePreconditionIfTest() {
		Config config = new Config();
		config.setProjectSourcePath(new String[] {"src/test/java/fr/inria/lille/spirals/diff/testclasses"});

		Launcher spoon = new Launcher();
		spoon.addInputResource("src/test/java/fr/inria/lille/spirals/diff/testclasses");
		spoon.buildModel();

		Factory factory = spoon.getFactory();
		SourceLocation pathLocation = new SourceLocation("fr.inria.lille.spirals.diff.testclasses.Bar", 6);
		pathLocation.setSourceStart(91);
		pathLocation.setSourceEnd(106);

		ExpressionPatch patch = new ExpressionPatch(
				new LiteralImpl(ValueFactory.create(false), config),
				pathLocation,
				StatementType.PRECONDITION);
		PatchGenerator test = new PatchGenerator(
				patch,
				factory, config);

		Assert.assertEquals("--- src/test/java/fr/inria/lille/spirals/diff/testclasses/Bar.java\n"
				+ "+++ src/test/java/fr/inria/lille/spirals/diff/testclasses/Bar.java\n"
				+ "@@ -5,4 +5,6 @@\n"
				+ " \tpublic void m() {\n"
				+ "-\t\tif (true) {\n"
				+ "-\n"
				+ "+\t\tif (false) {\n"
				+ "+\t\t\tif (true) {\n"
				+ "+\t\t\t\t\n"
				+ "+\t\t\t}\n"
				+ " \t\t}\n", test.getPatch());
	}

	@Test
	public void preconditionElseIfTest() {
		Config config = new Config();
		config.setProjectSourcePath(new String[] {"src/test/java/fr/inria/lille/spirals/diff/testclasses"});

		Launcher spoon = new Launcher();
		spoon.addInputResource("src/test/java/fr/inria/lille/spirals/diff/testclasses");
		spoon.buildModel();

		Factory factory = spoon.getFactory();
		SourceLocation pathLocation = new SourceLocation("fr.inria.lille.spirals.diff.testclasses.Bar", 12);
		pathLocation.setSourceStart(133);
		pathLocation.setSourceEnd(148);

		ExpressionPatch patch = new ExpressionPatch(
				new LiteralImpl(ValueFactory.create(false), config),
				pathLocation,
				StatementType.PRECONDITION);
		PatchGenerator test = new PatchGenerator(
				patch,
				factory, config);

		Assert.assertEquals("--- src/test/java/fr/inria/lille/spirals/diff/testclasses/Bar.java\n"
				+ "+++ src/test/java/fr/inria/lille/spirals/diff/testclasses/Bar.java\n"
				+ "@@ -11,4 +11,8 @@\n"
				+ " \n"
				+ "-\t\t} else if (true) {\n"
				+ "-\n"
				+ "+\t\t} else {\n"
				+ "+\t\t\tif (false) {\n"
				+ "+\t\t\t\tif (true) {\n"
				+ "+\t\t\t\t\t\n"
				+ "+\t\t\t\t}\n"
				+ "+\t\t\t}\n"
				+ " \t\t}\n", test.getPatch());
	}

	@Test
	public void preconditionInvocationTest() {
		Config config = new Config();
		config.setProjectSourcePath(new String[] {"src/test/java/fr/inria/lille/spirals/diff/testclasses"});

		Launcher spoon = new Launcher();
		spoon.addInputResource("src/test/java/fr/inria/lille/spirals/diff/testclasses");
		spoon.buildModel();

		Factory factory = spoon.getFactory();
		SourceLocation pathLocation = new SourceLocation("fr.inria.lille.spirals.diff.testclasses.Bar", 12);
		pathLocation.setSourceStart(153);
		pathLocation.setSourceEnd(179);

		ExpressionPatch patch = new ExpressionPatch(
				new LiteralImpl(ValueFactory.create(false), config),
				pathLocation,
				StatementType.PRECONDITION);
		PatchGenerator test = new PatchGenerator(
				patch,
				factory, config);

		Assert.assertEquals("--- src/test/java/fr/inria/lille/spirals/diff/testclasses/Bar.java\n"
				+ "+++ src/test/java/fr/inria/lille/spirals/diff/testclasses/Bar.java\n"
				+ "@@ -15,3 +15,5 @@\n"
				+ " \n"
				+ "-\t\tSystem.out.println(\"test\");\n"
				+ "+\t\tif (false) {\n"
				+ "+\t\t\tSystem.out.println(\"test\");\n"
				+ "+\t\t}\n"
				+ " \n", test.getPatch());
	}


	@Test
	public void preconditionMultiLineStatementTest() {
		Config config = new Config();
		config.setProjectSourcePath(new String[] {"src/test/java/fr/inria/lille/spirals/diff/testclasses"});

		Launcher spoon = new Launcher();
		spoon.addInputResource("src/test/java/fr/inria/lille/spirals/diff/testclasses");
		spoon.buildModel();

		Factory factory = spoon.getFactory();
		SourceLocation pathLocation = new SourceLocation("fr.inria.lille.spirals.diff.testclasses.Bar", 13);
		pathLocation.setSourceStart(184);
		pathLocation.setSourceEnd(258);

		ExpressionPatch patch = new ExpressionPatch(
				new LiteralImpl(ValueFactory.create(false), config),
				pathLocation,
				StatementType.PRECONDITION);
		PatchGenerator test = new PatchGenerator(
				patch,
				factory, config);

		Assert.assertEquals("--- src/test/java/fr/inria/lille/spirals/diff/testclasses/Bar.java\n"
				+ "+++ src/test/java/fr/inria/lille/spirals/diff/testclasses/Bar.java\n"
				+ "@@ -17,5 +17,7 @@\n"
				+ " \n"
				+ "-\t\tthrow new RuntimeException(\"FirstLine\" +\n"
				+ "-\t\t\"Second Line\" +\n"
				+ "-\t\t\"Third Line\");\n"
				+ "+\t\tif (false) {\n"
				+ "+\t\t\tthrow new RuntimeException(\"FirstLine\" +\n"
				+ "+\t\t\t\"Second Line\" +\n"
				+ "+\t\t\t\"Third Line\");\n"
				+ "+\t\t}\n"
				+ " \t}\n", test.getPatch());
	}


	@Test
	public void preconditionInvocationInConditionTest() {
		Config config = new Config();
		config.setProjectSourcePath(new String[] {"src/test/java/fr/inria/lille/spirals/diff/testclasses"});

		Launcher spoon = new Launcher();
		spoon.addInputResource("src/test/java/fr/inria/lille/spirals/diff/testclasses");
		spoon.buildModel();

		Factory factory = spoon.getFactory();
		SourceLocation pathLocation = new SourceLocation("fr.inria.lille.spirals.diff.testclasses.Bar", 25);
		pathLocation.setSourceStart(300);
		pathLocation.setSourceEnd(326);

		ExpressionPatch patch = new ExpressionPatch(
				new LiteralImpl(ValueFactory.create(false), config),
				pathLocation,
				StatementType.PRECONDITION);
		PatchGenerator test = new PatchGenerator(
				patch,
				factory, config);

		Assert.assertEquals("--- src/test/java/fr/inria/lille/spirals/diff/testclasses/Bar.java\n"
				+ "+++ src/test/java/fr/inria/lille/spirals/diff/testclasses/Bar.java\n"
				+ "@@ -24,3 +24,5 @@\n"
				+ " \t\tif (true) {\n"
				+ "-\t\t\tSystem.out.println(\"test\");\n"
				+ "+\t\t\tif (false) {\n"
				+ "+\t\t\t\tSystem.out.println(\"test\");\n"
				+ "+\t\t\t}\n"
				+ " \t\t}\n", test.getPatch());
	}
}