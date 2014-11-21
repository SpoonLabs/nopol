package fr.inria.lille.repair.symbolic.synth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.inria.lille.commons.spoon.SpoonedClass;
import fr.inria.lille.commons.trace.RuntimeValues;
import fr.inria.lille.repair.nopol.SourceLocation;
import fr.inria.lille.repair.symbolic.spoon.ConditionalReplacer;
import fr.inria.lille.repair.symbolic.spoon.LiteralReplacer;
import fr.inria.lille.repair.symbolic.spoon.SymbolicProcessor;

public final class SynthesizerFactory{

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final SpoonedClass spooner;
	private static RuntimeValues<Boolean> runtimeValuesInstance = RuntimeValues.newInstance();

	public SynthesizerFactory(SpoonedClass spoonFork) {
		this.spooner = spoonFork;
	}

	public Synthesizer getFor(SourceLocation statement) {
		// detects the statement type  
		StatementTypeDetector detector = new StatementTypeDetector(this.spooner.getSimpleType().getPosition().getFile(), statement.getLineNumber());

		spooner.process(detector);
		
		SymbolicProcessor processor = null;
		switch (detector.getType()) {
			case CONDITIONAL:
				processor = new ConditionalReplacer(detector.statement());
				break;
			case PRECONDITION:
				// processor = new ConditionalAdder(detector.statement());
				// break;
				return Synthesizer.NO_OP_SYNTHESIZER;
			case BOOLEAN_LITERAL:
				processor = new LiteralReplacer(detector.statement());
				break;
			default:
				logger.debug("No synthetizer found for {}.", statement);
				return Synthesizer.NO_OP_SYNTHESIZER;
		}
		JPFRunner constraintModelBuilder = new JPFRunner(runtimeValuesInstance, statement, processor, spooner);
		return new DefaultSynthesizer(constraintModelBuilder, statement, detector.getType(), processor);
	}

}
