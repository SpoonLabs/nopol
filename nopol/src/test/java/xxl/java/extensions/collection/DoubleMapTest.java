package xxl.java.extensions.collection;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static xxl.java.extensions.collection.DoubleMap.newHashDoubleMap;

import org.junit.Test;

import xxl.java.extensions.collection.DoubleMap;
import xxl.java.extensions.collection.MapLibrary;

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
		assertEquals(MapLibrary.newHashMap(asList("a", "b"), asList(true, false)), doubleMap.get(1));
		assertEquals(MapLibrary.newHashMap(asList("a"), asList(false)), doubleMap.get(2));
		doubleMap.getCreateIfAbsent(3);
		assertEquals(3, doubleMap.size());
		assertEquals(MapLibrary.newHashMap(), doubleMap.get(3));
	}
	
}
