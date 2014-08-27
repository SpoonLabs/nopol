package xxl.java.library;

import java.util.Collection;
import java.util.Collections;
import java.util.NoSuchElementException;

import xxl.java.support.Function;

public class NumberLibrary {

	public static Function<String, Integer> methodParseInteger() {
		return new Function<String, Integer>() {
			@Override
			public Integer outputFor(String value) {
				return Integer.valueOf(value);
			}
		};
	}
	
	public static int ifNegative(int number, int ifNegative) {
		return ifLessThan(0, number, ifNegative);
	}
	
	public static int ifLessThan(int bound, int number, int ifLess) {
		if (number < bound) {
			return ifLess;
		}
		return number;
	}
	
	public static int ifGreaterThan(int bound, int number, int ifGreater) {
		if (number > bound) {
			return ifGreater;
		}
		return number;
	}
	
	public static int bounded(int min, int max, int target) {
		target = Math.max(min, target);
		target = Math.min(max, target);
		return target;
	}
	
	public static int sumInts(Collection<Integer> values) {
		int total = 0;
		for (Integer value : values) {
			total += value;
		}
		return total;
	}
	
	public static long sumLongs(Collection<Long> values) {
		long total = 0;
		for (Long value : values) {
			total += value;
		}
		return total;
	}

	public static Integer maximumInt(Collection<Integer> values) {
		return maximumInt(values, null);
	}
	
	public static Integer maximumInt(Collection<Integer> values, Integer ifEmpty) {
		try {
			return Collections.max(values);
		} catch (NoSuchElementException nsee) {
			return ifEmpty;
		}
	}
	
	public static double mean(Collection<? extends Number> values) {
		if (values.isEmpty()) {
			return 0.0;
		}
		double accumulated = 0.0;
		for (Number number : values) {
			accumulated += number.doubleValue();
		}
		return accumulated / values.size();
	}
}
