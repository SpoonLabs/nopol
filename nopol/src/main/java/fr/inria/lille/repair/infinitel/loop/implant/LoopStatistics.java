package fr.inria.lille.repair.infinitel.loop.implant;

import xxl.java.container.various.Bag;
import xxl.java.support.Function;

import static java.lang.String.format;

public class LoopStatistics {

    public static Function<LoopStatistics, Integer> methodTopRecord() {
        return new Function<LoopStatistics, Integer>() {
            @Override
            public Integer outputFor(LoopStatistics stats) {
                return stats.topRecord();
            }
        };
    }

    public static Function<LoopStatistics, Long> methodNumberOfIterations() {
        return new Function<LoopStatistics, Long>() {
            @Override
            public Long outputFor(LoopStatistics stats) {
                return stats.numberOfIterations();
            }
        };
    }

    public static Function<LoopStatistics, Integer> methodNumberOfRecords() {
        return new Function<LoopStatistics, Integer>() {
            @Override
            public Integer outputFor(LoopStatistics stats) {
                return stats.numberOfRecords();
            }
        };
    }

    public static Function<LoopStatistics, Integer> methodNumberOfErrorExits() {
        return new Function<LoopStatistics, Integer>() {
            @Override
            public Integer outputFor(LoopStatistics stats) {
                return stats.numberOfErrorExits();
            }
        };
    }

    public static Function<LoopStatistics, Integer> methodNumberOfBreakExits() {
        return new Function<LoopStatistics, Integer>() {
            @Override
            public Integer outputFor(LoopStatistics stats) {
                return stats.numberOfBreakExits();
            }
        };
    }

    public static Function<LoopStatistics, Integer> methodNumberOfReturnExits() {
        return new Function<LoopStatistics, Integer>() {
            @Override
            public Integer outputFor(LoopStatistics stats) {
                return stats.numberOfReturnExits();
            }
        };
    }

    public static Function<LoopStatistics, Integer> methodNumberOfConditionalExits() {
        return new Function<LoopStatistics, Integer>() {
            @Override
            public Integer outputFor(LoopStatistics stats) {
                return stats.numberOfConditionalExits();
            }
        };
    }

    public static Function<LoopStatistics, Integer> methodNumberOfNonConditionalExits() {
        return new Function<LoopStatistics, Integer>() {
            @Override
            public Integer outputFor(LoopStatistics stats) {
                return stats.numberOfNonConditionalExits();
            }
        };
    }

    public static Function<LoopStatistics, Double> methodIterationsRatio() {
        return new Function<LoopStatistics, Double>() {
            @Override
            public Double outputFor(LoopStatistics stats) {
                return stats.iterationsRatio();
            }
        };
    }

    public static Function<LoopStatistics, Double> methodIterationsMedian() {
        return new Function<LoopStatistics, Double>() {
            @Override
            public Double outputFor(LoopStatistics stats) {
                return stats.iterationMedian();
            }
        };
    }

    public static Function<LoopStatistics, Double> methodIterationsFirstQuartile() {
        return new Function<LoopStatistics, Double>() {
            @Override
            public Double outputFor(LoopStatistics stats) {
                return stats.iterationsFirstQuartile();
            }
        };
    }

    public static Function<LoopStatistics, Double> methodIterationsThirdQuartile() {
        return new Function<LoopStatistics, Double>() {
            @Override
            public Double outputFor(LoopStatistics stats) {
                return stats.iterationsThirdQuartile();
            }
        };
    }

    public static Function<LoopStatistics, Bag<Integer>> methodExitRecords() {
        return new Function<LoopStatistics, Bag<Integer>>() {
            @Override
            public Bag<Integer> outputFor(LoopStatistics stats) {
                return stats.exitRecords();
            }
        };
    }

    public static Function<LoopStatistics, Bag<Integer>> methodConditionalRecords() {
        return new Function<LoopStatistics, Bag<Integer>>() {
            @Override
            public Bag<Integer> outputFor(LoopStatistics stats) {
                return stats.conditionalRecords();
            }
        };
    }

    public static Function<LoopStatistics, Bag<Integer>> methodErrorRecords() {
        return new Function<LoopStatistics, Bag<Integer>>() {
            @Override
            public Bag<Integer> outputFor(LoopStatistics stats) {
                return stats.errorRecords();
            }
        };
    }

    public static Function<LoopStatistics, Bag<Integer>> methodBreakRecords() {
        return new Function<LoopStatistics, Bag<Integer>>() {
            @Override
            public Bag<Integer> outputFor(LoopStatistics stats) {
                return stats.breakRecords();
            }
        };
    }

