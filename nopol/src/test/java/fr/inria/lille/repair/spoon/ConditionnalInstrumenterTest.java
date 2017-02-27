package fr.inria.lille.repair.spoon;

import fr.inria.lille.commons.spoon.SpoonedClass;
import fr.inria.lille.commons.spoon.SpoonedProject;
import fr.inria.lille.commons.trace.RuntimeValues;
import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.common.synth.StatementType;
import fr.inria.lille.repair.nopol.spoon.ConditionalLoggingInstrumenter;
import fr.inria.lille.repair.nopol.spoon.NopolProcessor;
import fr.inria.lille.repair.nopol.spoon.NopolProcessorBuilder;
import fr.inria.lille.repair.nopol.spoon.dynamoth.ConditionalInstrumenter;
import org.junit.Test;
import spoon.Launcher;
import spoon.processing.Processor;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtTry;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.filter.TypeFilter;
import xxl.java.compiler.DynamicCompilationException;

import java.io.File;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by bdanglot on 9/29/16.
 */
public class ConditionnalInstrumenterTest {

	@Test
	public void testConditionnalInstrumenter() throws Exception {
		File fileClassToSpoon = new File("src/test/resources/spoon/example/Thaliana.java");
		File[] sourceFiles = {fileClassToSpoon};

		NopolContext nopolContext = new NopolContext(sourceFiles, new URL[]{fileClassToSpoon.toURI().toURL()}, null);
		nopolContext.setSynthesis(NopolContext.NopolSynthesis.DYNAMOTH);
		nopolContext.setType(StatementType.CONDITIONAL);

		SpoonedProject spooner = new SpoonedProject(sourceFiles, nopolContext);

		Launcher l = new Launcher();
		l.addInputResource("src/test/resources/spoon/example/Thaliana.java");
		l.buildModel();
		CtIf ifStatement = l.getFactory().Class().get("spoon.example.Thaliana").getElements(new TypeFilter<CtIf>(CtIf.class) {
			@Override
			public boolean matches(CtIf element) {
				return "method".equals(((CtMethod) element.getParent(CtMethod.class)).getSimpleName());
			}
		}).get(0);


		SpoonedClass spoonCl = spooner.forked("spoon.example.Thaliana");
		NopolProcessorBuilder builder = new NopolProcessorBuilder(spoonCl.getSimpleType().getPosition().getFile(), ifStatement.getPosition().getLine(), nopolContext);
		spoonCl.process(builder);
		List<NopolProcessor> nopolProcessors = builder.getNopolProcessors();
		assertEquals(1, nopolProcessors.size());
		NopolProcessor nopolProcessor = nopolProcessors.get(0);
		Processor<CtStatement> processor = new ConditionalInstrumenter(nopolProcessor, nopolContext.getType().getType());
		spoonCl.process(processor);

		CtType<Object> spoonThaliana = spoonCl.spoonFactory().Type().get("spoon.example.Thaliana");

		assertTrue(!(spoonThaliana.getElements(new TypeFilter<CtTry>(CtTry.class) {
			@Override
			public boolean matches(CtTry element) {
				return true;
			}
		}).isEmpty()));

		assertEquals("__NopolProcessorException", spoonThaliana.getElements(new TypeFilter<CtCatch>(CtCatch.class) {
			@Override
			public boolean matches(CtCatch element) {
				return true;
			}
		}).get(0).getParameter().getSimpleName());

		ifStatement = l.getFactory().Class().get("spoon.example.Thaliana").getElements(new TypeFilter<CtIf>(CtIf.class) {
			@Override
			public boolean matches(CtIf element) {
				return "throwingExceptionDueToTheName".equals(((CtMethod) element.getParent(CtMethod.class)).getSimpleName());
			}
		}).get(0);

		spoonCl = spooner.forked("spoon.example.Thaliana");
		builder = new NopolProcessorBuilder(spoonCl.getSimpleType().getPosition().getFile(), ifStatement.getPosition().getLine(), nopolContext);
		spoonCl.process(builder);
		nopolProcessors = builder.getNopolProcessors();
		assertEquals(1, nopolProcessors.size());
		nopolProcessor = nopolProcessors.get(0);
		processor = new ConditionalInstrumenter(nopolProcessor, nopolContext.getType().getType());

		try {
			spoonCl.process(processor);
			fail();
		} catch (DynamicCompilationException exception) {
			assertEquals("Aborting: dynamic compilation failed", exception.getMessage());
		}
	}

