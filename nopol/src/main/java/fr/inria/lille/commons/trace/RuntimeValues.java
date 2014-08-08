package fr.inria.lille.commons.trace;

import static java.lang.String.format;

import java.util.Map;

import fr.inria.lille.commons.collections.MapLibrary;
import fr.inria.lille.commons.collections.Table;
import fr.inria.lille.commons.trace.collector.ValueCollector;
import fr.inria.lille.commons.utils.GlobalToggle;

public class RuntimeValues extends GlobalToggle {

	public static RuntimeValues newInstance() {
		int instanceNumber = numberOfInstances();
		RuntimeValues newInstance = new RuntimeValues(instanceNumber);
		allInstances().put(instanceNumber, newInstance);
		return newInstance;
	}
	
	public static RuntimeValues instance(int instanceID) {
		return allInstances().get(instanceID);
	}
	
	@Override
	protected void reset() {
		valueTable().clear();
		setCurrentRow(0);
	}

	@Override
	protected String globallyAccessibleName() {
		return format("%s.instance(%d)", getClass().getName(), instanceID());
	}
	
	public String invocationOnCollectionOf(String variableName) {
		return globallyAccessibleName() + format(".collectValue(\"%s\", %s)", variableName, variableName);
	}
	
	public String invocationOnCollectionEnd() {
		return globallyAccessibleName() + ".collectionEnds()";
	}
	
	public void collectValue(String variableName, Object value) {
		ValueCollector.collectFrom(variableName, value, valueTable().rowCreateIfAbsent(currentRow()));
	}
	
	public void collectionEnds() {
		setCurrentRow(currentRow() + 1);
	}
	
	public int numberOfTraces() {
		return valueTable().numberOfRows();
	}
	
	public boolean isEmpty() {
		return valueTable().isEmpty();
	}
	
	public Map<String, Object> valuesFor(int traceNumber) {
		return valueTable().row(traceNumber);
	}
	
	private Table<Integer, String, Object> valueTable() {
		return valueTable;
	}
	
	protected RuntimeValues(int instanceID) {
		this.instanceID = instanceID;
		valueTable = Table.newTable();
	}
	
	private Integer instanceID() {
		return instanceID;
	}
	
	private int currentRow() {
		return currentRow;
	}
	
	private void setCurrentRow(int value) {
		currentRow = value;
	}
	
	private static int numberOfInstances() {
		return allInstances().size();
	}
	
	private static Map<Integer, RuntimeValues> allInstances() {
		if (allInstances == null) {
			allInstances = MapLibrary.newHashMap();
		}
		return allInstances;
	}
	
	private int instanceID;
	private int currentRow;
	private Table<Integer, String, Object> valueTable;
	private static Map<Integer, RuntimeValues> allInstances;
}