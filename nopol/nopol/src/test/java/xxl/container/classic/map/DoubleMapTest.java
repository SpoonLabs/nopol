package xxl.container.classic.map;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static xxl.java.container.map.DoubleMap.newHashDoubleMap;

import org.junit.Test;

import xxl.java.container.classic.MetaMap;
import xxl.java.container.map.DoubleMap;

public class DoubleMapTest {

	@Test
	public void doubleMap() {
		DoubleMap<Integer, String, Boolean> doubleMap = newHashDoubleMap();
		assertTrue(doubleMap.isEmpty());
		assertEquals(0, doubleMap.size());
		doubleMap.put(1, "a", true);
		doubleMap.put(1, "b", false);
		doubleMap.put(2, "a", false);
		assertFalse(doubleMap.isEmpty());
		assertEquals(2,  doubleMap.size());
		assertTrue(doubleMap.value(1, "a"));
		assertFalse(doubleMap.value(1, "b"));
		assertFalse(doubleMap.value(2, "a"));
		assertEquals(MetaMap.newHashMap(asList("a", "b"), asList(true, false)), doubleMap.get(1));
		assertEquals(MetaMap.newHashMap(asList("a"), asList(false)), doubleMap.get(2));
		doubleMap.getPutIfAbsent(3);
		assertEquals(3, doubleMap.size());
		assertEquals(MetaMap.newHashMap(), doubleMap.get(3));
	}
	
}
