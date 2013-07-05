/*
 * This file is part of SAM (2012).
 *
 * SAM is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SAM is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SAM.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Last modified: 05.07.13 12:21
 */

package org.p000ison.dev.sam.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents a column in a database (use this for fields)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {

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
	 * Sets the length of the column
	 *
	 * @return The length of the column or a empty array
	 */
	int[] length() default {};

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

	boolean id() default false;

	Index[] indices() default {};
}
