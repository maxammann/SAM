package com.p000ison.dev.sqlapi;

import java.util.List;

/**
 * Represents a RegisteredTable
 */
class RegisteredTable {
    private String name;
    private Class<? extends TableObject> registeredClass;
    private List<Column> registeredColumns;

    RegisteredTable(String name, Class<? extends TableObject> registeredClass, List<Column> registeredColumns)
    {
        this.name = name;
        this.registeredClass = registeredClass;
        this.registeredColumns = registeredColumns;
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

    public boolean isRegisteredClass(Class<? extends TableObject> registeredClass) {
        return this.registeredClass.equals(registeredClass);
    }

    public String getName()
    {
        return name;
    }
}
