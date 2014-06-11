package fr.inria.lille.commons.trace.collector;

import java.util.Collection;
import java.util.Map;

import fr.inria.lille.commons.collections.ListLibrary;


public abstract class ValueCollector {
	
	protected abstract boolean handlesClassOf(Object object);
	
	protected abstract void addValue(String name, Object value, Map<String, Object> storage);
	
	public static void collectFrom(String name, Object value, Map<String, Object> storage) {
		for (ValueCollector collector : collectors()) {
			if (collector.handlesClassOf(value)) {
				collector.addValue(name, value, storage);
			}
		}
	}
	
	private static Collection<ValueCollector> collectors() {
		if (collectors == null) {
			Collection<ValueCollector> allSubclassesInstances = ListLibrary.newArrayList();
			allSubclassesInstances.add(new BooleanCollector());
			allSubclassesInstances.add(new NumberCollector());
			allSubclassesInstances.add(new ArrayCollector());
			allSubclassesInstances.add(new CollectionCollector());
			allSubclassesInstances.add(new CharSequenceCollector());
			allSubclassesInstances.add(new DictionaryCollector());
			allSubclassesInstances.add(new MapCollector());
			allSubclassesInstances.add(new IteratorCollector());
			allSubclassesInstances.add(new EnumerationCollector());
			collectors = allSubclassesInstances;
		}
		return collectors;
	}
	
	private static Collection<ValueCollector> collectors;	
}
