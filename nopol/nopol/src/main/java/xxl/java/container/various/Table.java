package xxl.java.container.various;

import xxl.java.support.Factory;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static xxl.java.container.classic.MetaMap.linkedHashMapFactory;
import static xxl.java.container.classic.MetaMap.newLinkedHashMap;
import static xxl.java.library.JavaLibrary.lineSeparator;
import static xxl.java.library.StringLibrary.*;

public class Table<R, C, V> {

    public static <R, C, V> Table<R, C, V> newTable(V defaultValue) {
        Factory<Map<C, V>> rowFactory = linkedHashMapFactory();
        Factory<Map<R, V>> columnFactory = linkedHashMapFactory();
        return new Table<R, C, V>(defaultValue, rowFactory, columnFactory);
    }

    public static <R, C, V> Table<R, C, V> newTable(Collection<? extends R> rows, Collection<? extends C> columns, V defaultValue) {
        Table<R, C, V> newTable = newTable(defaultValue);
        newTable.addRows(rows);
        newTable.addColumns(columns);
        return newTable;
    }

    private Table(V defaultValue, Factory<Map<C, V>> rowFactory, Factory<Map<R, V>> columnFactory) {
        this.defaultValue = defaultValue;
        this.rowFactory = rowFactory;
        this.columnFactory = columnFactory;
        table = newLinkedHashMap();
        transpose = newLinkedHashMap();
    }

    public boolean isEmpty() {
        return numberOfRows() == 0 && numberOfColumns() == 0;
    }

    public int numberOfRows() {
        return rows().size();
    }

    public int numberOfColumns() {
        return columns().size();
    }

    public Collection<R> rows() {
        return table().keySet();
    }

    public Collection<C> columns() {
        return transpose().keySet();
    }

    public boolean hasRow(R row) {
        return table().containsKey(row);
    }

    public boolean hasColumn(C column) {
        return transpose().containsKey(column);
    }

    public Map<C, V> row(R row) {
        return table().get(row);
    }

    public Map<R, V> column(C column) {
        return transpose().get(column);
    }

    public Map<C, V> rowAddIfAbsent(R row) {
        if (!hasRow(row)) {
            addRow(row);
        }
        return row(row);
    }

    public Map<R, V> columnAddIfAbsent(C column) {
        if (!hasColumn(column)) {
            addColumn(column);
        }
        return column(column);
    }

    public V put(R row, C column, V cell) {
        Map<C, V> targetRow = rowAddIfAbsent(row);
        Map<R, V> targetColumn = columnAddIfAbsent(column);
        targetRow.put(column, cell);
        return targetColumn.put(row, cell);
    }

    public void putRow(R row, Map<C, V> columns) {
        for (C column : columns.keySet()) {
            put(row, column, columns.get(column));
        }
    }

    public void putColumn(C column, Map<R, V> rows) {
        for (R row : rows.keySet()) {
            put(row, column, rows.get(row));
        }
    }

    public V cell(R row, C column) {
        return row(row).get(column);
    }

    public void addRows(Collection<? extends R> rows) {
        for (R row : rows) {
            addRow(row);
        }
    }

    public boolean addRow(R row) {
        if (!hasRow(row)) {
            fillEntries(table(), transpose(), rowFactory(), row, defaultValue());
            return true;
        }
        return false;
    }

    public void addColumns(Collection<? extends C> columns) {
        for (C column : columns) {
            addColumn(column);
        }
    }

    public boolean addColumn(C column) {
        if (!hasColumn(column)) {
            fillEntries(transpose(), table(), columnFactory(), column, defaultValue());
            return true;
        }
        return false;
    }

    private <X, Y, Z> void fillEntries(Map<X, Map<Y, Z>> firstHash, Map<Y, Map<X, Z>> secondHash, Factory<Map<Y, Z>> factory, X firstKey, Z defaultValue) {
        Map<Y, Z> newMap = factory.newInstance();
        for (Y secondKey : secondHash.keySet()) {
            newMap.put(secondKey, defaultValue);
            secondHash.get(secondKey).put(firstKey, defaultValue);
        }
        firstHash.put(firstKey, newMap);
    }

    public boolean removeRow(R row) {
        if (hasRow(row)) {
            removeEntries(table(), transpose(), row);
            return true;
        }
        return false;
    }

    public boolean removeColumn(C column) {
        if (hasColumn(column)) {
            removeEntries(transpose(), table(), column);
            return true;
        }
        return false;
    }

    private <X, Y, Z> void removeEntries(Map<X, Map<Y, Z>> firstHash, Map<Y, Map<X, Z>> secondHash, X firstKey) {
        for (Y secondKey : secondHash.keySet()) {
            secondHash.get(secondKey).remove(firstKey);
        }
        firstHash.get(firstKey).clear();
        firstHash.remove(firstKey);
        if (firstHash.isEmpty()) {
            secondHash.clear();
        }
    }

    public void clear() {
        clear(table());
        clear(transpose());
    }

    private <X, Y, Z> void clear(Map<X, Map<Y, Z>> doubleMap) {
        for (Map<Y, Z> map : doubleMap.values()) {
            map.clear();
        }
        doubleMap.clear();
    }

    public String prettyPrinted(int columnWidth) {
        String columnSeparator = " | ";
        int lineLength = (numberOfColumns() + 1) * (columnWidth + 3) - 3;
        String rowSeparator = lineSeparator() + repeated('-', lineLength) + lineSeparator();
        StringBuilder builder = new StringBuilder();
        List<String> columnStrings = toStringList(columns());
        columnStrings.add(0, repeated('.', columnWidth));
        builder.append(join(rightFilled(columnStrings, columnWidth, ' '), columnSeparator));
        for (R row : rows()) {
            builder.append(rowSeparator);
            builder.append(rightFilled(row.toString(), columnWidth, ' ') + columnSeparator);
            builder.append(join(rightFilled(toStringList(row(row).values(), ""), columnWidth, ' '), columnSeparator));
        }
        builder.append(rowSeparator);
        return builder.toString();
    }

    private Map<R, Map<C, V>> table() {
        return table;
    }

    private Map<C, Map<R, V>> transpose() {
        return transpose;
    }

    private V defaultValue() {
        return defaultValue;
    }

    private Factory<Map<C, V>> rowFactory() {
        return rowFactory;
    }

    private Factory<Map<R, V>> columnFactory() {
        return columnFactory;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((defaultValue() == null) ? 0 : defaultValue().hashCode());
        result = prime * result + ((table() == null) ? 0 : table().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Table<?, ?, ?> other = (Table<?, ?, ?>) obj;
        if (defaultValue() == null) {
            if (other.defaultValue() != null)
                return false;
        } else if (!defaultValue().equals(other.defaultValue()))
            return false;
        if (table() == null) {
            if (other.table() != null)
                return false;
        } else if (!table().equals(other.table()))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return format("Table[%d rows; %d columns; default value: %s]", numberOfRows(), numberOfColumns(), defaultValue());
    }

    private V defaultValue;
    private Map<R, Map<C, V>> table;
    private Map<C, Map<R, V>> transpose;
    private Factory<Map<C, V>> rowFactory;
    private Factory<Map<R, V>> columnFactory;
}
