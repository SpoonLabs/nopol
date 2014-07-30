package fr.inria.lille.infinitel;

import fr.inria.lille.infinitel.loop.counters.LoopBookkeepingCounterFactory;
import fr.inria.lille.infinitel.loop.counters.LoopCounterFactory;

public class InfinitelDiagnosticianConfiguration extends InfinitelConfiguration {

	public static InfinitelConfiguration instance() {
		if (instance == null) {
			instance = new InfinitelDiagnosticianConfiguration();
		}
		return instance;
	}
	
	@Override
	public Number iterationsThreshold() {
		return 1E7;
	}
	
	@Override
	public LoopCounterFactory counterFactory() {
		return LoopBookkeepingCounterFactory.instance();
	}
	
	protected InfinitelDiagnosticianConfiguration() {}
	
	private static InfinitelDiagnosticianConfiguration instance;
}
