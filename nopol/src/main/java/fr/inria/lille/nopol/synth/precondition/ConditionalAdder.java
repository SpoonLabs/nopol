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
package fr.inria.lille.nopol.synth.precondition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;
import fr.inria.lille.nopol.synth.Processor;

/**
 * @author Favio D. DeMarco
 * 
 */
public final class ConditionalAdder implements Processor {

	private final String snippet;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public ConditionalAdder(final String variableName) {
		snippet = variableName;
	}

	/**
	 * @see fr.inria.lille.nopol.synth.Processor#process(spoon.reflect.Factory, spoon.reflect.code.CtCodeElement)
	 */
	@Override
	public void process(final Factory factory, final CtElement element) {
		logger.debug("##### {} ##### Before:\n{}", element, element.getParent());
		CtElement parent = element.getParent();
		CtIf newIf = factory.Core().createIf();
		CtCodeSnippetExpression<Boolean> condition = factory.Core().createCodeSnippetExpression();
		condition.setValue(snippet);
		newIf.setCondition(condition);
		// Fix : warning: ignoring inconsistent parent for [CtElem1] ( [CtElem2] != [CtElem3] )
		newIf.setParent(parent);
		element.replace(newIf);
		// this should be after the replace to avoid an StackOverflowException caused by the circular reference.
		// see SpoonStatementPredicate
		newIf.setThenStatement((CtStatement) element);
		// Fix : warning: ignoring inconsistent parent for [CtElem1] ( [CtElem2] != [CtElem3] )
		newIf.getThenStatement().setParent(newIf);
		logger.debug("##### {} ##### After:\n{}", element, element.getParent().getParent());
	}

	
}
