package xxl.java.junit;

import java.util.Map;
import java.util.Map.Entry;

import static org.junit.Assert.assertTrue;

public class JunitLibrary {

    public static void assertEquality(Object a, Object b) {
        assertTrue(a.hashCode() == b.hashCode());
        assertTrue(a.equals(b));
        assertTrue(b.equals(a));
    }

    public static <K, V> void assertMapEquals(Map<? extends K, ? extends V> mapA, Map<? extends K, ? extends V> mapB) {
        assertTrue(mapA.size() == mapB.size());
        assertTrue(mapA.keySet().containsAll(mapB.keySet()));
        assertTrue(mapB.keySet().containsAll(mapA.keySet()));
        for (Entry<? extends K, ? extends V> entry : mapA.entrySet()) {
            assertEquality(entry.getValue(), mapB.get(entry.getKey()));
        }
    }
}
