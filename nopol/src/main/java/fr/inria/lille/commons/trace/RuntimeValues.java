package fr.inria.lille.commons.trace;

import static java.lang.String.format;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;
import fr.inria.lille.commons.collections.ListLibrary;
import fr.inria.lille.commons.collections.MapLibrary;
import fr.inria.lille.commons.collections.Table;
import fr.inria.lille.commons.spoon.util.SpoonModelLibrary;
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
	
	public List<CtStatement> asCollectionStatements(Collection<String> variableNames, CtElement parent) {
		List<CtStatement> newStatements = ListLibrary.newLinkedList();
		for (String variableName : variableNames) {
			CtStatement newStatement = statementFromSnippet(parent, format(".collectValue(\"%s\", %s)", variableName, variableName));
			newStatements.add(newStatement);
		}
		newStatements.add(statementFromSnippet(parent, ".advanceRow()"));
		return newStatements;
	}

	protected CtStatement statementFromSnippet(CtElement parent, String codeSnippet) {
		return SpoonModelLibrary.newStatementFromSnippet(parent.getFactory(), globallyAccessibleName() + codeSnippet, parent);
	}
	
	public void advanceRow() {
		setCurrentRow(currentRow() + 1);
	}
	
	public void collectValue(String variableName, Object value) {
		if (isEnabled()) {
			ValueCollector.collectFrom(variableName, value, valueTable().rowCreateIfAbsent(currentRow()));
		}
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
		if (valueTable == null) {
			valueTable = Table.newTable();
		}
		return valueTable;
	}
	
	protected RuntimeValues(int instanceID) {
		this.instanceID = instanceID;
		allInstances().put(instanceID, this);
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
		int numberOfInstances;
		synchronized (RuntimeValues.class) {
			numberOfInstances = allInstances().size();
		}
		return numberOfInstances;
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