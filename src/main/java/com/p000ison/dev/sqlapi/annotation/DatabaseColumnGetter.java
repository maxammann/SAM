package com.p000ison.dev.sqlapi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents a column in a database (use this for methods)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DatabaseColumnGetter {

    /**
     * The name of the column in the database
     *
     * @return The name of the column
     */
    String databaseName();

}
