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
package fr.inria.lille.repair.nopol.synth.precondition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;
import fr.inria.lille.repair.nopol.NoPol;
import fr.inria.lille.repair.nopol.synth.ConditionalValueHolder;

/**
 * @author Favio D. DeMarco
 * 
 */
public final class ConditionalAdder extends AbstractProcessor<CtElement> {

	private final String snippet;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public ConditionalAdder(final String variableName) {
		snippet = variableName;
	}

	@Override
	public void process(CtElement element) {
		logger.debug("##### {} ##### Before:\n{}", element, element.getParent());
		CtElement parent = element.getParent();
		CtIf newIf = element.getFactory().Core().createIf();
		CtCodeSnippetExpression<Boolean> condition = element.getFactory().Core().createCodeSnippetExpression();
		
		if ( snippet.equals(ConditionalValueHolder.VARIABLE_NAME) && NoPol.isOneBuild()){
			// Instrumenting
			condition.setValue("!"+ConditionalValueHolder.ENABLE_CONDITIONAL+ConditionalValueHolder.ID_Conditional+"] || ("+snippet+" && "+ConditionalValueHolder.ENABLE_CONDITIONAL+ConditionalValueHolder.ID_Conditional+"])");
			
		}else{
			// Test patch found
			condition.setValue(snippet);
		}
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
