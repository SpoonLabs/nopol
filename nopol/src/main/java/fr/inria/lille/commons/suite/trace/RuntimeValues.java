package fr.inria.lille.commons.suite.trace;

import java.util.Map;

import com.google.common.collect.ImmutableSet;

import fr.inria.lille.commons.collections.MapLibrary;

public final class RuntimeValues {
	
	private RuntimeValues() {}

	public static void collectValue(final String name, final Object value) {
		ValueCollector.collectFrom(name, value, storage());
	}
	
	public static boolean isEmpty() {
		return storage().isEmpty();
	}
	
	public static void discardCollectedValues() {
		storage().clear();
	}

	public static Iterable<Map.Entry<String, Object>> collectedValues() {
		return ImmutableSet.copyOf(storage().entrySet());
	}

	private static Map<String, Object> storage() {
		return valuesCache;
	}
	
	private static Map<String, Object> valuesCache = MapLibrary.newHashMap();
}
