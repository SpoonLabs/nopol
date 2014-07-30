package fr.inria.lille.infinitel.loop.counters;

public class LoopBookkeepingCounterFactory extends LoopCounterFactory {

	public static LoopCounterFactory instance() {
		if (instance == null) {
			instance = new LoopBookkeepingCounterFactory();
		}
		return instance;
	}
	
	@Override
	public LoopEntrancesCounter newCounter(int counterThreshold) {
		return LoopBookkeepingCounter.newInstance(counterThreshold);
	}
	
	private LoopBookkeepingCounterFactory() {}
	
	private static LoopBookkeepingCounterFactory instance;	
}