    public static Function<LoopStatistics, Bag<Integer>> methodReturnRecords() {
        return new Function<LoopStatistics, Bag<Integer>>() {
            @Override
            public Bag<Integer> outputFor(LoopStatistics stats) {
                return stats.returnRecords();
            }
        };
    }

    public static LoopStatistics empty() {
        return NullLoopStatistics.instance();
    }

    protected static long sumOf(Bag<Integer> bag) {
        long total = 0;
        for (Integer record : bag.asSet()) {
            total += record * bag.repetitionsOf(record);
        }
        return total;
    }

    public static double meanOf(Bag<Integer> bag) {
        double invocations = bag.size();
        if (invocations == 0.0) {
            return 0.0;
        }
        return sumOf(bag) / invocations;
    }

    public static double medianOf(Bag<Integer> bag) {
        return percentile(50, bag);
    }

    public static double firstQuartileOf(Bag<Integer> bag) {
        return percentile(25, bag);
    }

    public static double thirdQuartileOf(Bag<Integer> bag) {
        return percentile(75, bag);
    }

    public static double percentile(int percentile, Bag<Integer> bag) {
        int size = bag.size();
        int index = (int) ((percentile / 100.0) * size);
        if (size == 0) {
            return 0.0;
        }
        if (percentile < 100.0 / size) {
            return Bag.accessedInOrder(0, bag);
        }
        if (percentile + 100.0 / size > 100.0) {
            return Bag.accessedInOrder(size - 1, bag);
        }
        if (index * 100 == percentile * (size - 1)) {
            return Bag.accessedInOrder(index, bag);
        }
        int left = Bag.accessedInOrder(index - 1, bag);
        int right = Bag.accessedInOrder(index, bag);
        return (left + right) / 2.0;
    }

    public boolean hasInfiniteInvocation() {
        return infiniteInvocation() != null;
    }

    public Integer infiniteInvocation() {
        return infiniteInvocation;
    }

    public int topRecord() {
        int topRecord = 0;
        for (Integer record : exitRecords().asSet()) {
            if (record > topRecord) {
                topRecord = record;
            }
        }
        return topRecord;
    }

    public int numberOfRecords() {
        return exitRecords().size();
    }

    public int numberOfErrorExits() {
        return errorRecords().size();
    }

    public int numberOfBreakExits() {
        return breakRecords().size();
    }

    public int numberOfReturnExits() {
        return returnRecords().size();
    }

    public int numberOfConditionalExits() {
        return numberOfRecords() - numberOfNonConditionalExits();
    }

    public int numberOfNonConditionalExits() {
        return numberOfErrorExits() + numberOfBreakExits() + numberOfReturnExits();
    }

    public long numberOfIterations() {
        return sumOf(exitRecords());
    }

    public double iterationsRatio() {
        return meanOf(exitRecords());
    }

    public double iterationMedian() {
        return medianOf(exitRecords());
    }

    public double iterationsFirstQuartile() {
        return firstQuartileOf(exitRecords());
    }

    public double iterationsThirdQuartile() {
        return thirdQuartileOf(exitRecords());
    }

    public Bag<Integer> exitRecords() {
        return exitRecords;
    }

    public Bag<Integer> errorRecords() {
        return errorRecords;
    }

    public Bag<Integer> breakRecords() {
        return breakRecords;
    }

    public Bag<Integer> returnRecords() {
        return returnRecords;
    }

    public Bag<Integer> conditionalRecords() {
        Bag<Integer> conditionalRecords = exitRecords().copy();
        conditionalRecords.remove(errorRecords());
        conditionalRecords.remove(breakRecords());
        conditionalRecords.remove(returnRecords());
        return conditionalRecords;
    }

    protected void setInfiniteInvocation(Integer infiniteInvocation) {
        this.infiniteInvocation = infiniteInvocation;
    }

    protected void setExitRecords(Bag<Integer> exitRecords) {
        this.exitRecords = exitRecords;
    }

    protected void setErrorRecords(Bag<Integer> errorRecords) {
        this.errorRecords = errorRecords;
    }

    protected void setBreakRecords(Bag<Integer> breakRecords) {
        this.breakRecords = breakRecords;
    }

    protected void setReturnRecords(Bag<Integer> returnRecords) {
        this.returnRecords = returnRecords;
    }

    @Override
    public String toString() {
        return format("[#] loop-statistics");
    }

    private Bag<Integer> exitRecords;
    private Bag<Integer> errorRecords;
    private Bag<Integer> breakRecords;
    private Bag<Integer> returnRecords;
    private Integer infiniteInvocation;
}
