package fr.inria.lille.commons.synthesis.smt.locationVariables;

import fr.inria.lille.commons.synthesis.expression.Expression;
import fr.inria.lille.commons.synthesis.smt.SMTLib;
import org.smtlib.IExpr;
import xxl.java.container.classic.MetaList;

import java.util.List;
import java.util.Map;

public class IndexedLocationVariable<T> extends LocationVariable<T> {

    public static List<Object> extractWithObjectExpressions(Map<String, Object> map, List<IndexedLocationVariable<?>> locationVariables) {
        List<Object> extracted = MetaList.newLinkedList();
        for (IndexedLocationVariable<?> locationVariable : locationVariables) {
            extracted.add(map.get(locationVariable.objectTemplate().expression()));
        }
        return extracted;
    }

    public IndexedLocationVariable(Expression<T> expression, String subexpression, int index) {
        super(expression, subexpression);
        this.index = index;
    }

    public int index() {
        return index;
    }

    @Override
    public Expression<T> objectTemplate() {
        return (Expression<T>) super.objectTemplate();
    }

    @Override
    public IExpr encodedLineNumber() {
        return SMTLib.smtlib().numeral(Integer.toString(index()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + index;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        IndexedLocationVariable<?> other = (IndexedLocationVariable<?>) obj;
        if (index != other.index)
            return false;
        return true;
    }

    private int index;
}
