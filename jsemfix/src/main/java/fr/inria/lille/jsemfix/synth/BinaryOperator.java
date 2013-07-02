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
package fr.inria.lille.jsemfix.synth;

import org.smtlib.IExpr.IFactory;
import org.smtlib.IExpr.ISymbol;

/**
 * @author Favio D. DeMarco
 * 
 */
public final class BinaryOperator {

	static BinaryOperator createForLine(final int line, final IFactory efactory) {
		return new BinaryOperator(efactory.symbol("I" + line + "-L"), efactory.symbol("LI" + line + "-L"),
				efactory.symbol("I" + line + "-R"), efactory.symbol("LI" + line + "-R"), efactory.symbol("O" + line),
				efactory.symbol("LO" + line));
	}

	private final ISymbol leftInput;

	private final ISymbol leftInputLine;

	private final ISymbol output;

	private final ISymbol outputLine;

	private final ISymbol rightInput;

	private final ISymbol rightInputLine;

	/**
	 * @param leftInput
	 * @param rightInput
	 * @param output
	 */
	private BinaryOperator(final ISymbol leftInput, final ISymbol leftInputLine, final ISymbol rightInput,
			final ISymbol rightInputLine, final ISymbol output, final ISymbol outputLine) {
		this.leftInput = leftInput;
		this.rightInput = rightInput;
		this.output = output;
		this.leftInputLine = leftInputLine;
		this.rightInputLine = rightInputLine;
		this.outputLine = outputLine;
	}

	/**
	 * @return the leftInput
	 */
	public ISymbol getLeftInput() {
		return this.leftInput;
	}

	/**
	 * @return the leftInputLine
	 */
	public ISymbol getLeftInputLine() {
		return this.leftInputLine;
	}

	/**
	 * @return the output
	 */
	public ISymbol getOutput() {
		return this.output;
	}

	/**
	 * @return the outputLine
	 */
	public ISymbol getOutputLine() {
		return this.outputLine;
	}

	/**
	 * @return the rightInput
	 */
	public ISymbol getRightInput() {
		return this.rightInput;
	}

	/**
	 * @return the rightInputLine
	 */
	public ISymbol getRightInputLine() {
		return this.rightInputLine;
	}
}
