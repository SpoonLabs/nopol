package fr.inria.lille.spirals.repair.synthesizer.collect.spoon;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtThrow;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;

/**
 * Created by bdanglot on 10/4/16.
 */
public class DefaultConstantCollector extends AbstractProcessor<CtLiteral> {


	public DefaultConstantCollector() {
		super();
	}

	@Override
	public boolean isToBeProcessed(CtLiteral candidate) {
		CtMethod parent = candidate.getParent(CtMethod.class);
		if (parent == null) {
			return false;
		}
		return true;
	}

	@Override
	public void process(CtLiteral ctLiteral) {
		if (ctLiteral.getValue() instanceof Boolean) {
			return;
		} else if (ctLiteral.getValue() instanceof Number) {
			if (ctLiteral.getValue().equals(1) ||
					ctLiteral.getValue().equals(0)) {
				return;
			}
		}
		CtElement parent = ctLiteral.getParent(CtLocalVariable.class);
		if (parent != null) {
			return;
		}
		parent = ctLiteral.getParent(CtAssignment.class);
		if (parent != null) {
			return;
		}
		parent = ctLiteral.getParent(CtField.class);
		if (parent != null) {
			return;
		}
		parent = ctLiteral.getParent(CtThrow.class);
		if (parent != null) {
			return;
		}
	}
}
