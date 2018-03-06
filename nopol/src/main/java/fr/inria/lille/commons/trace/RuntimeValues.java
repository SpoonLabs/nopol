package fr.inria.lille.commons.trace;

import fr.inria.lille.commons.trace.collector.ValueCollector;
import xxl.java.container.classic.MetaList;
import xxl.java.container.classic.MetaMap;
import xxl.java.support.GlobalToggle;

import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static xxl.java.library.StringLibrary.quoted;

public class RuntimeValues<T> extends GlobalToggle {

    public static <T> RuntimeValues<T> newInstance() {
        int instanceNumber = numberOfInstances();
        RuntimeValues<T> newInstance = new RuntimeValues<T>(instanceNumber);
        allInstances().put(instanceNumber, newInstance);
        return newInstance;
    }

    public static RuntimeValues<?> instance(int instanceID) {
        return allInstances().get(instanceID);
    }

    @Override
    protected String globallyAccessibleName() {
        return format("%s.instance(%d)", getClass().getName(), instanceID());
    }

    public String invocationOnCollectionOf(String variableName) {
        return invocationOnCollectionOf(variableName, variableName);
    }

    public String invocationOnCollectionStart() {
        return invocationMessageFor("collectionStarts");
    }

    public String invocationOnCollectionOf(String codeSource, String executableCode) {
        String quoatationSafeName = quoted(codeSource.replace("\"", "\\\""));
        return "try{" + invocationMessageFor("collectInput", asList(String.class, Object.class), asList(quoatationSafeName, executableCode)) + ";} catch (Exception ex1) {ex1.printStackTrace();}";
    }

    public String invocationOnOutputCollection(String outputName) {
        return invocationMessageFor("collectOutput", asList(Object.class), asList(outputName));
    }

    public String invocationOnCollectionEnd() {
        return invocationMessageFor("collectionEnds");
    }

    public void collectionStarts() {
        acquireToggle();
        outputBuffer = null;
        valueBuffer = MetaMap.newHashMap();
    }

    public void collectInput(String variableName, byte value) {
        ValueCollector.collectFrom(variableName, value, valueBuffer());
    }

    public void collectInput(String variableName, char value) {
        ValueCollector.collectFrom(variableName, value, valueBuffer());
    }

    public void collectInput(String variableName, float value) {
        ValueCollector.collectFrom(variableName, value, valueBuffer());
    }

    public void collectInput(String variableName, double value) {
        ValueCollector.collectFrom(variableName, value, valueBuffer());
    }

    public void collectInput(String variableName, long value) {
        ValueCollector.collectFrom(variableName, value, valueBuffer());
    }

    public void collectInput(String variableName, int value) {
        ValueCollector.collectFrom(variableName, value, valueBuffer());
    }

    public void collectInput(String variableName, boolean value) {
        ValueCollector.collectFrom(variableName, value, valueBuffer());
    }

    public void collectInput(String variableName, Object value) {
        ValueCollector.collectFrom(variableName, value, valueBuffer());
    }

    public void collectOutput(char output) {
        outputBuffer = output;
    }

    public void collectOutput(byte output) {
        outputBuffer = output;
    }

    public void collectOutput(long output) {
        outputBuffer = output;
    }

    public void collectOutput(float output) {
        outputBuffer = output;
    }

    public void collectOutput(double output) {
        outputBuffer = output;
    }

    public void collectOutput(int output) {
        outputBuffer = output;
    }

    public void collectOutput(boolean output) {
        outputBuffer = output;
    }

    public void collectOutput(Object output) {
        outputBuffer = output;
    }

    @SuppressWarnings("unchecked")
    public void collectionEnds() {
        specifications.add(new Specification<T>(valueBuffer(), (T) outputBuffer()));
        releaseToggle();
    }

    public boolean isEmpty() {
        return specificationsForASingleTest().isEmpty();
    }

    /** returns the specification for a single test.
     * If the statement is executed only once, only contains one constraint
     */
    public List<Specification<T>> specificationsForASingleTest() {
        return specifications;
    }

    protected RuntimeValues(int instanceID) {
        this.instanceID = instanceID;
        outputBuffer = null;
        valueBuffer = MetaMap.newHashMap();
        specifications = MetaList.newLinkedList();
    }

    private Integer instanceID() {
        return instanceID;
    }

    private static int numberOfInstances() {
        return allInstances().size();
    }

    protected Map<String, Object> valueBuffer() {
        return valueBuffer;
    }

    protected Object outputBuffer() {
        return outputBuffer;
    }

    private static Map<Integer, RuntimeValues<?>> allInstances() {
        if (allInstances == null) {
            allInstances = MetaMap.newHashMap();
        }
        return allInstances;
    }

    private int instanceID;
    private Object outputBuffer;
    private Map<String, Object> valueBuffer;
    private List<Specification<T>> specifications;
    private static Map<Integer, RuntimeValues<?>> allInstances;
}