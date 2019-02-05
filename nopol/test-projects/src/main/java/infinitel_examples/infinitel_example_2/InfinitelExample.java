package infinitel_examples.infinitel_example_2;

public class InfinitelExample {

	public int oneIterationOrZero(boolean oneIteration) {
		int a = 0;
		while (oneIteration || ! oneIteration && a == 0) {
			a += 10;
		}
		return a;
	}

	public int breakInSwitchIsNotForWhile(char aChar) {
		while (true) {
			switch (aChar) {
				case 'a':
					return 1;
				case 'e':
					return 2;
				case 's':
					break;
				case 't':
					break;
				case 'u':
					break;
				case 'v':
					break;
			}
			if (aChar == 255) {
				break;
			}
			aChar++;
		}
		return 12;
	}
	
	public int returnInAnonymousClassIsNotForWhile(int a) {
		while (a-- > 0) {
			Comparable<Integer> comparable = new Comparable<Integer>() {
				public int compareTo(Integer b) {
					return 0;
				}
			};
			a += comparable.compareTo(a);
		}
		return a;
	}
	
	public int breakAndReturnInForNotForWhile(int a) {
		while (a-- > 0) {
			for (int i = 0; i < a; i += 1) {
				if (i == -a) {
					break;
				}
			}
			for (int i = a; i < 0; i -= 1) {
				if (i == -a) {
					return 0;
				}
			}
		}
		return a;
	}
}
