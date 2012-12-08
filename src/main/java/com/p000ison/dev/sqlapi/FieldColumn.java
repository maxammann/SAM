package com.p000ison.dev.sqlapi;

import com.p000ison.dev.sqlapi.annotation.DatabaseColumn;
import com.p000ison.dev.sqlapi.exception.TableBuildingException;

import java.lang.reflect.Field;

/**
 * Represents a FieldColumn
 */
public class FieldColumn implements Column {

    private Field field;
    private DatabaseColumn annotation;

    public FieldColumn(Field field, DatabaseColumn annotation)
    {
        this.field = field;
        this.annotation = annotation;
    }

    public FieldColumn(Field field)
    {
        this.field = field;
        this.annotation = field.getAnnotation(DatabaseColumn.class);
        if (annotation == null) {
            throw new TableBuildingException("The field %s is missing the DatabaseColumn annotation! Maybe this field is no column?");
        }
    }

    public Field getField()
    {
        return field;
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
    public String toString()
    {
        return "FieldColumn{" +
                "field=" + field +
                '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldColumn that = (FieldColumn) o;

        if (field != null ? !field.equals(that.field) : that.field != null) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        return field != null ? field.hashCode() : 0;
    }
}
