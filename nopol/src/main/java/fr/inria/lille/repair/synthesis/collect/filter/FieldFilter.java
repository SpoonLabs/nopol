package fr.inria.lille.repair.synthesis.collect.filter;

import com.sun.jdi.Field;

/**
 * Created by Thomas Durieux on 06/03/15.
 */
public class FieldFilter {
    public static boolean toProcess(Field field) {
        if (field.name().equals("serialVersionUID")) {
            return false;
        }
        if (field.name().toLowerCase().contains("hash")) {
            return false;
        }
        return !field.declaringType().name().equals("java.lang.String");
    }
}
