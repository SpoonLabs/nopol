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
package fr.inria.lille.jsemfix.conditional;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Favio D. DeMarco
 * 
 */
public final class ValuesCollector {

	private static final Map<String, Object> values = new HashMap<String, Object>();

	public static Object add(final String name, final Object value) {
		return values.put(name, value);
	}

	public static void clear() {
		values.clear();
	}

	public static Iterable<Map.Entry<String, Object>> getValues() {
		return values.entrySet();
	}

	/**
	 * 
	 */
	private ValuesCollector() {}
}
