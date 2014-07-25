package fr.inria.lille.commons.trace;

import static java.lang.String.format;

import java.util.Map;

import fr.inria.lille.commons.classes.GlobalToggle;
import fr.inria.lille.commons.collections.MapLibrary;
import fr.inria.lille.commons.trace.collector.ValueCollector;

public class IterationRuntimeValues extends GlobalToggle {

	public static IterationRuntimeValues instance() {
		if (instance == null) {
			instance = new IterationRuntimeValues();
		}
		return instance;
	}
	
	@Override
	public void reset() {
		iterationInputsCache().clear();
	}
	
	@Override
	public String instanceName() {
		return "instance()";
	}
	
	public String collectValueInvocation(String counterName, String variableName) {
		return globallyAccessibleName() + format(".collectValue(%s, \"%s\", %s)", counterName, variableName, variableName);
	}
	
	public void collectValue(int iterationNumber, String variableName, Object value) {
		ValueCollector.collectFrom(variableName, value, inputsFor(iterationNumber));
	}
	
	public int inputsSize() {
		return iterationInputsCache().size();
	}
	
	public Map<String, Object> inputsFor(int iterationNumber) {
		Map<String, Object> newMap = MapLibrary.newHashMap();
		return MapLibrary.getPutIfAbsent(iterationInputsCache(), iterationNumber, newMap);
	}
	
	private Map<Integer, Map<String, Object>> iterationInputsCache() {
		if (iterationInputsCache == null) {
			iterationInputsCache = MapLibrary.newHashMap();
		}
		return iterationInputsCache;
	}
	
	private IterationRuntimeValues() {}
	
	private static IterationRuntimeValues instance;
	private Map<Integer, Map<String, Object>> iterationInputsCache;
}
