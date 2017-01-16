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
package fr.inria.lille.repair.nopol.spoon.symbolic;

import com.google.common.base.Predicate;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtTypedElement;

/**
 * @author Thomas Durieux
 */
public enum SpoonDoubleStatement implements Predicate<CtElement> {
    INSTANCE;

    private Class<?> getClassOfStatement(CtElement candidate) {
        CtElement parent = candidate.getParent();
        while (parent != null && !(parent instanceof CtClass<?>)) {
            parent = parent.getParent();
        }
        if (parent != null) {
            return ((CtClass<?>) parent).getActualClass();
        }
        return null;
    }

    @Override
    public boolean apply(final CtElement candidate) {
        SourcePosition position = candidate.getPosition();
        if (position == null || position == SourcePosition.NOPOSITION) {
            return false;
        }

        Class<?> statementClass = getClassOfStatement(candidate);
        if (statementClass == null) {
            return false;
        }
        CtElement parent = candidate.getParent();
        if (parent == null) {
            return false;
        }
        boolean isLocalVariable = candidate instanceof CtLocalVariable<?>;
        boolean isPrimitiveLiteral = false;
        CtTypedElement<?> type = null;
        if (isLocalVariable) {
            type = (CtLocalVariable<?>) candidate;
        } else if (candidate instanceof CtAssignment<?, ?>) {
            type = (CtAssignment<?, ?>) candidate;
        }
        if (type == null) {
            return false;
        }
        Class<?> variableClass = type.getType().getActualClass();
        isPrimitiveLiteral = variableClass.equals(Double.class)
                || variableClass.equals(double.class);
        return isPrimitiveLiteral;
    }
}
