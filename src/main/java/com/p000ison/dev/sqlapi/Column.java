package com.p000ison.dev.sqlapi;

/**
 * Represents a Column
 */
public abstract class Column {
    private int databaseType;


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
    public abstract String getColumnName();

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
     * Whether this column is primary
     *
     * @return Whether this column is primary
     */
    public abstract boolean isPrimary();

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
    public abstract boolean isSerializable();

    public abstract boolean isID();

    public int getDatabaseDataType()
    {
        return databaseType;
    }

    void setDatabaseType(int databaseType)
    {
        this.databaseType = databaseType;
    }
}
