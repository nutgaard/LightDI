package no.utgdev.lightdi.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@AOPAnnotation
@Retention(value = RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.TYPE})
public @interface Bean {
}
