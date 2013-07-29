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
package fr.inria.lille.jefix.synth.smt.model;

import java.util.List;

import javax.annotation.Nonnull;

/**
 * @author Favio D. DeMarco
 * 
 */
public final class InputModel {

	private final List<Component> components;

	private final List<Type> inputTypes;

	private final Type outputType;

	private final ValuesModel values;

	/**
	 * @param inputTypes
	 * @param components
	 * @param data
	 */
	public InputModel(@Nonnull final List<Type> inputTypes, @Nonnull final List<Component> components,
			@Nonnull final Type outputType, final ValuesModel data) {
		// XXX FIXME TODO
		// checkArgument(!inputTypes.isEmpty(), "Input types cannot be empty.");
		// checkArgument(!components.isEmpty(), "Components cannot be empty.");
		this.inputTypes = inputTypes;
		this.components = components;
		this.outputType = outputType;
		this.values = data;
	}

	/**
	 * @return the components
	 */
	public List<Component> getComponents() {
		return this.components;
	}

	/**
	 * @return the inputTypes
	 */
	public List<Type> getInputTypes() {
		return this.inputTypes;
	}

	/**
	 * @return the outputType
	 */
	public Type getOutputType() {
		return this.outputType;
	}

	/**
	 * @return the values
	 */
	public ValuesModel getValues() {
		return this.values;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("InputModel [components=%s,%ninputTypes=%s,%noutputType=%s,%nvalues=%s]", this.components,
				this.inputTypes, this.outputType, this.values);
	}
}
