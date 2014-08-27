package fr.inria.lille.repair.infinitel;

public class InfinitelConfiguration {

	public static InfinitelConfiguration firstInstance() {
		/* Refer to: Singleton#createSingleton() */
		return new InfinitelConfiguration();
	}
	
	protected InfinitelConfiguration() {}
	
	public int iterationsThreshold() {
		return (int) 1E6;
	}

}
