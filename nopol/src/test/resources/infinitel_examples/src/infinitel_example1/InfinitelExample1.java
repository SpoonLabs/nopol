package infinitel_example1;

import java.util.ArrayList;
import java.util.List;

public class InfinitelExample1 {

	public int loopResult(Integer a) {
		int b = 0;
		while (b != a) {
			a += 1;
			b += 2;
		}
		return b / 2;
	}
	
	public boolean oneIteration() {
		for (int i = 0; i < 1; i += 1) {
			continue;
		}
		return true;
	}
	
	public void twoIterations() {
		List<Boolean[]> aCollection = new ArrayList<Boolean[]>();
		aCollection.add(new Boolean[] {true});
		aCollection.add(new Boolean[] {false});
		for (Boolean[] array : aCollection) {
			if (array[0]) {
				continue;
			} else if (! array[0]) {
				break;
			}
		}
	}
}
