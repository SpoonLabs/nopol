package xxl.java.container.various;

import java.util.Map;

import static xxl.java.container.classic.MetaMap.newHashMap;

@SuppressWarnings("rawtypes")
public class NullBag<T> extends Bag<T> {

    @SuppressWarnings("unchecked")
    protected static <T> NullBag<T> instance() {
        if (instance == null) {
            Map<?, Integer> empty = newHashMap();
            instance = new NullBag(empty);
        }
        return instance;
    }

    private NullBag(Map<T, Integer> emptyMap) {
        super(emptyMap);
    }

    private static NullBag instance;
}
