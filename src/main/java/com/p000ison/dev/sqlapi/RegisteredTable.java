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
 * Last modified: 18.12.12 17:27
 */

package com.p000ison.dev.sqlapi;

import com.p000ison.dev.sqlapi.exception.TableBuildingException;
import com.p000ison.dev.sqlapi.query.PreparedQuery;

import java.io.Serializable;
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
    private PreparedQuery updateStatement, insertStatement;

    RegisteredTable(String name, Class<? extends TableObject> registeredClass, List<Column> registeredColumns, Constructor<? extends TableObject> constructor)
    {
        this.name = name;
        this.registeredClass = registeredClass;
        this.registeredColumns = registeredColumns;
        this.constructor = constructor;
    }

    public boolean isRegistered(TableObject obj)
    {
        return isRegisteredClass(obj.getClass());
    }

    public Column getColumn(String columnName)
    {
        for (Column column : registeredColumns) {
            String name = column.getName();
            if (name.hashCode() == columnName.hashCode() && name.equals(columnName)) {
                return column;
            }
        }
        return null;
    }

    public Column getIDColumn()
    {
        for (Column column : registeredColumns) {
            if (column.isID()) {
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
    public <T> T createNewInstance()
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
            query.append(column.getName()).append("=?,");
            if (column.isID()) {
                id = column;
            }
        }

        if (id == null) {
            throw new TableBuildingException("The table %s does not have an id!", getName());
        }

        query.deleteCharAt(query.length() - 1);
        query.append(" WHERE ").append(id.getName()).append("=?");
        query.append(';');
        updateStatement = database.createPreparedStatement(query.toString());

        query.setLength(0);
        query.append("INSERT INTO ").append(getName()).append(" (");

        for (Column column : getRegisteredColumns()) {
            query.append(column.getName()).append(',');
        }
        query.deleteCharAt(query.length() - 1);
        query.append(") VALUES (");
        for (int i = 0; i < getRegisteredColumns().size(); i++) {
            query.append("?,");
        }
        query.deleteCharAt(query.length() - 1);
        query.append(");");
        insertStatement = database.createPreparedStatement(query.toString());
    }

    public PreparedQuery getUpdateStatement()
    {
        return updateStatement;
    }

    public PreparedQuery getInsertStatement()
    {
        return insertStatement;
    }
}
