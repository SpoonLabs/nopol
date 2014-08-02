package fr.inria.lille.commons.collections;

import static fr.inria.lille.commons.collections.MapLibrary.getPutIfAbsent;
import static fr.inria.lille.commons.collections.MapLibrary.newHashMap;

import java.util.Collection;
import java.util.Map;

public class Table<R, C, V> {

	public static <R, C, V> Table<R, C, V> newTable() {
		return new Table<R, C, V>();
	}
	
	public static <R, C, V> Table<R, C, V> newTable(Collection<R> rows) {
		Table<R, C, V> newTable = newTable();
		for (R row : rows) {
			newTable.rowCreateIfAbsent(row);
		}
		return newTable;
	}
	
	private Table() {
		table = newHashMap();
	}
	
	public V put(R row, C column, V value) {
		return rowCreateIfAbsent(row).put(column, value);
	}

	public boolean isEmpty() {
		return numberOfRows() == 0;
	}
	
	public void clear() {
		for (R row : table().keySet()) {
			row(row).clear();
		}
		table().clear();
	}
	
	public boolean existsRow(R row) {
		return table().containsKey(row);
	}
	
	public int numberOfRows() {
		return table().size();
	}
	
	public V cell(R row, C column) {
		return row(row).get(column);
	}

	public Map<C, V> row(R row) {
		return table().get(row);
	}
	
	public Map<C, V> rowCreateIfAbsent(R row) {
		return getPutIfAbsent(table(), row, (Map<C, V>) newHashMap());
	}
	
	private Map<R, Map<C, V>> table() {
		return table;
	}
	
	private Map<R, Map<C, V>> table;
}
