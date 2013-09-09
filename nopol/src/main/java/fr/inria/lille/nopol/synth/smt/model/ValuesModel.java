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
package fr.inria.lille.nopol.synth.smt.model;

import static com.google.common.collect.ImmutableList.copyOf;

import java.util.List;

import com.google.common.collect.Multimap;

import fr.inria.lille.nopol.synth.InputOutputValues;

/**
 * @author Favio D. DeMarco
 * 
 */
public final class ValuesModel {

	private final List<Object> constants;

	private final InputOutputValues data;

	// XXX FIXME TODO should be package...
	public ValuesModel(final InputOutputValues data, final List<Object> constants) {
		this.data = data;
		this.constants = copyOf(constants);
	}

	/**
	 * @return the constants
	 */
	public List<Object> getConstants() {
		return this.constants;
	}

	/**
	 * @return the inputvalues
	 */
	public Multimap<String, Object> getInputvalues() {
		return this.data.getInputvalues();
	}

	/**
	 * @return the outputValues
	 */
	public Iterable<Object> getOutputValues() {
		return this.data.getOutputValues();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("ValuesModel [constants=%s,%ndata=%s]", this.constants, this.data);
	}
}
