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
    
    public double binomialTest(int numberOfTrials, int numberOfSuccesses, char probability) {
	    switch (probability) {
	        case '0':
	            int criticalValueLow = 0;
	            int criticalValueHigh = numberOfTrials;
	            double pTotal = 0;
	            while (true) {
	                double pLow = numberOfTrials / 0.01;
	                numberOfTrials = (int) pLow;
	                if ((criticalValueLow > numberOfSuccesses) || (criticalValueHigh < numberOfSuccesses)) {
	                    break;
	                } 
	            }
	            return pTotal;
	        default :
	            return 1;
	    }
	}
}
