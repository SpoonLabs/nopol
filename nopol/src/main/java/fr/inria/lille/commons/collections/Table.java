package fr.inria.lille.commons.collections;

import static fr.inria.lille.commons.collections.MapLibrary.getPutIfAbsent;
import static fr.inria.lille.commons.collections.MapLibrary.linkedHashMapFactory;
import static fr.inria.lille.commons.collections.MapLibrary.newLinkedHashMap;

import java.util.Collection;
import java.util.Map;

import fr.inria.lille.commons.utils.Factory;

public class Table<R, C, V> {

	public static <R, C, V> Table<R, C, V> newTable() {
		Map<R, Map<C, V>> emptyMap = newLinkedHashMap();
		Factory<Map<C, V>> mapFactory = linkedHashMapFactory();
		return new Table<R, C, V>(emptyMap, mapFactory);
	}
	
	public static <R, C, V> Table<R, C, V> newTable(Collection<R> rows) {
		Table<R, C, V> newTable = newTable();
		for (R row : rows) {
			newTable.rowCreateIfAbsent(row);
		}
		return newTable;
	}
	
	private Table(Map<R, Map<C, V>> emptyMap, Factory<Map<C, V>> rowMapFactory) {
		table = emptyMap;
		this.rowMapFactory = rowMapFactory;
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
		return getPutIfAbsent(table(), row, rowMapFactory().newInstance());
	}
	
	private Map<R, Map<C, V>> table() {
		return table;
	}
	
	private Factory<Map<C, V>> rowMapFactory() {
		return rowMapFactory;
	}
	
	private Map<R, Map<C, V>> table;
	private Factory<Map<C, V>> rowMapFactory;
}
