package infinitel_examples.infinitel_example_3;

public class InfinitelExample {

	public static int nestedLoops(int a) {
		int aCopy = a;
		while (true) {
			while (aCopy > 0) {
				aCopy -= 1;
			}
			if (a > aCopy) {
				break;
			}
		}
		return aCopy;
	}
	
}
