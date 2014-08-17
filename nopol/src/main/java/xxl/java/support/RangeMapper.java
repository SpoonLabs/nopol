package xxl.java.support;

import static java.lang.Math.abs;
import static java.lang.Math.log10;
import static java.lang.Math.pow;
import xxl.java.container.various.Pair;

public class RangeMapper implements Function<Integer, Pair<Integer, Integer>> {

	public RangeMapper(int factor, int step) {
		this.factor = factor;
		this.step = step;
	}
	
	@Override
	public Pair<Integer, Integer> outputFor(Integer value) {
		return rangeFor(value);
	}
	
	public Pair<Integer, Integer> rangeFor(int value) {
		int originalValue = value;
		value = abs(value);
		int factorPower = coefficient(value);
		int area = value / factorPower / step();
		int lowValue = step() * (area) + ((factorPower > 1 && area == 0) ? 1 : 0);
		int highValue = step() * (area + 1);
		return pairCorrectingSign(originalValue, factorPower * lowValue, factorPower * highValue);
	}
	
	private int coefficient(int value) {
		if (value == 0) {
			return 1;
		}
		Double logFactor = log10(value) / log10(factor());
		Double power = pow(factor(), logFactor.intValue());
		return power.intValue();
	}
	
	private Pair<Integer, Integer> pairCorrectingSign(int value, int lowValue, int highValue) {
		if (value < 0) {
			return Pair.from(-1 * highValue, -1 * lowValue);
		}
		return Pair.from(lowValue, highValue);
	}
	
	private int factor() {
		return factor;
	}
	
	private int step() {
		return step;
	}
	
	private int step;
	private int factor;
}
