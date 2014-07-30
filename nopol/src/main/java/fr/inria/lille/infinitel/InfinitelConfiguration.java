package fr.inria.lille.infinitel;

import fr.inria.lille.infinitel.loop.counters.LoopCounterFactory;
import fr.inria.lille.infinitel.loop.counters.LoopEntrancesCounterFactory;

public class InfinitelConfiguration {

	public static InfinitelConfiguration instance() {
		if (instance == null) {
			instance = new InfinitelConfiguration();
		}
		return instance;
	}
	
	public Number iterationsThreshold() {
		return 1E6;
	}

	public LoopCounterFactory counterFactory() {
		return LoopEntrancesCounterFactory.instance();
	}
	
	protected InfinitelConfiguration() {}
	
	private static InfinitelConfiguration instance;
}
