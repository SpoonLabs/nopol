package symbolic_examples.symbolic_example_9;

public class NopolExample {

	public int f(int x) {
		int resf = 0;
	    
	    // here there is a bug: fix is  x+2
	    //int guess_fix=Debug.makeSymbolicInteger("guess_fix");
	    //resf = x + guess_fix;//1;
	    resf = x + 1;
		return resf;
	}

	int g(int x) {
		int resg = 0;

		// here there is a bug: fix is x*2
		resg = x * 3;

		return resg;
	}

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