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

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtLoop;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;

import com.google.common.base.Predicate;

/**
 * @author Favio D. DeMarco
 * 
 */
public enum SpoonStatementPredicate implements Predicate<CtElement>{
	INSTANCE;

	@Override
	public boolean apply(final CtElement input) {
		CtElement parent = input.getParent();
		if ( parent == null ){
			return false;
		}
		boolean isConstructorCall = input.toString().contains("super(") || input.toString().contains("this("); // big workaround in order to catch super() call or this() in constructor, could be more efficient
		boolean isCtStamement = input instanceof CtStatement;
		boolean isCtReturn = input instanceof CtReturn;
		boolean isInsideIf = parent.getParent() instanceof CtIf; // Checking parent isn't enough, parent will be CtBlock and grandpa will be CtIf
		boolean isCtLocalVariable = input instanceof CtLocalVariable;
		boolean isInsideIfLoopCaseBlock = (parent instanceof CtIf || parent instanceof CtLoop || parent instanceof CtCase || parent instanceof CtBlock);
		boolean isInsideForUpdate = parent instanceof CtFor ? ((CtFor)(parent)).getForUpdate().contains(input) : false ;
		
		boolean result = isCtStamement 
				// input instanceof CtClass ||

				// cannot insert code before '{}', for example would try to add code between 'Constructor()' and '{}'
				// input instanceof CtBlock ||

				// cannot insert a conditional before 'return', it won't compile. TODO : Or it need to be inside If with no return in the other branch 
				&& !(isCtReturn && !( isInsideIf ))
				// cannot insert a conditional before a variable declaration, it won't compile if the variable is used
				// later on.
				&& !isCtLocalVariable 
				// Avoids ClassCastException's. @see spoon.support.reflect.code.CtStatementImpl#insertBefore(CtStatement
				// target, CtStatementList<?> statements)
				&& isInsideIfLoopCaseBlock
				// cannot insert if inside update statement in for loop declaration
				&& !isInsideForUpdate
				// cannot insert if before super() call in constructor
				&& !isConstructorCall;
		return  result;
	}
}
