package symbolic_examples.symbolic_example_10;

public class NopolExample {

	int g(int x) {
		int resg = 0;

		// here there is a bug: fix is x*2
		resg = x * 3;

		return resg;
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