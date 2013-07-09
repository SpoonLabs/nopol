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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import com.google.common.base.Supplier;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * @author Favio D. DeMarco
 * 
 */
public final class ValuesCollector {

	private enum ListSupplier implements Supplier<List<Object>> {
		INSTANCE;
		@Override
		public List<Object> get() {
			return new ArrayList<>();
		}
	}

	private static final Multimap<String, Object> values = Multimaps.newListMultimap(
			new LinkedHashMap<String, Collection<Object>>(), ListSupplier.INSTANCE);

	public static boolean add(final String name, final Object value) {
		return values.put(name, value);
	}

	public static final Multimap<String, Object> getValues() {
		return values;
	}

	/**
	 * 
	 */
	private ValuesCollector() {}
}
