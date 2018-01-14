package me.itsmas.sql.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks a class as a serializable database object
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface DatabaseObject
{
    /**
     * The table that the object will form part of
     */
    String table();

    /**
     * The fields to insert into the row when the object is first created
     */
    String[] insertFields();

    /**
     * The name of the field holding the unique value to the object
     */
    String uniqueKeyField() default "";
}
