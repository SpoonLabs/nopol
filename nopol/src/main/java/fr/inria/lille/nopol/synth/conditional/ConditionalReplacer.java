package fr.inria.lille.nopol.synth.conditional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;
import fr.inria.lille.nopol.NoPol;
import fr.inria.lille.nopol.synth.ConditionalValueHolder;
import fr.inria.lille.nopol.synth.Processor;


/**
 * @author Favio D. DeMarco
 */
public final class ConditionalReplacer implements Processor {

	private final String value;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * @param file
	 * @param line
	 */
	public ConditionalReplacer(final String value) {
		this.value = value;
	}

	/**
	 * @param element
	 * @return
	 */
	private CtExpression<Boolean> getCondition(final CtElement element) {
		CtExpression<Boolean> condition;
		if (element instanceof CtIf) {
			condition = ((CtIf) element).getCondition();
		} else if (element instanceof CtConditional) {
			condition = ((CtConditional<?>) element).getCondition();
		} else {
			throw new IllegalStateException("Unknown conditional class: " + element.getClass());
		}
		return condition;
	}

	@Override
	public void process(final Factory factory, final CtElement element) {
		logger.debug("Replacing:\n{}", element);
		// we declare a new snippet of code to be inserted
		CtCodeSnippetExpression<Boolean> snippet = factory.Core().createCodeSnippetExpression();
		if ( value.equals(ConditionalValueHolder.VARIABLE_NAME ) && NoPol.isOneBuild() ){
			// Instrumenting
			String enable = ConditionalValueHolder.ENABLE_CONDITIONAL+ConditionalValueHolder.ID_Conditional+"]";
			String condition = "("+value+ " && " + enable+")" + " || "+ "(!"+enable+" && ("+ getCondition(element)+"))";
			snippet.setValue(condition);
		}else{
			// Test patch found
			snippet.setValue(value);
		}
		CtExpression<Boolean> condition = getCondition(element);
		condition.replace(snippet);
	}

	
}
