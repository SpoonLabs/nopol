package fr.inria.lille.commons.synthesis.expression;

import org.smtlib.ISort;
import xxl.java.container.map.Multimap;
import xxl.java.library.ClassLibrary;

import java.util.Collection;

import static fr.inria.lille.commons.synthesis.smt.SMTLib.smtlib;

public abstract class ObjectTemplate<T> {

    public static Multimap<ISort, ObjectTemplate<?>> bySort(Collection<? extends ObjectTemplate<?>> objects) {
        Multimap<ISort, ObjectTemplate<?>> multimap = Multimap.newLinkedHashSetMultimap();
        for (ObjectTemplate<?> object : objects) {
            multimap.add(object.smtSort(), object);
        }
        return multimap;
    }

    public ObjectTemplate(Class<T> aClass) {
        myClass = aClass;
    }

    public Class<T> type() {
        return myClass;
    }

    public ISort smtSort() {
        return smtlib().sortFor(type());
    }

    public boolean typeIsSuperClassOf(Class<?> aClass) {
        return ClassLibrary.isSuperclassOf(aClass, type());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = prime * type().getName().hashCode();
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
        ObjectTemplate<?> other = (ObjectTemplate<?>) obj;
        if (type() == null) {
            if (other.type() != null)
                return false;
        } else if (!type().getName().equals(other.type().getName()))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Object template of class " + type();
    }

    private Class<T> myClass;
}
