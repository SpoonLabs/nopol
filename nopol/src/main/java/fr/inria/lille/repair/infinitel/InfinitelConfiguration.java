package fr.inria.lille.repair.infinitel;

public class InfinitelConfiguration {

    public static InfinitelConfiguration firstInstance() {
        /* Refer to: Singleton#createSingleton() */
        return new InfinitelConfiguration();
    }

    public static void setIterationsThreshold(int iterationsThreshold) {
        InfinitelConfiguration.iterationsThreshold = iterationsThreshold;
    }

    protected InfinitelConfiguration() {
    }

    public int iterationsThreshold() {
        if (iterationsThreshold == null) {
            iterationsThreshold = (int) 1E6;
        }
        return iterationsThreshold;
    }

    public static Integer iterationsThreshold;
}
