package fr.inria.lille.repair.symbolic.synth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xxl.java.compiler.DynamicCompilationException;
import fr.inria.lille.commons.spoon.SpoonedClass;
import fr.inria.lille.commons.trace.RuntimeValues;
import fr.inria.lille.repair.nopol.SourceLocation;
import fr.inria.lille.repair.symbolic.spoon.ConditionalAdder;
import fr.inria.lille.repair.symbolic.spoon.ConditionalReplacer;
import fr.inria.lille.repair.symbolic.spoon.LiteralReplacer;
import fr.inria.lille.repair.symbolic.spoon.SymbolicProcessor;

public final class SynthesizerFactory {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final SpoonedClass spooner;

	public SynthesizerFactory(SpoonedClass spoonFork) {
		this.spooner = spoonFork;
	}

	public Synthesizer getFor(SourceLocation statement, StatementType type) {
		// detects the statement type
		StatementTypeDetector detector = new StatementTypeDetector(this.spooner
				.getSimpleType().getPosition().getFile(),
				statement.getLineNumber(), type);
		try{
			spooner.process(detector);
		} catch(DynamicCompilationException e) {
			logger.error(e.getMessage());
			// error during the process ignore the statement
			return Synthesizer.NO_OP_SYNTHESIZER; 
		}

		SymbolicProcessor processor = null;
		switch (detector.getType()) {
		case CONDITIONAL:
			processor = new ConditionalReplacer(detector.statement());
			break;
		case PRECONDITION:
			processor = new ConditionalAdder(detector.statement());
			break;
		case INTEGER_LITERAL:
		case BOOLEAN_LITERAL:
		case DOUBLE_LITERAL:
			processor = new LiteralReplacer(detector.getType().getType(),
					detector.statement());
			break;
		default:
			logger.debug("No synthetizer found for {}.", statement);
			return Synthesizer.NO_OP_SYNTHESIZER;
		}

		if (Boolean.class.equals(detector.getType().getType())) {
			RuntimeValues<Boolean> runtimeValuesInstance = RuntimeValues
					.<Boolean> newInstance();
			JPFRunner<Boolean> constraintModelBuilder = new JPFRunner<Boolean>(
					runtimeValuesInstance, statement, processor, spooner);
			return new DefaultSynthesizer<Boolean>(constraintModelBuilder,
					statement, detector.getType(), processor);
		}
		if (Integer.class.equals(detector.getType().getType())) {
			RuntimeValues<Integer> runtimeValuesInstance = RuntimeValues
					.<Integer> newInstance();
			JPFRunner<Integer> constraintModelBuilder = new JPFRunner<Integer>(
					runtimeValuesInstance, statement, processor, spooner);
			return new DefaultSynthesizer<Integer>(constraintModelBuilder,
					statement, detector.getType(), processor);
		} else if (Double.class.equals(detector.getType().getType())) {
			RuntimeValues<Double> runtimeValuesInstance = RuntimeValues
					.<Double> newInstance();
			JPFRunner<Double> constraintModelBuilder = new JPFRunner<Double>(
					runtimeValuesInstance, statement, processor, spooner);
			return new DefaultSynthesizer<Double>(constraintModelBuilder,
					statement, detector.getType(), processor);
		} else {
			return Synthesizer.NO_OP_SYNTHESIZER;
		}

	}

}
