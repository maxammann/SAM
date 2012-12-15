package com.p000ison.dev.sqlapi;

import com.p000ison.dev.sqlapi.exception.TableBuildingException;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.util.List;

/**
 * Represents a RegisteredTable
 */
public class RegisteredTable {
    private String name;
    private Class<? extends TableObject> registeredClass;
    private List<Column> registeredColumns;
    private Constructor<? extends TableObject> constructor;
    private PreparedStatement updateStatement, insertStatement;

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

    public static boolean isSerializable(Class<?> clazz)
    {
        for (Class interfacee : clazz.getInterfaces()) {
            if (interfacee == Serializable.class) {
                return true;
            }
        }

        return false;
    }

    void prepareSaveStatement(Database database)
    {
        StringBuilder query = new StringBuilder("UPDATE ").append(getName()).append(" SET ");
        Column id = null;
        for (Column column : getRegisteredColumns()) {
            query.append(column.getColumnName()).append("=?,");
            if (column.isID()) {
                id = column;
            }
        }
        if (id == null) {
            throw new TableBuildingException("The table %s does not have an id!", getName());
        }
        query.append(" WHERE ").append(id.getColumnName()).append("=?");
        query.deleteCharAt(query.length() - 1);
        query.append(';');
        updateStatement = database.prepare(query.toString());

        query.setLength(0);
        query.append("INSERT INTO ").append(getName()).append(" (");

        for (Column column : getRegisteredColumns()) {
            query.append(column.getColumnName()).append(',');
        }
        query.deleteCharAt(query.length() - 1);
        query.append(") VALUES (");
        for (int i = 0; i < getRegisteredColumns().size(); i++) {
            query.append("?,");
        }
        query.deleteCharAt(query.length() - 1);
        query.append(");");
        insertStatement = database.prepare(query.toString());
    }

    public PreparedStatement getUpdateStatement()
    {
        return updateStatement;
    }

    public PreparedStatement getInsertStatement()
    {
        return insertStatement;
    }
}
