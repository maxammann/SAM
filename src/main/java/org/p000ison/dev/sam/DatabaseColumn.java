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

package org.p000ison.dev.sam;

import org.p000ison.dev.sam.annotation.Index;

/**
 * This class represents a column. Known implementations are {@link FieldColumn} (Used to store {@link org.p000ison.dev.sam.annotation.Column}s).
 */
public abstract class DatabaseColumn {

	protected DatabaseColumn() {
	}

	/**
	 * Gets the class of the java object which represents this column
	 *
	 * @return The type of this column
	 */
	public abstract Class<?> getType();

	/**
	 * Gets the name of the column
	 *
	 * @return The name of the column
	 */
	public abstract String getName();

	/**
	 * Gets the position of the column. This can be any value above or equal 0 or -1 if the order does not matter.
	 *
	 * @return The position of the column in the table
	 */
	public abstract int getPosition();

	/**
	 * Gets a optional default value for this column or a empty string
	 *
	 * @return A optional default value
	 */
	public abstract String getDefaultValue();

	/**
	 * Gets the length of this column like { 5, 10 } or a empty array if there is no length.
	 *
	 * @return The length of the column or a empty array.
	 */
	public abstract int[] getLength();

	/**
	 * Whether this column should autoincrement
	 *
	 * @return Weather this column should autoincrement
	 */
	public abstract boolean isAutoIncrementing();

	/**
	 * Whether this column can be null
	 *
	 * @return Whether this column can be null
	 */
	public abstract boolean isNotNull();

	/**
	 * Sets a value for the column in the {@link TableObject}.
	 *
	 * @param tableObject The table object to modify
	 * @param object      The object to set the column to
	 */
	public abstract void setValue(TableObject tableObject, Object object);

	/**
	 * Gets the value for the column in the {@link TableObject}.
	 *
	 * @param tableObject The table object
	 * @return The value
	 */
	public abstract Object getValue(TableObject tableObject);

	/**
	 * Checks if the type is serializable so we can store it in a blob
	 *
	 * @return Weather this type is serializable
	 */
	public boolean isSerializable() {
		return RegisteredTable.isSerializable(getType());
	}

	public abstract Index[] getIndices();

	public abstract boolean isID();
}
