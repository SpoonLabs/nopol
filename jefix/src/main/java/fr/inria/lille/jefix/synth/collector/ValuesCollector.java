/*
 * Copyright (C) 2013 INRIA
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package fr.inria.lille.jefix.synth.collector;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableSet;

import fr.inria.lille.jefix.synth.smt.model.Type;

/**
 * @author Favio D. DeMarco
 * 
 */
public final class ValuesCollector {

	private static final SubValuesCollector ARRAY_COLLECTOR = new ArraySubValuesCollector();

	private static final SubValuesCollector COLLECTION_COLLECTOR = new CollectionSubValuesCollector();
	private static final SubValuesCollector STRING_COLLECTOR = new StringSubValuesCollector();
	private static final Map<String, Object> VALUES = new HashMap<String, Object>();

	public static Object add(final String name, final Object value) {
		boolean isNotNull = null != value;
		addValue(name + "!=null", isNotNull);
		if (isNotNull) {
			addSubValues(name, value);
		}
		if (Type.isOfAKnownType(value)) {
			return addValue(name, value);
		}
		return null;
	}

	private static void addSubValues(final String name, final Object value) {
		if (value.getClass().isArray()) {
			ARRAY_COLLECTOR.addSubValues(name, value);
		} else if (value instanceof Collection) {
			COLLECTION_COLLECTOR.addSubValues(name, value);
		} else if (value instanceof String) {
			STRING_COLLECTOR.addSubValues(name, value);
		}
	}

	public static void clear() {
		VALUES.clear();
	}

	public static Iterable<Map.Entry<String, Object>> getValues() {
		return ImmutableSet.copyOf(VALUES.entrySet());
	}

	public static boolean isEmpty() {
		return VALUES.isEmpty();
	}

	/**
	 * 
	 */
	private ValuesCollector() {}

	public static Object addValue(final String name, final Object value) {
		return VALUES.put(name, value);
	}
}
