package fr.inria.lille.repair.nopol.spoon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;
import xxl.java.library.FileLibrary;

public abstract class ConditionalProcessor extends AbstractProcessor<CtStatement> {

	public abstract CtIf processCondition(CtStatement statement, String newCondition);
	
	public ConditionalProcessor(CtStatement target, String defaultCondition) {
		this.target = target;
		setDefaultCondition(defaultCondition);
	}
	
	@Override
	public boolean isToBeProcessed(CtStatement statement) {
		if (statement.getPosition() != null) {
			return (statement.getPosition().getLine() == target().getPosition().getLine()) &&
				   (statement.getPosition().getColumn() == target().getPosition().getColumn()) &&
				   (FileLibrary.isSameFile(target().getPosition().getFile(), statement.getPosition().getFile()));
		}
		return false;
	}

	public static CtExpression<Boolean> getCondition(CtElement element) {
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
	public void process(CtStatement statement) {
		processCondition(statement, defaultCondition());
	}

	public String defaultCondition() {
		return defaultCondition;
	}
	
	public void setDefaultCondition(String defaultCondition) {
		this.defaultCondition = defaultCondition;
	}
	
	protected CtStatement target() {
		return target;
	}
	
	private CtStatement target;
	private String defaultCondition;	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
}
