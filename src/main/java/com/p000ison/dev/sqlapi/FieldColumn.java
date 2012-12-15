package com.p000ison.dev.sqlapi;

import com.p000ison.dev.sqlapi.annotation.DatabaseColumn;

import java.lang.reflect.Field;

/**
 * Represents a FieldColumn
 */
final class FieldColumn extends Column {

    private Field field;
    private DatabaseColumn annotation;

    FieldColumn(Field field, DatabaseColumn annotation)
    {
        this.field = field;
        field.setAccessible(true);
        this.annotation = annotation;
    }

    @Override
    public Class<?> getType()
    {
        return field.getType();
    }

    @Override
    public String getColumnName()
    {
        return annotation.databaseName();
    }

    @Override
    public int getPosition()
    {
        return annotation.position();
    }

    @Override
    public String getDefaultValue()
    {
        return annotation.defaultValue();
    }

    @Override
    public int[] getLength()
    {
        return annotation.lenght();
    }

    @Override
    public boolean isAutoIncrementing()
    {
        return annotation.autoIncrement();
    }

    @Override
    public boolean isNotNull()
    {
        return annotation.notNull();
    }

    @Override
    public boolean isUnique()
    {
        return annotation.unique();
    }

    @Override
    public boolean isPrimary()
    {
        return annotation.primary();
    }

    @Override
    public void setValue(TableObject tableObject, Object object)
    {
        try {
            field.set(tableObject, object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object getValue(TableObject tableObject)
    {
        try {
            return field.get(tableObject);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString()
    {
        return "FieldColumn{" +
                "column-name=" + getColumnName() +
                ", field=" + (field == null ? null : field.getName()) +
                '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldColumn that = (FieldColumn) o;

        return !(field != null ? !field.equals(that.field) : that.field != null);
    }

    @Override
    public boolean isSerializable()
    {
        return RegisteredTable.isSerializable(getType());
    }

    @Override
    public boolean isID()
    {
        return annotation.id();
    }

    @Override
    public int hashCode()
    {
        return field != null ? field.hashCode() : 0;
    }
}
