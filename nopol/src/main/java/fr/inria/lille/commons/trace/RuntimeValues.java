package fr.inria.lille.commons.trace;

import java.util.Map;

import com.google.common.collect.ImmutableSet;

import fr.inria.lille.commons.collections.MapLibrary;
import fr.inria.lille.commons.trace.collector.ValueCollector;

public final class RuntimeValues {

	public static String collectValueInvocation(String variableName) {
		String methodInvocation = String.format(".collectValue(\"%s\", %s);", variableName, variableName);
		return RuntimeValues.class.getName() + methodInvocation + System.lineSeparator();
	}
	

	public static void collectValue(String name, Object value) {
		ValueCollector.collectFrom(name, value, collectedValuesMap());
	}
	
	public static boolean isEmpty() {
		return collectedValuesMap().isEmpty();
	}
	
	public static void discardCollectedValues() {
		collectedValuesMap().clear();
	}

	public static Iterable<Map.Entry<String, Object>> collectedValues() {
		return ImmutableSet.copyOf(collectedValuesMap().entrySet());
	}

	protected static Map<String, Object> collectedValuesMap() {
		if (valuesCache == null) {
			valuesCache = MapLibrary.newHashMap();
		}
		return valuesCache;
	}
	
	private RuntimeValues() {}
	
	private static Map<String, Object> valuesCache;
}