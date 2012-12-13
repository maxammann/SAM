package com.p000ison.dev.sqlapi;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Represents a RegisteredTable
 */
public class RegisteredTable {
    private String name;
    private Class<? extends TableObject> registeredClass;
    private List<Column> registeredColumns;
    private Constructor<? extends TableObject> constructor;

    RegisteredTable(String name, Class<? extends TableObject> registeredClass, List<Column> registeredColumns, Constructor<? extends TableObject> constructor)
    {
        this.name = name;
        this.registeredClass = registeredClass;
        this.registeredColumns = registeredColumns;
        this.constructor = constructor;
    }

    public boolean isRegistered(Object obj)
    {
        return obj.getClass().equals(registeredClass);
    }

    public Column getColumn(String columnName)
    {
        for (Column column : registeredColumns) {
            String name = column.getColumnName();
            if (name.hashCode() == columnName.hashCode() && name.equals(columnName)) {
                return column;
            }
        }
        return null;
    }

    public List<Column> getRegisteredColumns()
    {
        return registeredColumns;
    }

    public boolean isRegisteredClass(Class<? extends TableObject> registeredClass)
    {
        return this.registeredClass.equals(registeredClass);
    }

    @SuppressWarnings("unchecked")
    <T> T createNewInstance()
    {
        try {
            return (T) constructor.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getName()
    {
        return name;
    }

    Class<? extends TableObject> getRegisteredClass()
    {
        return registeredClass;
    }
}
