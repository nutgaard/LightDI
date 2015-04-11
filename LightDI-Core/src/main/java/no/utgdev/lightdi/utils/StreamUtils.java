package no.utgdev.lightdi.utils;

import java.util.function.Function;

public class StreamUtils {
    public static <T> Function<T, T> print() {
        return print("");
    }

    public static <T> Function<T, T> print(String prefix) {
        return (T t) -> {
            System.out.println(prefix + t);
            return t;
        };
    }
}
