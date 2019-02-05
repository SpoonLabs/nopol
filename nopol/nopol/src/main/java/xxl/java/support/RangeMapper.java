package xxl.java.support;

import xxl.java.container.various.Pair;

import static java.lang.Math.*;

public class RangeMapper implements Function<Integer, Pair<Integer, Integer>> {

    public RangeMapper(int base, int step) {
        this.base = base;
        this.step = step;
    }

    @Override
    public Pair<Integer, Integer> outputFor(Integer value) {
        return rangeFor(value);
    }

    public Pair<Integer, Integer> rangeFor(int value) {
        int unsigned = abs(value);
        int factorPower = coefficient(unsigned);
        int area = unsigned / factorPower / step();
        int lowValue = step() * (area) + ((factorPower > 1 && area == 0) ? 1 : 0);
        int highValue = step() * (area + 1);
        return pairCorrectingSign(value < unsigned, factorPower * lowValue, factorPower * highValue);
    }

    private int coefficient(int value) {
        if (value == 0) {
            return 1;
        }
        Double logFactor = log10(value) / log10(base());
        Double power = pow(base(), logFactor.intValue());
        return power.intValue();
    }

    private Pair<Integer, Integer> pairCorrectingSign(boolean negative, int lowValue, int highValue) {
        if (negative) {
            return Pair.from(-1 * highValue, -1 * lowValue);
        }
        return Pair.from(lowValue, highValue);
    }

    private int base() {
        return base;
    }

    private int step() {
        return step;
    }

    private int step;
    private int base;
}
