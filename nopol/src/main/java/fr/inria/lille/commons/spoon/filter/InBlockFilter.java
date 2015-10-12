package fr.inria.lille.commons.spoon.filter;

import spoon.reflect.code.CtBlock;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.filter.AbstractFilter;

import java.util.Collection;

public class InBlockFilter<T extends CtElement> extends AbstractFilter<T> {

    public InBlockFilter(Class<T> theClass, Collection<CtBlock<?>> blocks) {
        super(theClass);
        this.blocks = blocks;
    }

    @Override
    public boolean matches(T element) {
        CtBlock<?> elementBlock = element.getParent(CtBlock.class);
        if (elementBlock != null) {
            return blocks().contains(elementBlock);
        }
        return false;
    }

    private Collection<CtBlock<?>> blocks() {
        return blocks;
    }

    private Collection<CtBlock<?>> blocks;
}
