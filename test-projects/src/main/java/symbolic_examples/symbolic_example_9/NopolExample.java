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
}