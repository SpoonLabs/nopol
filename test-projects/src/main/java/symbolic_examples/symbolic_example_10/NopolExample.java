package symbolic_examples.symbolic_example_10;

public class NopolExample {

	int g(int x) {
		int resg = 0;

		// here there is a bug: fix is x*2
		resg = x * 3;

		return resg;
	}

}