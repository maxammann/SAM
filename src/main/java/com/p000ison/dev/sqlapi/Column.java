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
 * Last modified: 18.12.12 18:07
 */

package com.p000ison.dev.sqlapi;

/**
 * Represents a Column
 */
public abstract class Column {

    protected Column()
    {
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
     * Gets the lenght of this column like { 5, 10 } or a empty array if there is no lenght.
     *
     * @return The lenght of the column or a empty array.
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
     * Whether this column is unique
     *
     * @return Whether this column is unique
     */
    public abstract boolean isUnique();

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
    public boolean isSerializable()
    {
        return RegisteredTable.isSerializable(getType());
    }

    public abstract boolean isID();

    public abstract boolean isSaveInputAfterLoading();
}
