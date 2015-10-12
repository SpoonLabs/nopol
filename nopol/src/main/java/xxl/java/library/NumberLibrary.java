package xxl.java.library;

import xxl.java.support.Function;

import java.util.Collection;
import java.util.List;

import static xxl.java.container.classic.MetaCollection.sorted;

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

    public static int sumInts(Iterable<Integer> values) {
        int total = 0;
        for (Integer value : values) {
            total += value;
        }
        return total;
    }

    public static long sumLongs(Iterable<Long> values) {
        long total = 0;
        for (Long value : values) {
            total += value;
        }
        return total;
    }

    public static double sum(Iterable<? extends Number> values) {
        double total = 0.0;
        for (Number value : values) {
            total += value.doubleValue();
        }
        return total;
    }

    public static double mean(Collection<? extends Number> values) {
        if (values.isEmpty()) {
            return 0.0;
        }
        return sum(values) / values.size();
    }

    public static <T extends Number & Comparable<T>> double median(Collection<? extends T> values) {
        if (values.isEmpty()) {
            return 0.0;
        }
        int size = values.size();
        int median = size / 2;
        List<? extends T> ordered = sorted(values);
        double medianValue = ordered.get(median).doubleValue();
        if (size % 2 == 0) {
            medianValue = (medianValue + ordered.get(median - 1).doubleValue()) / 2.0;
        }
        return medianValue;
    }
}
