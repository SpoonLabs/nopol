package fr.inria.lille.infinitel;

public class InfinitelDiagnosticianConfiguration extends InfinitelConfiguration {

	public static InfinitelConfiguration instance() {
		if (instance == null) {
			instance = new InfinitelDiagnosticianConfiguration();
		}
		return instance;
	}
	
	@Override
	public int iterationsThreshold() {
		return (int) 1E7;
	}
	
	protected InfinitelDiagnosticianConfiguration() {}
	
	private static InfinitelDiagnosticianConfiguration instance;
}
