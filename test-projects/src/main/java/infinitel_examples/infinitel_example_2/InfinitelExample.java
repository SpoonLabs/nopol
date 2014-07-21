package infinitel_examples.infinitel_example_2;

public class InfinitelExample {

	public int oneIterationOrZero(boolean oneIteration) {
		int a = 0;
		while (oneIteration || ! oneIteration && a == 0) {
			a += 10;
		}
		return a;
	}

}
