package fr.inria.lille.repair.common;


import com.sun.jdi.Type;
import fr.inria.lille.repair.expression.Expression;
import fr.inria.lille.repair.expression.access.Method;

import java.util.*;

/**
 * Todo: explain the two levels of cache
 */
public class Candidates extends ArrayList<Expression> {

    private Set<String> cache = new HashSet<>();
    private Map<String, Object> cacheValues = new HashMap<>();

    public Map<String, Object> getCacheValues() {
        return cacheValues;
    }

    @Override
    public boolean add(Expression expression) {
        if (expression == null) {
            return false;
        }
        String key = expression.toString().intern();
        if (this.cache.contains(key)) {
            return false;
        }

        if (cacheValues.containsKey(key)) {
            return false;
        }
        addToCache(expression);

        if (!this.contains(expression)) {
            return super.add(expression);
        }
        
        // TODO what happens here?
        /*Expression parent = this.get(this.indexOf(expression));
        List<Expression> alternatives = parent.getAlternatives();
        expression.getInAlternativesOf().add(parent);
        alternatives.add(expression);*/
        
        return true;

    }

    public boolean addAll(Candidates candidates) {
        if (candidates == null) {
            return false;
        }
        for (Expression expression : candidates) {
            this.add(expression);
            /*for (int j = 0; j < expression.getAlternatives().size(); j++) {
                Expression expression1 = expression.getAlternatives().get(j);
                this.add(expression1);
            }*/
        }
        cache.addAll(candidates.cache);
        cacheValues.putAll(candidates.cacheValues);
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends Expression> c) {
        if (c == null) {
            return false;
        }
        for (Iterator<? extends Expression> iterator = c.iterator(); iterator.hasNext(); ) {
            Expression next = iterator.next();
            this.add(next);
        }
        return true;
    }

    public Candidates filter(Class... types) {
        Candidates exps = new Candidates();
        for (int i = 0; i < this.size(); i++) {
            Expression expression = this.get(i);
            for (Class type : types) {
                if (expression.getValue().getType() != null && type.isAssignableFrom(expression.getValue().getType())) {
                    exps.add(expression);
                }
            }
        }
        return exps;
    }

    public Candidates filter(Class type, Object value) {
        Candidates exps = new Candidates();

        Set<String> tmpCache = new HashSet<>();
        Map<String, Object> tmpCacheValues = new HashMap<>();

        for (int i = 0; i < this.size(); i++) {
            Expression expression = this.get(i);
            if (expression.getValue().getType() != null && type.isAssignableFrom(expression.getValue().getType()) && (expression.getValue().equals(value) || (expression.getValue() == null && value == null))) {
                exps.add(expression);
                addToCache(expression, tmpCache, tmpCacheValues);
            }
        }
        exps.cache.addAll(tmpCache);
        exps.cacheValues.putAll(tmpCacheValues);
        return exps;
    }

    public Candidates filter(Type type) {
        Candidates exps = new Candidates();
        for (int i = 0; i < this.size(); i++) {
            Expression expression = this.get(i);
            if (expression instanceof Method) {
                continue;
            }
            if (expression.getValue().isAssignableTo(type)) {
                exps.add(expression);
            }
        }
        return exps;
    }

    private void addToCache(Expression expression) {
        addToCache(expression, cache, cacheValues);
    }

    private void addToCache(Expression expression, Set<String> tmpCache, Map<String, Object> tmpCacheValue) {
        String key = expression.toString().intern();
        if (tmpCacheValue.containsKey(key)) {
            return;
        }
        tmpCache.add(key);
        tmpCacheValue.put(key, expression.getValue());
    }

    public Candidates intersection(Candidates filtredCandidates, boolean checkValue) {
        Candidates intersection = new Candidates();
        for (int i = 0; i < filtredCandidates.size(); i++) {
            Expression expression = filtredCandidates.get(i);
            intersection(this, expression, intersection, checkValue);
        }
        return intersection;
    }

    private void intersection(Candidates filtredCandidates, Expression expression, Candidates intersection, boolean checkValue) {
        if (checkValue) {
            if (filtredCandidates.containsExpressionValue(expression)) {
                intersection.add(expression);
            }
            return;
        }
        if (filtredCandidates.containsExpression(expression)) {
            intersection.add(expression);
        }
    }

    public boolean containsExpression(Expression o) {
        return this.cache.contains(o.toString().intern());
    }

    public boolean containsExpressionValue(Expression o) {
        String key = o.toString().intern();
        if (this.cacheValues.containsKey(key)) {
            if (this.cacheValues.get(key) == null) {
                return this.cacheValues.get(key) == o.getValue();
            }
            return this.cacheValues.get(key).equals(o.getValue());
        }
        return false;
    }

    @Override
    public String toString() {
        String result = "";
        for (int i = 0; i < this.size(); i++) {
            Expression expression = this.get(i);
            result += expression + ", ";
        }
        return result;
    }
}
