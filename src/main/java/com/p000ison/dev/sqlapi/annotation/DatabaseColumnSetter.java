/*
 * This file is part of SQLDatabaseAPI (2012).
 *
 * SQLDatabaseAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SQLDatabaseAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SQLDatabaseAPI.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Last modified: 18.12.12 17:27
 */

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
    boolean notNull() default false;

    /**
     * Sets whether this column is unique. Default is false
     *
     * @return Whether this column is unique
     */
    boolean unique() default false;

    boolean id() default false;

    boolean saveValueAfterLoading() default false;
}
