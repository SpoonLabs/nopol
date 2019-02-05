package symbolic_examples.symbolic_example_3;

public class NopolExample {
	
	/*
	 * Return true if a is odd number
	 */
	public boolean isOddNumber(int a){
		int tmp = (a-1)%2;
		
		if ( tmp != 0 ){ // Fix : tmp == 0
			return true;
		}
		return false;
		
	}
	
	private void method(boolean aBoolean) {
		int reachableVariable = 3;
		int unreachableVariable;
		if ("aaaa".startsWith("b")) {
			unreachableVariable = 23;
		} else {
			if (! aBoolean || reachableVariable < 2) {
				unreachableVariable = 10;
			}
		}
	}
	
	private void otherMethod(boolean aBoolean) {
		int uninitializedReachableVariable;
		if (aBoolean) {
			uninitializedReachableVariable = 23;
		} else {
			uninitializedReachableVariable = 11;
			if (! aBoolean && uninitializedReachableVariable < 2) {
				uninitializedReachableVariable = 10;
			}
		}
	}
}
