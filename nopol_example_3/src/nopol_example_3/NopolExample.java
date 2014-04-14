package nopol_example_3;

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
	
	
	
}
