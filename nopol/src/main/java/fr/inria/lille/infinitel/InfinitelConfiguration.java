package fr.inria.lille.infinitel;

public class InfinitelConfiguration {

	public static InfinitelConfiguration instance() {
		if (instance == null) {
			instance = new InfinitelConfiguration();
		}
		return instance;
	}
	
	public int iterationsThreshold() {
		return (int) 1E6;
	}

	public int diagnosticsIterationsThreshold() {
		return (int) 1E7;
	}

	protected InfinitelConfiguration() {}
	
	private static InfinitelConfiguration instance;

}
