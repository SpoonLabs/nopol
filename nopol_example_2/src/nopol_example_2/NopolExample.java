package nopol_example_2;

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
}
