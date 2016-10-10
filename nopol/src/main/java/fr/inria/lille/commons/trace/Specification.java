package fr.inria.lille.commons.trace;

import java.util.Map;

import static java.lang.String.format;

public class Specification<T> {

    public Specification(Map<String, Object> values, T expectedOutput) {
        this.values = values;
        this.expectedOutput = expectedOutput;
    }

    public Map<String, Object> inputs() {
        return values;
    }

    public T output() {
        return expectedOutput;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((output() == null) ? 0 : output().hashCode());
        result = prime * result + ((inputs() == null) ? 0 : inputs().hashCode());
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
        Specification<?> other = (Specification<?>) obj;
        if (output() == null) {
            if (other.output() != null)
                return false;
        } else if (!output().equals(other.output()))
            return false;
        if (inputs() == null) {
            if (other.inputs() != null)
                return false;
        } else if (!inputs().equals(other.inputs()))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return format("collected data: %s. outcome: %s", inputs().toString(), output().toString());
    }

    private T expectedOutput;
    private Map<String, Object> values;
}
