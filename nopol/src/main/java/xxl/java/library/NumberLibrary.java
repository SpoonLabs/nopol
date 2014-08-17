package xxl.java.library;

public class NumberLibrary {

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
}
