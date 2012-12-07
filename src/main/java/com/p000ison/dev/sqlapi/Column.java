package com.p000ison.dev.sqlapi;

/**
 * Represents a column in a database
 */
public interface Column {

    Class<?> getType();

    String getColumnName();

    int getPosition();

    String getDefaultValue();

    int[] getLength();

    boolean isAutoIncrementing();

    boolean isNotNull();

    boolean isUnique();

    boolean isPrimary();
}
