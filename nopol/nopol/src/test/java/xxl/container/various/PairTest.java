package xxl.container.various;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import org.junit.Test;

import xxl.java.container.various.Pair;

public class PairTest {

	@Test
	public void staticPairConstructor() {
		Pair<Character, String> newPair = Pair.from('a', "aA0");
		assertEquals('a', newPair.first().charValue());
		assertEquals("aA0", newPair.second());
	}
	
	@Test
	public void copyPair() {
		Pair<Character, String> pair = Pair.from('a', "aA0");
		Pair<Character, String> copy = pair.copy();
		assertFalse(pair == copy);
		assertTrue(pair.equals(copy));
		assertTrue(copy.equals(pair));
		assertTrue(pair.hashCode() == copy.hashCode());
	}
	
	@Test
	public void pairComparison() {
		Pair<Character, String> pairA = Pair.from('b', "A");
		Pair<Character, String> pairB = Pair.from('b', "B");
		Pair<Character, String> pairC = Pair.from('a', "B");
		Pair<Character, String> pairD = Pair.from('a', "A");
		List<Pair<Character, String>> list = asList(pairA, pairB, pairC, pairD);
		Collections.sort(list);
		assertTrue(list.get(0) == pairD);
		assertTrue(list.get(1) == pairC);
		assertTrue(list.get(2) == pairA);
		assertTrue(list.get(3) == pairB);
	}
}
