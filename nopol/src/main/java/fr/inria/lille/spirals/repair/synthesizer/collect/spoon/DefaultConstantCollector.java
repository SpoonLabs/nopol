package fr.inria.lille.spirals.repair.synthesizer.collect.spoon;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtThrow;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;

import java.util.Map;


/**
 * Created by bdanglot on 10/4/16.
 */
public class DefaultConstantCollector extends AbstractProcessor<CtLiteral> {

	private Map<String, Number> constants;

	public DefaultConstantCollector(Map<String, Number> constants) {
		this.constants = constants;
		//default constants
		constants.put("-1", -1);
		constants.put("0", 0);
		constants.put("1", 1);
	}

	@Override
	public boolean isToBeProcessed(CtLiteral candidate) {
		CtMethod parent = candidate.getParent(CtMethod.class);
		if (parent == null) {
			return false;
		}

		if (candidate.getValue() instanceof Boolean) {
			return false;
		} else if (candidate.getValue() instanceof Number) {// 3 defaults value will be added anyway
			if (candidate.getValue().equals(1) ||
					candidate.getValue().equals(0) ||
					candidate.getValue().equals(-1)) {
				return false;
			}
		}

		if (candidate.getParent(CtLocalVariable.class) != null) {
			return false;
		}
		if (candidate.getParent(CtAssignment.class) != null) {
			return false;
		}
		if (candidate.getParent(CtField.class) != null) {
			return false;
		}
		if (candidate.getParent(CtThrow.class) != null) {
			return false;
		}

		Object value = candidate.getValue();
		if (value == null) {
			return false;
		}

		//Processing only Long and Integer
		return Long.class.isAssignableFrom(value.getClass()) ||
				Integer.class.isAssignableFrom(value.getClass());
	}

	@Override
	public void process(CtLiteral ctLiteral) {
		Object value = ctLiteral.getValue();
		constants.put(value.toString(), (Number) value);
	}
}
