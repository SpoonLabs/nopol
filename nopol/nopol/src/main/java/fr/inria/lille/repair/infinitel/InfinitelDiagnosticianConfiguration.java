package fr.inria.lille.repair.infinitel;

public class InfinitelDiagnosticianConfiguration extends InfinitelConfiguration {

    public static InfinitelConfiguration firstInstance() {
        /* Refer to: Singleton#createSingleton() */
        return new InfinitelDiagnosticianConfiguration();
    }

    protected InfinitelDiagnosticianConfiguration() {
    }

    @Override
    public int iterationsThreshold() {
        return (int) 1E7;
    }

}
