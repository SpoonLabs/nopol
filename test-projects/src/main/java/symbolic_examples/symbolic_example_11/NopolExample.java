package symbolic_examples.symbolic_example_11;

public class NopolExample {

	int h(int x, int y) {
		int resh = 0;

		if (y < 0) {
			// here there is a bug: fix is x%5
			resh = x % 6;
		} else {
			resh = x - 1;
		}

		return resh + 1;
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