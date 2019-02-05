package fr.inria.lille.repair.infinitel.loop.implant;

import xxl.java.container.various.Bag;

public class NullLoopStatistics extends LoopStatistics {

    protected static NullLoopStatistics instance() {
        if (instance == null) {
            instance = new NullLoopStatistics();
        }
        return instance;
    }

    private NullLoopStatistics() {
        Bag<Integer> emptyRecords = Bag.empty();
        setInfiniteInvocation(null);
        setExitRecords(emptyRecords);
        setBreakRecords(emptyRecords);
        setReturnRecords(emptyRecords);
    }

    private static NullLoopStatistics instance;
}
