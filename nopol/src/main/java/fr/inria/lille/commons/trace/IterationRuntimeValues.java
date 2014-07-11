package fr.inria.lille.commons.trace;

import java.util.Map;

import fr.inria.lille.commons.classes.Toggle;
import fr.inria.lille.commons.collections.MapLibrary;
import fr.inria.lille.commons.trace.collector.ValueCollector;

public class IterationRuntimeValues extends Toggle {

	public static IterationRuntimeValues instance() {
		if (instance == null) {
			instance = new IterationRuntimeValues();
		}
		return instance;
	}
	
	@Override
	public void reset() {
		iterationValuesCache().clear();
	}
	
	public String collectValueInvocation(String counterName, String variableName) {
		String methodInvocation = String.format(".collectValue(%s, \"%s\", %s)", counterName, variableName, variableName);
		return qualifiedInstanceName() + methodInvocation;
	}
	
	public String isEnabledCondition() {
		return qualifiedInstanceName() + ".isEnabled()";
	}
	
	public String qualifiedInstanceName() {
		return getClass().getName() + ".instance()";
	}
	
	public void collectValue(int iterationNumber, String variableName, Object value) {
		ValueCollector.collectFrom(variableName, value, valuesCacheFor(iterationNumber));
	}
	
	public int numberOfIterations() {
		return iterationValuesCache().size();
	}
	
	public Map<String, Object> valuesCacheFor(int iterationNumber) {
		Map<String, Object> newMap = MapLibrary.newHashMap();
		return MapLibrary.getPutIfAbsent(iterationValuesCache(), iterationNumber, newMap);
	}
	
	private Map<Integer, Map<String, Object>> iterationValuesCache() {
		if (iterationValuesCache == null) {
			iterationValuesCache = MapLibrary.newHashMap();
		}
		return iterationValuesCache;
	}
	
	private IterationRuntimeValues() {}
	
	private static IterationRuntimeValues instance;
	private Map<Integer, Map<String, Object>> iterationValuesCache;
}
