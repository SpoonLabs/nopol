package fr.inria.lille.commons.spoon.filter;

import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.filter.AbstractFilter;
import xxl.java.library.FileLibrary;

public abstract class LocationFilter<T extends CtElement> extends AbstractFilter<T> {

    public LocationFilter(Class<T> theClass, SourcePosition position) {
        super(theClass);
        this.position = position;
    }

    protected SourcePosition position() {
        return position;
    }

    public boolean onTheSameFile(SourcePosition otherPosition) {
        return FileLibrary.isSameFile(position().getFile(), otherPosition.getFile());
    }

    private SourcePosition position;
}
