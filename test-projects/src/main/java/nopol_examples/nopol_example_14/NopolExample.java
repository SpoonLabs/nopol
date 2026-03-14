package nopol_examples.nopol_example_14;

public class NopolExample {

	/*
   * Buggy implementation of the identity function.
	 */
	public int identity(int a){
		if (a == 1) { // With *only* the test a = 1, there are "Not enough specifications",
									// yet this statement admits an angelic value. 
			return 0;
		}

		return a;
	}
}
