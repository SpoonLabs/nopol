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
package fr.inria.lille.jefix.synth.precondition;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtLoop;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;

import com.google.common.base.Predicate;

/**
 * @author Favio D. DeMarco
 * 
 */
public enum SpoonStatementPredicate implements Predicate<CtCodeElement> {

	INSTANCE;

	@Override
	public boolean apply(final CtCodeElement input) {
		CtElement parent = input.getParent();
		return input instanceof CtStatement && !(
				// input instanceof CtClass ||

				// cannot insert code before '{}', for example would try to add code between 'Constructor()' and '{}'
				// input instanceof CtBlock ||

				// cannot insert a conditional before 'return', it won't compile.
				// input instanceof CtReturn ||

				// cannot insert a conditional before a variable declaration, it won't compile if the variable is used
				// later on.
				input instanceof CtLocalVariable)

				// Avoids ClassCastException's. @see spoon.support.reflect.code.CtStatementImpl#insertBefore(CtStatement
				// target, CtStatementList<?> statements)
				&& (parent instanceof CtIf || parent instanceof CtLoop || parent instanceof CtCase || parent instanceof CtBlock);
	}
}
