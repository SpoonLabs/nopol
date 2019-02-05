package xxl.container.various;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static xxl.java.container.various.Table.newTable;

import java.util.Map;

import org.junit.Test;

import xxl.java.container.classic.MetaMap;
import xxl.java.container.various.Table;

public class TableTest {

	@Test
	public void basicTableFuncionality() {
		Table<Integer, String, Boolean> table = newTable(false);
		assertTrue(table.isEmpty());
		assertEquals(0, table.numberOfRows());
		assertEquals(0, table.numberOfColumns());
		table.put(1, "a", true);
		table.put(1, "b", false);
		table.put(2, "a", false);
		assertFalse(table.isEmpty());
		assertEquals(2,  table.numberOfRows());
		assertEquals(2, table.numberOfColumns());
		assertTrue(table.hasRow(1));
		assertTrue(table.hasRow(2));
		assertTrue(table.hasColumn("a"));
		assertTrue(table.hasColumn("b"));
		assertTrue(table.cell(1, "a"));
		assertFalse(table.cell(1, "b"));
		assertFalse(table.cell(2, "a"));
		assertFalse(table.cell(2, "b"));
	}
	
	@Test
	public void tableOfNumbers() {
		Table<Integer, Integer, Integer> table = newTable(null);
		table.put(1, 0, 1);
		table.put(1, 1, 2);
		table.put(2, 1, 3);
		table.put(4, 1, 5);
		assertEquals(3, table.numberOfRows());
		assertEquals(2, table.numberOfColumns());
		assertTrue(table.rows().containsAll(asList(1, 2, 4)));
		assertTrue(table.columns().containsAll(asList(0, 1)));
		assertEquals(1, table.cell(1, 0).intValue());
		assertEquals(2, table.cell(1, 1).intValue());
		assertEquals(null, table.cell(2, 0));
		assertEquals(3, table.cell(2, 1).intValue());
		assertEquals(null, table.cell(4, 0));
		assertEquals(5, table.cell(4, 1).intValue());
		assertFalse(table.addRow(1));
		assertTrue(table.addRow(5));
		assertTrue(table.rows().containsAll(asList(1, 2, 4, 5)));
		assertTrue(table.columns().containsAll(asList(0, 1)));
		assertEquals(2, table.row(5).size());
		assertTrue(asList(null, null).containsAll(table.row(5).values()));
	}
	
	@Test
	public void removingColumns() {
		Table<Integer, Integer, Integer> table = newTable(0);
		table.put(1, 1, 1);
		table.put(2, 2, 2);
		table.put(3, 3, 3);
		assertFalse(table.isEmpty());
		assertFalse(table.removeColumn(4));
		assertEquals(3, table.numberOfRows());
		assertEquals(3, table.numberOfColumns());
		assertTrue(table.removeColumn(2));
		assertEquals(3, table.numberOfRows());
		assertEquals(2, table.numberOfColumns());
		assertTrue(table.rows().containsAll(asList(1, 2, 3)));
		assertTrue(table.columns().containsAll(asList(1, 3)));
		assertTrue(table.removeColumn(3));
		assertEquals(3, table.numberOfRows());
		assertEquals(1, table.numberOfColumns());
		assertTrue(table.rows().containsAll(asList(1, 2, 3)));
		assertTrue(table.columns().containsAll(asList(1)));
		assertTrue(table.removeColumn(1));
		assertTrue(table.isEmpty());
		assertEquals(0, table.numberOfRows());
		assertEquals(0, table.numberOfColumns());
	}
	
	@Test
	public void removingRows() {
		Table<Integer, Integer, Integer> table = newTable(0);
		table.put(1, 1, 1);
		table.put(2, 2, 2);
		table.put(3, 3, 3);
		assertFalse(table.isEmpty());
		assertFalse(table.removeRow(4));
		assertEquals(3, table.numberOfRows());
		assertEquals(3, table.numberOfColumns());
		assertTrue(table.removeRow(2));
		assertEquals(2, table.numberOfRows());
		assertEquals(3, table.numberOfColumns());
		assertTrue(table.rows().containsAll(asList(1, 3)));
		assertTrue(table.columns().containsAll(asList(1, 2, 3)));
		assertTrue(table.removeRow(3));
		assertEquals(1, table.numberOfRows());
		assertEquals(3, table.numberOfColumns());
		assertTrue(table.rows().containsAll(asList(1)));
		assertTrue(table.columns().containsAll(asList(1, 2, 3)));
		assertTrue(table.removeRow(1));
		assertTrue(table.isEmpty());
		assertEquals(0, table.numberOfRows());
		assertEquals(0, table.numberOfColumns());
	}
	
	@Test
	public void addingMultipleRows() {
		Table<Integer, Integer, Integer> table = newTable(0);
		table.addRows(asList(1, 2, 3, 4, 2, 5));
		assertFalse(table.isEmpty());
		assertEquals(5, table.numberOfRows());
		assertEquals(0, table.numberOfColumns());
		table.put(2, 2, 2);
		assertTrue(table.hasColumn(2));
		assertEquals(1, table.numberOfColumns());
		assertEquals(0, table.cell(1, 2).intValue());
	}
	
	@Test
	public void addingMultipleColumns() {
		Table<Integer, Integer, Integer> table = newTable(0);
		table.addColumns(asList(1, 2, 3, 4, 2, 5));
		assertFalse(table.isEmpty());
		assertEquals(0, table.numberOfRows());
		assertEquals(5, table.numberOfColumns());
		table.put(2, 2, 2);
		assertTrue(table.hasRow(2));
		assertEquals(1, table.numberOfRows());
		assertEquals(0, table.cell(2, 1).intValue());
	}
	
	@Test
	public void tableCreationWithRowsAndColumns() {
		Table<String, String, Integer> table = newTable(asList("a", "b", "c"), asList("W", "X", "Y", "Z"), 0);
		assertFalse(table.isEmpty());
		assertEquals(3, table.numberOfRows());
		assertTrue(table.rows().containsAll(asList("a", "b", "c")));
		assertEquals(4, table.numberOfColumns());
		assertTrue(table.columns().containsAll(asList("W", "X", "Y", "Z")));
		assertEquals(0, table.cell("a", "X").intValue());
	}
	
	@Test
	public void defineAColumnOfATable() {
		Table<String, String, String> table = newTable(".");
		Map<String, String> column = MetaMap.newLinkedHashMap(asList("a", "b", "c"), asList("0", "1", "2"));
		table.putColumn("Index", column);
		assertEquals(1, table.numberOfColumns());
		assertEquals(3, table.numberOfRows());
		assertEquals("0", table.cell("a", "Index"));
		assertEquals("1", table.cell("b", "Index"));
		assertEquals("2", table.cell("c", "Index"));
	}
	
	@Test
	public void defineARowOfATable() {
		Table<String, String, String> table = newTable(".");
		Map<String, String> row = MetaMap.newLinkedHashMap(asList("Index", "Upper", "Next"), asList("0", "A", "b"));
		table.putRow("a", row);
		assertEquals(3, table.numberOfColumns());
		assertEquals(1, table.numberOfRows());
		assertEquals("0", table.cell("a", "Index"));
		assertEquals("A", table.cell("a", "Upper"));
		assertEquals("b", table.cell("a", "Next"));
	}
}
