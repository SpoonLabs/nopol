package fr.inria.lille.repair.synthesis.collect.spoon;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtLiteral;

import java.util.Map;


/**
 * Created by bdanglot on 10/4/16.
 */
public class DefaultConstantCollector extends AbstractProcessor<CtLiteral> {

	private Map<String, Number> constants;

	public DefaultConstantCollector(Map<String, Number> constants) {
		this.constants = constants;
		//default constants
		this.constants.put("-1", -1);
		this.constants.put("0", 0);
		this.constants.put("1", 1);
	}

	@Override
	public boolean isToBeProcessed(CtLiteral candidate) {
		if (candidate.getValue() instanceof Boolean) {
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
