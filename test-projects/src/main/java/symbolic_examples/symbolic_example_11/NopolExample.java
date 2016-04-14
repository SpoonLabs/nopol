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