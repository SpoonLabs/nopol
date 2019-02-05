package fr.inria.lille.commons.trace.collector;

import xxl.java.container.classic.MetaList;
import xxl.java.container.various.Pair;

import java.util.Collection;
import java.util.Map;


public abstract class ValueCollector {

    protected abstract Class<?> collectingClass();

    protected abstract Collection<Pair<String, Object>> collectedValues(String name, Object value);

    public static void collectFrom(String name, Object value, Map<String, Object> storage) {
        if (!collectWith(primitiveCollectors(), name, value, storage)) {
            boolean isNotNull = value != null;
            storage.put(name + "!=null", isNotNull);
            if (isNotNull) {
                collectWith(classCollectors(), name, value, storage);
            }
        }
    }

    private static boolean collectWith(Collection<ValueCollector> collectors, String name, Object value, Map<String, Object> storage) {
        for (ValueCollector collector : collectors) {
            if (collector.handlesClassOf(value)) {
                Collection<Pair<String, Object>> collected = collector.collectedValues(name, value);
                for (Pair<String, Object> collectedPair : collected) {
                    storage.put(collectedPair.first().intern(), collectedPair.second());
                    /** String.intern() recycles the same string instance from a String Pool */
                }
                return true;
            }
        }
        return false;
    }

    public boolean handlesClassOf(Object value) {
        return collectingClass().isInstance(value);
    }

    private static Collection<ValueCollector> primitiveCollectors() {
        if (primitiveCollectors == null) {
            Collection<ValueCollector> collectorInstances = MetaList.newArrayList();
            collectorInstances.add(new BooleanCollector());
            collectorInstances.add(new NumberCollector());
            collectorInstances.add(new CharacterCollector());
            primitiveCollectors = collectorInstances;
        }
        return primitiveCollectors;
    }

    private static Collection<ValueCollector> classCollectors() {
        if (classCollectors == null) {
            Collection<ValueCollector> collectorInstances = MetaList.newArrayList();
            collectorInstances.add(new ArrayCollector());
            collectorInstances.add(new CollectionCollector());
            collectorInstances.add(new CharSequenceCollector());
            collectorInstances.add(new DictionaryCollector());
            collectorInstances.add(new MapCollector());
            collectorInstances.add(new IteratorCollector());
            collectorInstances.add(new EnumerationCollector());
            classCollectors = collectorInstances;
        }
        return classCollectors;
    }

    private static Collection<ValueCollector> classCollectors;
    private static Collection<ValueCollector> primitiveCollectors;
}
