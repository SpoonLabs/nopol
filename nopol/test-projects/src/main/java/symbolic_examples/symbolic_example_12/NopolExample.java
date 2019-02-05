package symbolic_examples.symbolic_example_12;

public class NopolExample {

	int i(int x, Calculator y) {
		int resi = 0;

		// here there is in the concrete implementation of y
		resi = y.compute(x);

		return resi * 2;
	}
}

interface Calculator {
	int compute(int x);
}

class C1 implements Calculator {
	public int compute(int x) {
		return x + 1;
	}
}

class C2 implements Calculator {
	public int compute(int x) {
		// bug: should be x+2
		int resC2 = x + 3;
		return resC2;
	}
}