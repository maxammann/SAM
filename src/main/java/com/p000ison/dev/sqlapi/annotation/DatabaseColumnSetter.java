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
public @interface DatabaseColumnSetter {


    /**
     * The name of the column in the database
     *
     * @return The name of the column
     */
    String databaseName();

    /**
     * Sets the position of the column in the database
     *
     * @return The position
     */
    int position() default -1;

    /**
     * Sets optionally a default value
     *
     * @return A default value or a empty string if there is no one
     */
    String defaultValue() default "";

    /**
     * Sets the lenght of the column
     *
     * @return The lenght of the column or a empty array
     */
    int[] lenght() default {};

    /**
     * If this returns true the column should autoincrement.
     * <p>WARNING: There is only one autoincrementing column in one table!</p>
     *
     * @return Whether this column is autoincrementing
     */
    boolean autoIncrement() default false;

    /**
     * Sets whether this column can be null. Default is false
     *
     * @return Whether this column can be null
     */
    boolean notNull() default true;

    /**
     * Sets whether this column is unique. Default is false
     *
     * @return Whether this column is unique
     */
    boolean unique() default false;

    /**
     * Sets whether this column is primary. Default is false
     * <p>WARNING: There is only one primary column in one table!</p>
     *
     * @return Whether this column is primary
     */
    boolean primary() default false;

}
