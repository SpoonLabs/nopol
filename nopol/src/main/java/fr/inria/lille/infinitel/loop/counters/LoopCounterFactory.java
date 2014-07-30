package fr.inria.lille.infinitel.loop.counters;

public abstract class LoopCounterFactory {

	public abstract LoopEntrancesCounter newCounter(int counterThreshold);
	
}
