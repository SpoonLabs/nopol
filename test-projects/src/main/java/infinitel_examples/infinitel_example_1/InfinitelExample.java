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

    private void fixableInfiniteLoop(int a) {
        while (true) {
            if (unfixableInfiniteLoop(a-1)) {
                return;
            } else if (unfixableInfiniteLoop(a+1)) {
                return;
            }
        }
    }
    
    private boolean unfixableInfiniteLoop(int a) {
        while (true) {
            if (unfixableInfiniteLoop(a-1)) {
                return true;
            } else if (unfixableInfiniteLoop(a+1)) {
                return false;
            }
        }
    }
    
    private int otherUnfixableInfiniteLoop(boolean a, int b) {
    	if (a)
    		while (true) {
	            if (unfixableInfiniteLoop(b-1)) {
	                return b-1;
	            } else if (unfixableInfiniteLoop(b+1)) {
	                return b+1;
	            }
    		}
    	else
    		return 0;
    }
}
