package fr.inria.lille.commons.trace;

import java.util.Map;

import com.google.common.collect.ImmutableSet;

import fr.inria.lille.commons.collections.MapLibrary;
import fr.inria.lille.commons.trace.collector.ValueCollector;

public class RuntimeValues {

	public static String collectValueInvocation(String variableName) {
		String methodInvocation = String.format(".collectValue(\"%s\", %s)", variableName, variableName);
		return RuntimeValues.class.getName() + methodInvocation;
	}
	
	public static void collectValue(String variableName, Object value) {
		ValueCollector.collectFrom(variableName, value, valuesCache());
	}
	
	public static Iterable<Map.Entry<String, Object>> collectedValues() {
		return ImmutableSet.copyOf(valuesCache().entrySet());
	}
	
	public static boolean isEmpty() {
		return valuesCache().isEmpty();
	}
	
	public static void discardCollectedValues() {
		valuesCache().clear();
	}

	private static Map<String, Object> valuesCache() {
		if (valuesCache == null) {
			valuesCache = MapLibrary.newHashMap();
		}
		return valuesCache;
	}
	
	private RuntimeValues() {}
	
	private static Map<String, Object> valuesCache;
}