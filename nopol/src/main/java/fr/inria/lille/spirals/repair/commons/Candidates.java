package fr.inria.lille.spirals.repair.commons;

import com.sun.jdi.Type;
import com.sun.jdi.Value;
import fr.inria.lille.spirals.repair.expression.*;

import java.util.*;

/**
 * Created by Thomas Durieux on 05/03/15.
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
        if(this.cache.contains(expression.toString())) {
            return false;
        }
        if (cacheValues.containsKey(expression.toString())) {
            return false;
        }
        addToCache(expression);

        if (!this.contains(expression)) {
            return super.add(expression);
        } else {
            Expression parent = this.get(this.indexOf(expression));
            List<Expression> alternatives = parent.getAlternatives();
            expression.getInAlternativesOf().add(parent);
            return alternatives.add(expression);
        }
    }

    public boolean addAll(Candidates candidates) {
        if (candidates == null) {
            return false;
        }
        for (int i = 0; i < candidates.size(); i++) {
            Expression expression = candidates.get(i);
            this.add(expression);
            for (int j = 0; j < expression.getAlternatives().size(); j++) {
                Expression expression1 = expression.getAlternatives().get(j);
                this.add(expression1);
            }
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
            for (int j = 0; j < types.length; j++) {
                Class type = types[j];
                if (expression.getType() != null && type.isAssignableFrom(expression.getType())) {
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
            if (expression.getType() != null && type.isAssignableFrom(expression.getType()) && (expression.getValue().equals(value) || (expression.getValue() == null && value == null))) {
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
            if (expression instanceof Variable) {
                Value value = ((Variable) expression).getJdiValue();
                if (value != null && value.type().equals(type)) {
                    exps.add(expression);
                }
            } else if (expression instanceof FieldAccess) {
                Value value = ((FieldAccess) expression).getJdiValue();
                if (value != null && value.type().equals(type)) {
                    exps.add(expression);
                }
            } else if (expression instanceof MethodInvocation) {
                if (((MethodInvocation) expression).getJdiValue().type().equals(type)) {
                    exps.add(expression);
                }
            }
        }
        return exps;
    }

    private void addToCache(Expression expression) {
        addToCache(expression, cache, cacheValues);
    }

    private void addToCache(Expression expression, Set<String> tmpCache, Map<String, Object> tmpCacheValue) {
        if (tmpCacheValue.containsKey(expression.toString())) {
            return;
        }
        tmpCache.add(expression.toString());
        tmpCacheValue.put(expression.toString(), expression.getValue());
    }

    public Candidates intersection(Candidates filtredCandidates, boolean checkValue) {
        Candidates intersection = new Candidates();
        for (int i = 0; i < filtredCandidates.size(); i++) {
            Expression expression = filtredCandidates.get(i);
            intersection(this, expression, intersection, checkValue);
        }
        return intersection;
    }

    public Candidates diff(Candidates filtredCandidates) {
        Candidates intersection = new Candidates();
        for (int i = 0; i < this.size(); i++) {
            Expression expression = this.get(i);
            if (!filtredCandidates.cache.contains(expression) || expression instanceof Constant) {
                intersection.add(expression);
            }
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
        return this.cache.contains(o.toString());
    }

    public boolean containsExpressionValue(Expression o) {
        if (this.cacheValues.containsKey(o.toString())) {
            if (this.cacheValues.get(o.toString()) == null) {
                return this.cacheValues.get(o.toString()) == o.getValue();
            }
            return this.cacheValues.get(o.toString()).equals(o.getValue());
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
