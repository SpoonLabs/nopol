package fr.inria.lille.diff;

import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.common.patch.ExpressionPatch;
import fr.inria.lille.repair.common.synth.StatementType;
import fr.inria.lille.repair.nopol.SourceLocation;
import fr.inria.lille.repair.expression.access.LiteralImpl;
import fr.inria.lille.repair.expression.factory.ValueFactory;
import org.junit.Assert;
import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.factory.Factory;

public class PatchGeneratorTest {

	private static final String projectSourcePath = "src/test/java/fr/inria/lille/diff/testclasses";

	@Test
	public void simpleConditionChangeTest() {
		NopolContext nopolContext = new NopolContext(projectSourcePath, null, null);

		Launcher spoon = new Launcher();
		spoon.addInputResource(projectSourcePath);
		spoon.buildModel();

		Factory factory = spoon.getFactory();
		SourceLocation pathLocation = new SourceLocation("fr.inria.lille.diff.testclasses.Bar", 6);
		pathLocation.setSourceStart(83);
		pathLocation.setSourceEnd(98);

		ExpressionPatch patch = new ExpressionPatch(
				new LiteralImpl(ValueFactory.create(false), nopolContext),
				pathLocation,
				StatementType.CONDITIONAL);
		PatchGenerator test = new PatchGenerator(
				patch,
				factory, nopolContext);

		Assert.assertEquals("--- "+projectSourcePath+"/Bar.java\n"
				+ "+++ "+projectSourcePath+"/Bar.java\n"
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
		NopolContext nopolContext = new NopolContext(projectSourcePath, null, null);

		Launcher spoon = new Launcher();
		spoon.addInputResource(projectSourcePath);
		spoon.buildModel();

		Factory factory = spoon.getFactory();
		SourceLocation pathLocation = new SourceLocation("fr.inria.lille.diff.testclasses.Bar", 12);
		pathLocation.setSourceStart(125);
		pathLocation.setSourceEnd(140);

		ExpressionPatch patch = new ExpressionPatch(
				new LiteralImpl(ValueFactory.create(false), nopolContext),
				pathLocation,
				StatementType.CONDITIONAL);
		PatchGenerator test = new PatchGenerator(
				patch,
				factory, nopolContext);

		Assert.assertEquals("--- "+projectSourcePath+"/Bar.java\n"
				+ "+++ "+projectSourcePath+"/Bar.java\n"
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
		NopolContext nopolContext = new NopolContext(projectSourcePath, null, null);

		Launcher spoon = new Launcher();
		spoon.addInputResource(projectSourcePath);
		spoon.buildModel();

		Factory factory = spoon.getFactory();
		SourceLocation pathLocation = new SourceLocation("fr.inria.lille.diff.testclasses.Bar", 6);
		pathLocation.setSourceStart(83);
		pathLocation.setSourceEnd(98);

		ExpressionPatch patch = new ExpressionPatch(
				new LiteralImpl(ValueFactory.create(false), nopolContext),
				pathLocation,
				StatementType.PRECONDITION);
		PatchGenerator test = new PatchGenerator(
				patch,
				factory, nopolContext);

		Assert.assertEquals("--- "+projectSourcePath+"/Bar.java\n"
				+ "+++ "+projectSourcePath+"/Bar.java\n"
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
		NopolContext nopolContext = new NopolContext(projectSourcePath, null, null);

		Launcher spoon = new Launcher();
		spoon.addInputResource(projectSourcePath);
		spoon.buildModel();

		Factory factory = spoon.getFactory();
		SourceLocation pathLocation = new SourceLocation("fr.inria.lille.diff.testclasses.Bar", 12);
		pathLocation.setSourceStart(125);
		pathLocation.setSourceEnd(140);

		ExpressionPatch patch = new ExpressionPatch(
				new LiteralImpl(ValueFactory.create(false), nopolContext),
				pathLocation,
				StatementType.PRECONDITION);
		PatchGenerator test = new PatchGenerator(
				patch,
				factory, nopolContext);

		Assert.assertEquals("--- "+projectSourcePath+"/Bar.java\n"
				+ "+++ "+projectSourcePath+"/Bar.java\n"
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
		NopolContext nopolContext = new NopolContext(projectSourcePath, null, null);

		Launcher spoon = new Launcher();
		spoon.addInputResource(projectSourcePath);
		spoon.buildModel();

		Factory factory = spoon.getFactory();
		SourceLocation pathLocation = new SourceLocation("fr.inria.lille.diff.testclasses.Bar", 12);
		pathLocation.setSourceStart(145);
		pathLocation.setSourceEnd(171);

		ExpressionPatch patch = new ExpressionPatch(
				new LiteralImpl(ValueFactory.create(false), nopolContext),
				pathLocation,
				StatementType.PRECONDITION);
		PatchGenerator test = new PatchGenerator(
				patch,
				factory, nopolContext);

		Assert.assertEquals("--- "+projectSourcePath+"/Bar.java\n"
				+ "+++ "+projectSourcePath+"/Bar.java\n"
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
		NopolContext nopolContext = new NopolContext(projectSourcePath, null, null);

		Launcher spoon = new Launcher();
		spoon.addInputResource(projectSourcePath);
		spoon.buildModel();

		Factory factory = spoon.getFactory();
		SourceLocation pathLocation = new SourceLocation("fr.inria.lille.diff.testclasses.Bar", 13);
		pathLocation.setSourceStart(176);
		pathLocation.setSourceEnd(250);

		ExpressionPatch patch = new ExpressionPatch(
				new LiteralImpl(ValueFactory.create(false), nopolContext),
				pathLocation,
				StatementType.PRECONDITION);
		PatchGenerator test = new PatchGenerator(
				patch,
				factory, nopolContext);

		Assert.assertEquals("--- "+projectSourcePath+"/Bar.java\n"
				+ "+++ "+projectSourcePath+"/Bar.java\n"
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
		NopolContext nopolContext = new NopolContext(projectSourcePath, null, null);

		Launcher spoon = new Launcher();
		spoon.addInputResource(projectSourcePath);
		spoon.buildModel();

		Factory factory = spoon.getFactory();
		SourceLocation pathLocation = new SourceLocation("fr.inria.lille.diff.testclasses.Bar", 25);
		pathLocation.setSourceStart(292);
		pathLocation.setSourceEnd(318);

		ExpressionPatch patch = new ExpressionPatch(
				new LiteralImpl(ValueFactory.create(false), nopolContext),
				pathLocation,
				StatementType.PRECONDITION);
		PatchGenerator test = new PatchGenerator(
				patch,
				factory, nopolContext);

		Assert.assertEquals("--- "+projectSourcePath+"/Bar.java\n"
				+ "+++ "+projectSourcePath+"/Bar.java\n"
				+ "@@ -24,3 +24,5 @@\n"
				+ " \t\tif (true) {\n"
				+ "-\t\t\tSystem.out.println(\"test\");\n"
				+ "+\t\t\tif (false) {\n"
				+ "+\t\t\t\tSystem.out.println(\"test\");\n"
				+ "+\t\t\t}\n"
				+ " \t\t}\n", test.getPatch());
	}

	@Test
	public void preconditionInvocationInElseTest() {
		NopolContext nopolContext = new NopolContext(projectSourcePath, null, null);

		Launcher spoon = new Launcher();
		spoon.addInputResource(projectSourcePath);
		spoon.buildModel();

		Factory factory = spoon.getFactory();
		SourceLocation pathLocation = new SourceLocation("fr.inria.lille.diff.testclasses.Bar", 34);
		pathLocation.setSourceStart(408);
		pathLocation.setSourceEnd(435);

		ExpressionPatch patch = new ExpressionPatch(
				new LiteralImpl(ValueFactory.create(false), nopolContext),
				pathLocation,
				StatementType.PRECONDITION);
		PatchGenerator test = new PatchGenerator(
				patch,
				factory, nopolContext);

		Assert.assertEquals("--- "+projectSourcePath+"/Bar.java\n"
				+ "+++ "+projectSourcePath+"/Bar.java\n"
				+ "@@ -33,3 +33,5 @@\n"
				+ " \t\t\tSystem.out.println(\"test1\");\n"
				+ "-\t\t\tSystem.out.println(\"test2\");\n"
				+ "+\t\t\tif (false) {\n"
				+ "+\t\t\t\tSystem.out.println(\"test2\");\n"
				+ "+\t\t\t}\n"
				+ " \t\t}\n", test.getPatch());
	}
}