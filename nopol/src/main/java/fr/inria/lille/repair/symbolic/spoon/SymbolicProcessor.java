package fr.inria.lille.repair.symbolic.spoon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtStatement;
import xxl.java.library.FileLibrary;

public abstract class SymbolicProcessor extends AbstractProcessor<CtStatement> {

	public SymbolicProcessor(CtStatement target) {
		this.target = target;
	}

	@Override
	public boolean isToBeProcessed(CtStatement statement) {
		if (statement.getPosition() != null) {
			return (statement.getPosition().getLine() == this.target
					.getPosition().getLine())
					&& (statement.getPosition().getColumn() == this.target
							.getPosition().getColumn())
					&& (FileLibrary.isSameFile(this.target.getPosition()
							.getFile(), statement.getPosition().getFile()));
		}
		return false;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	private Class<?> type;
	protected String defaultValue;
	private String value;
	private final CtStatement target;
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
}
