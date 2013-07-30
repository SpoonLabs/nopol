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
package fr.inria.lille.jefix.synth.smt.constraint;

import java.util.Collection;

import org.smtlib.IResponse;
import org.smtlib.IVisitor.VisitorException;
import org.smtlib.sexpr.ISexpr;

import fr.inria.lille.jefix.synth.RepairCandidate;
import fr.inria.lille.jefix.synth.expression.Expression;
import fr.inria.lille.jefix.synth.expression.SimpleExpression;
import fr.inria.lille.jefix.synth.smt.model.Component;
import fr.inria.lille.jefix.synth.smt.model.InputModel;
import fr.inria.lille.jefix.synth.smt.model.ValuesModel;

/**
 * @author Favio D. DeMarco
 *
 */
final class RepairCandidateBuilder {

	private final InputModel model;
	private final IResponse response;

	RepairCandidateBuilder(final InputModel model, final IResponse solverResponse) {
		this.model = model;
		this.response = solverResponse;
	}

	RepairCandidate build() {

		try {
			Iterable<ISexpr> valueList = new SeqToSexprCollectionVisitor().visit(this.response);

			ValuesModel values = this.model.getValues();
			Collection<String> inputValues = values.getInputvalues().keySet();
			Collection<Object> constants = values.getConstants();
			int inputValuesCount = inputValues.size();
			int simpleValuesCount = inputValuesCount + constants.size();
			int linesCount = simpleValuesCount + this.model.getComponents().size();

			Expression[] expressions = new Expression[linesCount];

			this.fillWith(expressions, inputValues, 0);
			this.fillWith(expressions, constants, inputValuesCount);
			this.addOperationsLines(expressions, this.model.getComponents(), simpleValuesCount);

		} catch (VisitorException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}

		return new RepairCandidate("0 != up_sep");
	}

	private void addOperationsLines(final Expression[] expressions, final Iterable<Component> components,
			final int simpleValuesCount) {

	}

	private void fillWith(final Expression[] expressions, final Iterable<?> values,
			final int position) {
		int index = position;
		for (Object value : values) {
			expressions[index] = new SimpleExpression(value.toString());
			index++;
		}
	}
}
