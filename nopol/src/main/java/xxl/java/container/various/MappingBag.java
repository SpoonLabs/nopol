package xxl.java.container.various;

import xxl.java.container.classic.MetaMap;
import xxl.java.support.Function;

import java.util.Map;

public class MappingBag<X, T> extends Bag<T> {

    public static <X, T> MappingBag<X, T> newMappingBag(Function<X, T> function) {
        Map<T, Integer> newMap = MetaMap.newHashMap();
        return new MappingBag<X, T>(newMap, function);
    }

    public static <X, T> MappingBag<X, T> newMappingBag(Function<X, T> function, Bag<X> bagToMap) {
        MappingBag<X, T> mappingBag = newMappingBag(function);
        return mappingBag.withAllMapping(bagToMap);
    }

    protected MappingBag(Map<T, Integer> emptyMap, Function<X, T> function) {
        super(emptyMap);
        this.function = function;
    }

    public MappingBag<X, T> withAllMapping(Bag<X> bagToMap) {
        addAllMapping(bagToMap);
        return this;
    }

    public void addAllMapping(Bag<X> bagToMap) {
        for (X element : bagToMap.asSet()) {
            addMapping(element, bagToMap.repetitionsOf(element));
        }
    }

    public int addMapping(X element) {
        return addMapping(element, 1);
    }

    public int addMapping(X element, int numberOfTimes) {
        return super.add(mapped(element), numberOfTimes);
    }

    public boolean removeMapping(X element) {
        return removeMapping(element, 1);
    }

    public boolean removeMapping(X element, int numberOfTimes) {
        return super.remove(mapped(element), numberOfTimes);
    }

    public int repetitionsOfMapped(X element) {
        return super.repetitionsOf(mapped(element));
    }

    protected T mapped(X element) {
        return function().outputFor(element);
    }

    private Function<X, T> function() {
        return function;
    }

    private Function<X, T> function;
}
