package xxl.java.extensions.collection;

import java.util.Arrays;

public class ArrayLibrary {

	public static <T> T[] subarray(T[] array, int start, int end) {
		Integer newLength = end - start;
		if (newLength < 0) {
			throw new IndexOutOfBoundsException("Can't get subarray from " + start + " to " + end);
		}
		end = Math.min(end, array.length);
		return Arrays.copyOfRange(array, start, end);
	}
}
