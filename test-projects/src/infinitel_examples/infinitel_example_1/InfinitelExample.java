package infinitel_examples.infinitel_example_1;


public class InfinitelExample {

	public int loopResult(Integer a) {
		int b = 0;
		while (b != a) {
			a += 1;
			b += 2;
		}
		return b / 2;
	}

}