	@Test
	public void testConditionnalLoggingInstrumenter() throws Exception {
		File fileClassToSpoon = new File("src/test/resources/spoon/example/Thaliana.java");
		File[] sourceFiles = {fileClassToSpoon};

		NopolContext nopolContext = new NopolContext(sourceFiles, new URL[]{fileClassToSpoon.toURI().toURL()}, null);
		nopolContext.setSynthesis(NopolContext.NopolSynthesis.DYNAMOTH);
		nopolContext.setType(StatementType.CONDITIONAL);

		SpoonedProject spooner = new SpoonedProject(sourceFiles, nopolContext);

		Launcher l = new Launcher();
		l.addInputResource("src/test/resources/spoon/example/Thaliana.java");
		l.buildModel();
		CtIf ifStatement = l.getFactory().Class().get("spoon.example.Thaliana").getElements(new TypeFilter<CtIf>(CtIf.class) {
			@Override
			public boolean matches(CtIf element) {
				return "method".equals(((CtMethod) element.getParent(CtMethod.class)).getSimpleName());
			}
		}).get(0);

		SpoonedClass spoonCl = spooner.forked("spoon.example.Thaliana");
		NopolProcessorBuilder builder = new NopolProcessorBuilder(spoonCl.getSimpleType().getPosition().getFile(), ifStatement.getPosition().getLine(), nopolContext);
		spoonCl.process(builder);
		List<NopolProcessor> nopolProcessors = builder.getNopolProcessors();
		assertEquals(1, nopolProcessors.size());
		NopolProcessor nopolProcessor = nopolProcessors.get(0);
		Processor<CtStatement> processor = new ConditionalLoggingInstrumenter(RuntimeValues.<Boolean>newInstance(), nopolProcessor);
		spoonCl.process(processor);

		CtType<Object> spoonThaliana = spoonCl.spoonFactory().Type().get("spoon.example.Thaliana");

		assertTrue(!(spoonThaliana.getElements(new TypeFilter<CtTry>(CtTry.class) {
			@Override
			public boolean matches(CtTry element) {
				return true;
			}
		}).isEmpty()));

		assertEquals("__NopolProcessorException", spoonThaliana.getElements(new TypeFilter<CtCatch>(CtCatch.class) {
			@Override
			public boolean matches(CtCatch element) {
				return true;
			}
		}).get(0).getParameter().getSimpleName());

		ifStatement = l.getFactory().Class().get("spoon.example.Thaliana").getElements(new TypeFilter<CtIf>(CtIf.class) {
			@Override
			public boolean matches(CtIf element) {
				return "throwingExceptionDueToTheName".equals(((CtMethod) element.getParent(CtMethod.class)).getSimpleName());
			}
		}).get(0);

		spoonCl = spooner.forked("spoon.example.Thaliana");
		builder = new NopolProcessorBuilder(spoonCl.getSimpleType().getPosition().getFile(), ifStatement.getPosition().getLine(), nopolContext);
		spoonCl.process(builder);
		nopolProcessors = builder.getNopolProcessors();
		assertEquals(1, nopolProcessors.size());
		nopolProcessor = nopolProcessors.get(0);
		processor = new ConditionalInstrumenter(nopolProcessor, nopolContext.getType().getType());
		try {
			spoonCl.process(processor);
			fail();
		} catch (DynamicCompilationException exception) {
			assertEquals("Aborting: dynamic compilation failed", exception.getMessage());
		}

	}
}
