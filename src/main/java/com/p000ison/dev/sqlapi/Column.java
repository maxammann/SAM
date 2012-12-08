package com.p000ison.dev.sqlapi;

/**
 * Represents a column in a database
 */
public interface Column {

    /**
     * Gets the class of the java object which represents this column
     *
     * @return The type of this column
     */
    Class<?> getType();

    /**
     * Gets the name of the column
     *
     * @return The name of the column
     */
    String getColumnName();

    /**
     * Gets the position of the column. This can be any value above or equal 0 or -1 if the order does not matter.
     *
     * @return The position of the column in the table
     */
    int getPosition();

    /**
     * Gets a optional default value for this column or a empty string
     *
     * @return A optional default value
     */
    String getDefaultValue();

    /**
     * Gets the lenght of this column like { 5, 10 } or a empty array if there is no lenght.
     *
     * @return The lenght of the column or a empty array.
     */
    int[] getLength();

    /**
     * Whether this column should autoincrement
     *
     * @return Weather this column should autoincrement
     */
    boolean isAutoIncrementing();

    /**
     * Whether this column can be null
     *
     * @return Whether this column can be null
     */
    boolean isNotNull();

    /**
     * Whether this column is unique
     *
     * @return Whether this column is unique
     */
    boolean isUnique();

    /**
     * Whether this column is primary
     *
     * @return Whether this column is primary
     */
    boolean isPrimary();
}
