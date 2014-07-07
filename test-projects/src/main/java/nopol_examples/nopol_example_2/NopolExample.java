package nopol_examples.nopol_example_2;

public class NopolExample {

	/*
	 * Return the max between a and b
	 */
	public int getMax(int a, int b){
		if ( (b - a) < 0 ){ // Fix a < b
			return b;
		}
		return a;
	}
	
	private class InnerNopolExample {
		
		public int method(boolean aBoolean) {
			int result = 29;
			if (aBoolean) {
				return result * 2;
			} else {
				return result * 3;
			}
		}
		
		private int fieldOfInnerClass;
	}
	
	private int fieldOfOuterClass;
}
