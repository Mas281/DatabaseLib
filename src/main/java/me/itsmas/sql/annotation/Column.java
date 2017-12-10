package me.itsmas.sql.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks a field in an object as a database column
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface Column
{
    /**
     * The name of the column which the field wraps
     *
     * If no value is specified, the field's
     * name will be used when processing it
     */
    String value() default "";
}
