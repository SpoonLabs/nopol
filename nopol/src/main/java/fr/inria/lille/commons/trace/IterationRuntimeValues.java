package fr.inria.lille.commons.trace;

import static java.lang.String.format;

import java.util.Map;

import fr.inria.lille.commons.classes.GlobalToggle;
import fr.inria.lille.commons.classes.Singleton;
import fr.inria.lille.commons.collections.Table;
import fr.inria.lille.commons.trace.collector.ValueCollector;

public class IterationRuntimeValues extends GlobalToggle {

	public static IterationRuntimeValues firstInstance() {
		/* Refer to: Singleton#createSingleton() */
		return new IterationRuntimeValues();
	}
	
	public static IterationRuntimeValues instance() {
		return Singleton.of(IterationRuntimeValues.class);
	}
	
	protected IterationRuntimeValues() {}
	
	@Override
	protected void reset() {
		valueTable().clear();
	}
	
	@Override
	public String globallyAccessibleName() {
		return format("%s.instance()", getClass().getName());
	}
	
	public String collectValueInvocation(String counterName, String variableName) {
		return globallyAccessibleName() + format(".collectValue(%s, \"%s\", %s)", counterName, variableName, variableName);
	}
	
	public void collectValue(int iterationNumber, String variableName, Object value) {
		ValueCollector.collectFrom(variableName, value, inputsFor(iterationNumber));
	}
	
	public int inputsSize() {
		return valueTable().numberOfRows();
	}
	
	public Map<String, Object> inputsFor(int iterationNumber) {
		return valueTable().rowCreateIfAbsent(iterationNumber);
	}
	
	private Table<Integer, String, Object> valueTable() {
		if (valueTable == null) {
			valueTable = Table.newTable();
		}
		return valueTable;
	}
	
	private Table<Integer, String, Object> valueTable;
}
