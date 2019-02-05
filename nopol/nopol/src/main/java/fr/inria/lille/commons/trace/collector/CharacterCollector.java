package fr.inria.lille.commons.trace.collector;

public class CharacterCollector extends PrimitiveTypeCollector {

    @Override
    protected Class<?> collectingClass() {
        return Character.class;
    }

}
