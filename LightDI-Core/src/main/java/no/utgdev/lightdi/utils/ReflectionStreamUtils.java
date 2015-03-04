package no.utgdev.lightdi.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

public class ReflectionStreamUtils {

    public static class ClassUtils {

        public static Stream<Field> fieldsIn(Class cls) {
            return asList(cls.getDeclaredFields()).stream();
        }
    }

    public static class FieldUtils {

        public static Predicate<Field> hasAnnotation(final Class<? extends Annotation> annotation) {
            return field -> field.isAnnotationPresent(annotation);
        }
    }
}
