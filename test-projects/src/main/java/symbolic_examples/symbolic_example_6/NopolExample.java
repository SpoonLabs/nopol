package symbolic_examples.symbolic_example_6;

public class NopolExample {

	public int abs(int a, int b)
	{
		if (a > b) { // FIX: if(a < b)
			return b - a;
		}
		return a - b;
	}
	
}
