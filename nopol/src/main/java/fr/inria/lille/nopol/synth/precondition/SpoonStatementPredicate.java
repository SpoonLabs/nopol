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

import java.util.List;

import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtLoop;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.filter.TypeFilter;

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
		
		/*
		 * Check if the statement is a Return, if true, check for no existing return in the other branch otherwise it won't compile
		 */
		if ( isInsideIf && isCtReturn){
			CtIf iff = (CtIf) parent.getParent();
			if ( iff.getElseStatement() != null ){
				List<CtReturn<?>> thenReturn = iff.getThenStatement().getElements(new TypeFilter<CtReturn<?>>(CtReturn.class));
				List<CtReturn<?>> elseReturn = iff.getElseStatement().getElements(new TypeFilter<CtReturn<?>>(CtReturn.class));
				if ( thenReturn.contains(input) && !elseReturn.isEmpty() ){
					return false;
				}else if ( elseReturn.contains(input) && !thenReturn.isEmpty() ){
					return false;
				}
			}
		}
		/*
		 * Check if the variable assigned was previously initiliaze, if not, check for no assignment in the other branch, otherwise it won't compile
		 */
		if ( isInsideIf && (input instanceof CtAssignment<? , ?>)){
			CtAssignment<?, ?> assignment = (CtAssignment<?, ?>) input;
			if ( assignment.getAssigned() instanceof CtVariableAccess<?> ){
				CtVariableAccess<?> varAccess = (CtVariableAccess<?>) assignment.getAssigned();
				CtVariableReference<?> var = varAccess.getVariable();
				if (var.getDeclaration() != null) {
					if (var.getDeclaration().getDefaultExpression() == null) {
						/*
						 * variable isn't initialize before this statement
						 */
						CtIf iff = (CtIf) parent.getParent();
						if (iff.getElseStatement() != null) {
							List<CtAssignment<?, ?>> thenAssignment = iff.getThenStatement().getElements(new TypeFilter<CtAssignment<?, ?>>(CtAssignment.class));
							List<CtAssignment<?, ?>> elseAssignment = iff.getElseStatement().getElements(new TypeFilter<CtAssignment<?, ?>>(CtAssignment.class));
							if (thenAssignment.contains(assignment)&& !elseAssignment.isEmpty()) {
								for (CtAssignment<?, ?> tmp : elseAssignment) {
									if (tmp.getAssigned() instanceof CtVariableAccess<?>) {
										if (((CtVariableAccess<?>) (tmp.getAssigned())).getVariable().equals(var)) {
											/*
											 * variable is also assign in the
											 * other branch
											 */
											return false;
										}
									}
								}
							} else if (elseAssignment.contains(assignment)&& !thenAssignment.isEmpty()) {
								for (CtAssignment<?, ?> tmp : thenAssignment) {
									if (tmp.getAssigned() instanceof CtVariableAccess<?>) {
										if (((CtVariableAccess<?>) (tmp.getAssigned())).getVariable().equals(var)) {
											/*
											 * variable is also assign in the
											 * other branch
											 */
											return false;
										}
									}
								}

							}
						}

					}
				}
			}
			
		}
		
		boolean result = isCtStamement 
				// input instanceof CtClass ||

				// cannot insert code before '{}', for example would try to add code between 'Constructor()' and '{}'
				// input instanceof CtBlock ||

				// cannot insert a conditional before 'return', it won't compile. 
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
