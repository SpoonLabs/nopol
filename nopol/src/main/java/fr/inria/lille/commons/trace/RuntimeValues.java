package fr.inria.lille.commons.trace;

import static java.lang.String.format;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import fr.inria.lille.commons.collections.MapLibrary;
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
	public void reset() {
		uniqueTraces().clear();
		setTraceNumber(0);
		flushBuffer();
	}

	@Override
	protected String globallyAccessibleName() {
		return format("%s.instance(%d)", getClass().getName(), instanceID());
	}
	
	public String invocationOnCollectionOf(String variableName) {
		String quoatationSafeName = variableName.replace("\"", "\\\"");
		return globallyAccessibleName() + format(".collectValue(\"%s\", %s)", quoatationSafeName, variableName);
	}
	
	public String invocationOnCollectionEnd() {
		return globallyAccessibleName() + ".collectionEnds()";
	}
	
	public void collectValue(String variableName, Object value) {
		ValueCollector.collectFrom(variableName, value, valueBuffer());
	}
	
	public void collectionEnds() {
		if (! uniqueTraces().containsKey(valueBuffer())) {
			uniqueTraces().put(valueBuffer(), traceNumber());
			renewBuffer();
		}
		flushBuffer();
		setTraceNumber(traceNumber() + 1);
	}
	
	public int numberOfTraces() {
		return traceNumber();
	}
	
	public boolean isEmpty() {
		return uniqueTraces().isEmpty();
	}
	
	public Collection<Entry<Map<String, Object>, Integer>> uniqueTraceSet() {
		return uniqueTraces().entrySet();
	}
	
	private Map<Map<String, Object>, Integer> uniqueTraces() {
		return uniqueTraces;
	}
	
	protected RuntimeValues(int instanceID) {
		this.instanceID = instanceID;
		uniqueTraces = MapLibrary.newHashMap();
		renewBuffer();
	}
	
	private Integer instanceID() {
		return instanceID;
	}
	
	private int traceNumber() {
		return traceNumber;
	}
	
	private void setTraceNumber(int value) {
		traceNumber = value;
	}
	
	private static int numberOfInstances() {
		return allInstances().size();
	}
	
	protected Map<String, Object> valueBuffer() {
		return valueBuffer;
	}
	
	private void renewBuffer() {
		valueBuffer = MapLibrary.newHashMap();
	}
	
	private void flushBuffer() {
		valueBuffer().clear();
	}
	
	private static Map<Integer, RuntimeValues> allInstances() {
		if (allInstances == null) {
			allInstances = MapLibrary.newHashMap();
		}
		return allInstances;
	}
	
	private int instanceID;
	private int traceNumber;
	private Map<String, Object> valueBuffer;
	private Map<Map<String, Object>, Integer> uniqueTraces;
	private static Map<Integer, RuntimeValues> allInstances;
}