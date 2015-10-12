package xxl.java.library;

import xxl.java.support.Function;

public class ObjectLibrary {

    public static <T> Function<T, String> methodToString() {
        return new Function<T, String>() {
            @Override
            public String outputFor(T value) {
                return value.toString();
            }
        };
    }

    public static <U, T> Function<U, T> methodIdentity(final T object) {
        return new Function<U, T>() {
            @Override
            public T outputFor(U value) {
                return object;
            }
        };
    }

    public static <T> Function<T, T> methodYourself() {
        return new Function<T, T>() {
            @Override
            public T outputFor(T value) {
                return value;
            }
        };
    }
}
