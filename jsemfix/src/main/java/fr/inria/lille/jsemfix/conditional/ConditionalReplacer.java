package fr.inria.lille.jsemfix.conditional;

import java.io.File;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.cu.SourcePosition;
import spoon.support.reflect.code.CtLiteralImpl;

/**
 * XXX FIXME TODO duplicated code {@code IfCoConditionalReplacer}
 * 
 * @author Favio D. DeMarco
 */
final class ConditionalReplacer extends AbstractProcessor<CtConditional<Object>> {

	final File file;
	final int line;
	final boolean value;

	/**
	 * @param file
	 * @param line
	 */
	ConditionalReplacer(final File file, final int line, final boolean value) {
		this.file = file;
		this.line = line;
		this.value = value;
	}

	@Override
	public void process(final CtConditional<Object> element) {
		SourcePosition position = element.getPosition();
		if (position.getLine() == this.line && position.getFile().equals(this.file)) {
			// we declare a new snippet of code to be inserted
			CtLiteral<Boolean> snippet = new CtLiteralImpl<>();
			snippet.setFactory(this.getFactory());
			snippet.setValue(this.value);
			element.getCondition().replace(snippet);
		}
	}
}
