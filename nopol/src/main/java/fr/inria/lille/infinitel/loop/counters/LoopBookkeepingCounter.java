package fr.inria.lille.infinitel.loop.counters;

import java.util.List;
import java.util.Map;

import fr.inria.lille.commons.collections.ListLibrary;
import fr.inria.lille.commons.collections.MapLibrary;

public class LoopBookkeepingCounter extends LoopEntrancesCounter {

	public static LoopBookkeepingCounter newInstance(Number threshold) {
		int instanceNumber = allInstances().size();
		LoopBookkeepingCounter newInstance = new LoopBookkeepingCounter(threshold, instanceNumber);
		allInstances().add(newInstance);
		return newInstance;
	}
	
	public static LoopBookkeepingCounter instance(int instanceID) {
		return allInstances().get(instanceID);
	}

	@Override
	protected void reset() {
		super.reset();
		recordFrequencies().clear();
	}
	
	@Override
	protected void recordEntrance(int loopEntrances) {
		synchronized (this) {
			super.recordEntrance(loopEntrances);
			recordFrequencies().put(loopEntrances, MapLibrary.getPutIfAbsent(recordFrequencies(), loopEntrances, 0) + 1);
			if (loopEntrances > 0) {
				int previousEntrance = loopEntrances - 1;
				Integer oldValue = recordFrequencies().remove(previousEntrance);
				if (oldValue > 1) {
					recordFrequencies().put(previousEntrance, oldValue - 1);
				}
			}
		}
	}
	
	public Map<Integer, Integer> recordFrequencies() {
		return recordFrequencies;
	}
	
	protected LoopBookkeepingCounter(Number threshold, int instanceNumber) {
		super(threshold, instanceNumber);
		recordFrequencies = MapLibrary.newHashMap();
	}
	
	private static List<LoopBookkeepingCounter> allInstances() {
		if (allInstances == null) {
			allInstances = ListLibrary.newArrayList();
		}
		return allInstances;
	}
	
	private Map<Integer, Integer> recordFrequencies;
	
	/** XXX This causes memory leaks **/
	private static List<LoopBookkeepingCounter> allInstances;
}
