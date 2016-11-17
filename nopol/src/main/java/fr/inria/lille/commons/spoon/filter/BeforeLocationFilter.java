package fr.inria.lille.commons.spoon.filter;

import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;

public class BeforeLocationFilter<T extends CtElement> extends LocationFilter<T> {

    public BeforeLocationFilter(Class<T> theClass, SourcePosition position) {
        super(theClass, position);
    }

    @Override
    public boolean matches(T element) {
        SourcePosition elementPosition = element.getPosition();
        if (elementPosition != null && elementPosition != SourcePosition.NOPOSITION && onTheSameFile(elementPosition)) {
            int distance = position().getLine() - elementPosition.getLine();
            if (distance >= 0) {
                return distance > 0 || position().getColumn() > elementPosition.getColumn();
            }
        }
        return false;
    }
}
