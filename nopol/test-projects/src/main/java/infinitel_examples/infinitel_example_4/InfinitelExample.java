package infinitel_examples.infinitel_example_4;

public class InfinitelExample {

	public static int loopWithBreakAndReturn(int a) {
		int b = a;
		while (b > 0) {
			if (b == 18) {
				return a;
			}
			if (b == 9) {
				break;
			}
			b -=1;
		}
		return b;
	}
}
