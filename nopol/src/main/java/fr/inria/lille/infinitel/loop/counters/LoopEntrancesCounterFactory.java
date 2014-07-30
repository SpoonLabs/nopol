package fr.inria.lille.infinitel.loop.counters;

public class LoopEntrancesCounterFactory extends LoopCounterFactory {

	public static LoopCounterFactory instance() {
		if (instance == null) {
			instance = new LoopEntrancesCounterFactory();
		}
		return instance;
	}
	
	@Override
	public LoopEntrancesCounter newCounter(int counterThreshold) {
		return LoopEntrancesCounter.newInstance(counterThreshold);
	}
	
	private LoopEntrancesCounterFactory() {}
	
	private static LoopEntrancesCounterFactory instance;
}
