package fr.inria.lille.repair.infinitel.loop.implant;

import static java.lang.String.format;
import xxl.java.container.various.Bag;
import xxl.java.support.Function;
import fr.inria.lille.repair.infinitel.loop.While;

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
	
	public static Function<LoopStatistics, Bag<Integer>> methodExitRecords() {
		return new Function<LoopStatistics, Bag<Integer>>() {
			@Override
			public Bag<Integer> outputFor(LoopStatistics stats) {
				return stats.exitRecords();
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
	
	public LoopStatistics(While loop) {
		this.loop = loop;
	}
	
	public While loop() {
		return loop;
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
	
	public long numberOfIterations() {
		return Bag.sum(exitRecords());
	}
	
	public int numberOfRecords() {
		return exitRecords().size();
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
		return numberOfBreakExits() + numberOfReturnExits();
	}
	
	public double iterationsRatio() {
		double invocations = (double) numberOfRecords();
		if (invocations == 0.0) {
			return 0.0;
		}
		return numberOfIterations() / invocations;
	}
	
	public Bag<Integer> exitRecords() {
		return exitRecords;
	}
	
	public Bag<Integer> breakRecords() {
		return breakRecords;
	}
	
	public Bag<Integer> returnRecords() {
		return returnRecords;
	}
	
	protected void setInfiniteInvocation(Integer infiniteInvocation) {
		this.infiniteInvocation = infiniteInvocation;
	}
	
	protected void setExitRecords(Bag<Integer> exitRecords) {
		this.exitRecords = exitRecords;
	}
	
	protected void setBreakRecords(Bag<Integer> breakRecords) {
		this.breakRecords = breakRecords;
	}
	
	protected void setReturnRecords(Bag<Integer> returnRecords) {
		this.returnRecords = returnRecords;
	}
	
	@Override
	public String toString() {
		return format("LoopStatistics(%s)", loop.toString());
	}
	
	private While loop;
	private Bag<Integer> exitRecords;
	private Bag<Integer> breakRecords;
	private Bag<Integer> returnRecords;
	private Integer infiniteInvocation;
}
