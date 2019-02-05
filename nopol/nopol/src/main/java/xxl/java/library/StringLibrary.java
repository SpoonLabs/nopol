package xxl.java.library;

import xxl.java.support.Function;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static xxl.java.container.classic.MetaMap.remade;
import static xxl.java.container.classic.MetaMap.remapped;
import static xxl.java.library.ObjectLibrary.methodToString;

public class StringLibrary {

    public static String unique(String string) {
        return string.intern();
    }

    public static String quoted(String string) {
        return '\"' + string + '\"';
    }

    public static List<String> split(String chainedStrings, Character character) {
        return split(chainedStrings, format("[%c]", character));
    }

    public static List<String> split(String chainedStrings, String splittingRegex) {
        return asList(chainedStrings.split(splittingRegex));
    }

    public static String join(Collection<String> subStrings, Character connector) {
        return join(subStrings, "" + connector);
    }

    public static String join(Collection<String> subStrings, String connector) {
        StringBuilder joined = new StringBuilder();
        if (!subStrings.isEmpty()) {
            Iterator<String> iterator = subStrings.iterator();
            joined.append(iterator.next());
            while (iterator.hasNext()) {
                joined.append(connector + iterator.next());
            }
        }
        return joined.toString();
    }

    public static String stripEnd(String string, String suffix) {
        if (string.endsWith(suffix)) {
            return string.substring(0, string.length() - suffix.length());
        }
        return string;
    }

    public static String firstAfterSplit(String string, Character character) {
        return firstAfterSplit(string, format("[%c]", character));
    }

    public static String firstAfterSplit(String string, String splittingRegex) {
        List<String> splitted = split(string, splittingRegex);
        if (!splitted.isEmpty()) {
            return splitted.get(0);
        }
        return string;
    }

    public static String lastAfterSplit(String string, Character character) {
        return lastAfterSplit(string, format("[%c]", character));
    }

    public static String lastAfterSplit(String string, String splittingRegex) {
        List<String> splitted = split(string, splittingRegex);
        if (!splitted.isEmpty()) {
            return splitted.get(splitted.size() - 1);
        }
        return string;
    }

    public static String reversed(String string) {
        int length = string.length();
        StringBuilder builder = new StringBuilder(length);
        for (int index = length - 1; index >= 0; index -= 1) {
            builder.append(string.charAt(index));
        }
        return builder.toString();
    }

    public static List<String> toStringList(Collection<? extends Object> objects) {
        return toStringList(objects, "null");
    }

    public static List<String> toStringList(Collection<? extends Object> objects, String nullString) {
        List<String> toStringList = new LinkedList<String>();
        for (Object object : objects) {
            if (object == null) {
                toStringList.add(nullString);
            } else {
                toStringList.add(object.toString());
            }
        }
        return toStringList;
    }

    public static <K, V> Map<String, String> asStringMap(Map<K, V> sourceMap) {
        return mapWithStringValues(mapWithStringKeys(sourceMap));
    }

    public static <K, V> Map<String, V> mapWithStringKeys(Map<K, V> sourceMap) {
        Function<K, String> toStringKey = methodToString();
        return remade(sourceMap, toStringKey);
    }

    public static <K, V> Map<K, String> mapWithStringValues(Map<K, V> sourceMap) {
        Function<V, String> toStringValue = methodToString();
        return remapped(sourceMap, toStringValue);
    }

    public static int maximumToStringLength(Collection<? extends Object> objects, int lengthOfNullToString) {
        int length = 0;
        for (Object object : objects) {
            int objectLength = lengthOfNullToString;
            if (object != null) {
                objectLength = object.toString().length();
            }
            length = Math.max(length, objectLength);
        }
        return length;
    }

    public static String rightFilled(String string, int targetLength, Character filler) {
        int difference = 0;
        int length = string.length();
        if (length < targetLength) {
            difference = targetLength - length;
        }
        return string + repeated(filler, difference);
    }

    public static Collection<String> rightFilled(Collection<String> strings, int targetLength, Character filler) {
        Collection<String> filled = new ArrayList<String>(strings.size());
        for (String string : strings) {
            filled.add(rightFilled(string, targetLength, filler));
        }
        return filled;
    }

    public static String leftFilled(String string, int targetLength, Character filler) {
        int difference = 0;
        int length = string.length();
        if (length < targetLength) {
            difference = targetLength - length;
        }
        return repeated(filler, difference) + string;
    }

    public static Collection<String> leftFilled(Collection<String> strings, int targetLength, Character filler) {
        Collection<String> filled = new ArrayList<String>(strings.size());
        for (String string : strings) {
            filled.add(leftFilled(string, targetLength, filler));
        }
        return filled;
    }

    public static String repeated(Character character, int times) {
        return repeated("" + character, times);
    }

    public static String repeated(String text, int times) {
        StringBuilder builder = new StringBuilder();
        if (times > 0) {
            for (int i = 0; i < times; i += 1) {
                builder.append(text);
            }
        }
        return builder.toString();
    }

    /**
     * Returns a plain representation of {@code number}. If the number is an integer,
     * a trailing decimal symbol with a zero is added. The decimal symbol is always the dot.
     */
    public static String plainDecimalRepresentation(Number number) {
        double doubleValue = number.doubleValue();
        BigDecimal decimal = BigDecimal.valueOf(doubleValue);
        if (doubleValue > 1.0 || doubleValue < -1.0) {
            DecimalFormat decimalFormat = new DecimalFormat("#0.0", new DecimalFormatSymbols(Locale.US));
            return decimalFormat.format(decimal);
        } else {
            return decimal.toPlainString();
        }
    }
}

