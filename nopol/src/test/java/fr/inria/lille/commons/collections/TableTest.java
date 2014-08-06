package fr.inria.lille.commons.collections;

import static fr.inria.lille.commons.collections.Table.newTable;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import fr.inria.lille.commons.collections.MapLibrary;
import fr.inria.lille.commons.collections.Table;

public class TableTest {

	@Test
	public void table() {
		Table<Integer, String, Boolean> table = newTable();
		assertTrue(table.isEmpty());
		assertEquals(0, table.numberOfRows());
		table.put(1, "a", true);
		table.put(1, "b", false);
		table.put(2, "a", false);
		assertFalse(table.isEmpty());
		assertEquals(2,  table.numberOfRows());
		assertTrue(table.cell(1, "a"));
		assertFalse(table.cell(1, "b"));
		assertFalse(table.cell(2, "a"));
		assertEquals(MapLibrary.newHashMap(asList("a", "b"), asList(true, false)), table.row(1));
		assertEquals(MapLibrary.newHashMap(asList("a"), asList(false)), table.row(2));
		table.rowCreateIfAbsent(3);
		assertEquals(3, table.numberOfRows());
		assertEquals(MapLibrary.newHashMap(), table.row(3));
	}
	
	@Test
	public void tableCreationWithRows() {
		Table<Integer, String, Boolean> table = newTable(asList(1, 2, 3));
		assertEquals(3, table.numberOfRows());
		assertTrue(table.existsRow(1));
		assertTrue(table.existsRow(2));
		assertTrue(table.existsRow(3));
	}
}
