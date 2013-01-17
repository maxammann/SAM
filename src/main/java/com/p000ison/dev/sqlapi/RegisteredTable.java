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
 * Last modified: 29.12.12 16:03
 */

package com.p000ison.dev.sqlapi;

import com.p000ison.dev.sqlapi.exception.QueryException;
import com.p000ison.dev.sqlapi.exception.RegistrationException;
import com.p000ison.dev.sqlapi.exception.TableBuildingException;
import com.p000ison.dev.sqlapi.query.PreparedQuery;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Used to register TableObjects. Holds the columns, the registered constructor, some prepared statements and of the class
 * or the TableObject.
 */
public class RegisteredTable {
    private String name;
    private Class<? extends TableObject> registeredClass;
    private List<Column> registeredColumns;
    private RegisteredConstructor constructor;
    private PreparedQuery updateStatement, insertStatement, deleteStatement;
    private Queue<StoredTableObjectValue> storedColumnValues = new LinkedList<StoredTableObjectValue>();

    RegisteredTable(String name, Class<? extends TableObject> registeredClass, List<Column> registeredColumns, Constructor<? extends TableObject> constructor) {
        this.name = name;
        this.registeredClass = registeredClass;
        this.registeredColumns = registeredColumns;
        if (constructor != null) {
            this.constructor = new RegisteredConstructor(constructor);
        }
    }

    public boolean isRegistered(TableObject obj) {
        return isRegisteredClass(obj.getClass());
    }

    public Column getColumn(String columnName) {
        for (Column column : registeredColumns) {
            String name = column.getName();
            if (name.hashCode() == columnName.hashCode() && name.equals(columnName)) {
                return column;
            }
        }
        return null;
    }

    public Column getIDColumn() {
        for (Column column : registeredColumns) {
            if (column.isID()) {
                return column;
            }
        }
        return null;
    }

    public List<Column> getRegisteredColumns() {
        return registeredColumns;
    }

    public boolean isRegisteredClass(Class<? extends TableObject> registeredClass) {
        return this.registeredClass.equals(registeredClass);
    }

    public <T extends TableObject> T createNewInstance() {
        if (constructor == null) {
            throw new QueryException("No default constructor and no constructor registered for class %s!", registeredClass.getName());
        }
        return constructor.newInstance();
    }

    public String getName() {
        return name;
    }

    public static boolean isSerializable(Class<?> clazz) {
        for (Class interfacee : clazz.getInterfaces()) {
            if (interfacee == Serializable.class) {
                return true;
            }
        }

        return false;
    }

    void prepareAllStatements(Database database) {
        prepareDeleteStatement(database);
        prepareInsertStatement(database);
        prepareUpdateStatement(database);
    }

    void prepareUpdateStatement(Database database) {
        StringBuilder query = new StringBuilder("UPDATE ").append(getName()).append(" SET ");
        Column id = null;
        for (Column column : getRegisteredColumns()) {
            if (column.isID()) {
                id = column;
                continue;
            }
            query.append(column.getName()).append("=?,");
        }

        if (id == null) {
            throw new TableBuildingException("The table %s does not have an id!", getName());
        }

        query.deleteCharAt(query.length() - 1);
        query.append(" WHERE ").append(id.getName()).append("=?");
        query.append(';');
        updateStatement = database.createPreparedStatement(query.toString());
    }

    void prepareDeleteStatement(Database database) {
        StringBuilder query = new StringBuilder();
        Column id = getIDColumn();
        query.append("DELETE FROM ").append(getName()).append(" WHERE ").append(id.getName()).append("=?;");

        deleteStatement = database.createPreparedStatement(query.toString());
    }

    void prepareInsertStatement(Database database) {
        StringBuilder query = new StringBuilder();
        Column id = getIDColumn();
        query.append("INSERT INTO ").append(getName()).append(" (");

        for (Column column : getRegisteredColumns()) {
            if (column.equals(id)) {
                continue;
            }
            query.append(column.getName()).append(',');
        }
        query.deleteCharAt(query.length() - 1);
        query.append(") VALUES (");
        for (int i = 0; i < getRegisteredColumns().size() - 1; i++) {
            query.append("?,");
        }
        query.deleteCharAt(query.length() - 1);
        query.append(");");

        insertStatement = database.createPreparedStatement(query.toString());
    }

    public PreparedQuery getPreparedUpdateStatement() {
        return updateStatement;
    }

    /**
     * Registers a constructor which will be used to build the objects, just pass for example: "test", 5 in it to
     * find a constructor with the parameters String and int.
     *
     * @param arguments Will be used to build the object, pass in nothing to use the default constructor
     * @return A registered constructor
     */
    public RegisteredConstructor registerConstructor(Object... arguments) {
        try {
            if (arguments.length == 0) {
                constructor = new RegisteredConstructor(registeredClass.getConstructor());
            } else {
                constructor = new RegisteredConstructor(registeredClass, arguments);
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(String.format("Constructor for class %s not found!", getName()), e);
        }

        return constructor;
    }

    /**
     * Registers a constructor which will be used to build the objects, just pass for example: String and int in it to
     * find a constructor with the parameters String and int.
     *
     * @param arguments Will be used to build the object, pass in nothing to use the default constructor
     * @return A registered constructor
     */
    public RegisteredConstructor registerConstructor(Class... arguments) {
        try {
            if (arguments.length == 0) {
                constructor = new RegisteredConstructor(registeredClass.getConstructor());
            } else {
                constructor = new RegisteredConstructor(registeredClass, arguments);
            }
        } catch (NoSuchMethodException e) {
            throw new RegistrationException(e, "Failed at registering the constructor! Constructor not found!");
        }

        return constructor;
    }

    public PreparedQuery getPreparedInsertStatement() {
        return insertStatement;
    }

    public void storeColumnValue(Column column, Object value, TableObject tableObject) {
        storedColumnValues.add(new StoredTableObjectValue(tableObject, value, column));
    }

    public void saveStoredValues() {
        StoredTableObjectValue value;

        while ((value = storedColumnValues.poll()) != null) {
            value.getColumn().setValue(value.getTableObject(), value.getValue());
        }
    }

    public void clearStoredValues() {
        storedColumnValues.clear();
    }

    public PreparedQuery getDeleteStatement() {
        return deleteStatement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RegisteredTable that = (RegisteredTable) o;

        return !(name != null ? !name.equals(that.name) : that.name != null);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    public void close() {
        updateStatement.close();
        insertStatement.close();
        deleteStatement.close();
    }

    PreparedQuery createFullInsertStatement(Database database) {
        StringBuilder query = new StringBuilder();
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

        return database.createPreparedStatement(query.toString());
    }
}
