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
package fr.inria.lille.nopol.synth.collector;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableSet;

import fr.inria.lille.nopol.synth.smt.model.Type;

/**
 * @author Favio D. DeMarco
 * 
 */
public final class ValuesCollector {

	private static final Map<String, Object> VALUES = new HashMap<String, Object>();

	public static void collectValue(final String name, final Object value) {
		if (Type.isOfAKnownType(value)) {
			addValue(name, value);
		}
	}
	
	/** workaround */
	public static void collectTrue() {
			addValue("true", true);
	}

	public static void collectNullness(final String name, final Object value){
		boolean isNotNull = null != value;
		addValue(name + "!=null", isNotNull);
	
		if (isNotNull) {
			addSubValues(name, value);
		}
	}
	

	private static void addSubValues(final String name, final Object value) {
		SubValuesCollectors.process(name, value);
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
