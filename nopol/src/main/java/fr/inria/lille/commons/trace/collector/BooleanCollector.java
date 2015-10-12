package fr.inria.lille.commons.trace.collector;

public class BooleanCollector extends PrimitiveTypeCollector {

    @Override
    protected Class<?> collectingClass() {
        return Boolean.class;
    }

}
