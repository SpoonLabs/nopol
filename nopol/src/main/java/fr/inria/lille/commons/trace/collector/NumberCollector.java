package fr.inria.lille.commons.trace.collector;

public class NumberCollector extends PrimitiveTypeCollector {

    @Override
    protected Class<?> collectingClass() {
        return Number.class;
    }

}
