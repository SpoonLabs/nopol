package fr.inria.lille.repair.synthesis.collect.spoon;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by spirals on 27/03/15.
 */
public class MethodCollector extends AbstractProcessor<CtInvocation> {
    private Map<String, Integer> statMethod = new HashMap<>();


    public MethodCollector() {

    }

    @Override
    public void process(CtInvocation ctElement) {
        CtExecutableReference executable = ctElement.getExecutable();
        if (executable.isConstructor()) {
            return;
        }
        String key = executable.toString();
        CtTypeReference declaringType = executable.getDeclaringType();
        if (declaringType.getPackage() != null) {
            key = declaringType.getPackage().getSimpleName() + "." + executable.getSimpleName();
        }
        if (!statMethod.containsKey(key)) {
            statMethod.put(key, 1);
        } else {
            statMethod.put(key, statMethod.get(key) + 1);
        }
    }

    public Map<String, Integer> getStatMethod() {
        return statMethod;
    }

    public Set<String> getMethods() {
        return statMethod.keySet();
    }
}
