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
 * Last modified: 18.12.12 18:29
 */

package com.p000ison.dev.sqlapi;

import com.p000ison.dev.sqlapi.annotation.DatabaseTable;
import com.p000ison.dev.sqlapi.exception.DatabaseConnectionException;
import com.p000ison.dev.sqlapi.exception.QueryException;
import com.p000ison.dev.sqlapi.query.PreparedQuery;
import com.p000ison.dev.sqlapi.query.SelectQuery;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a Database
 */
public abstract class Database {

    /**
     * The configuration object which holds all settings
     */
    private DatabaseConfiguration configuration;
    /**
     * Whether old columns should be dropped
     */
    private boolean dropOldColumns = false;
    /**
     * A map of registered tables (classes) and a list of columns
     */
    private Set<RegisteredTable> registeredTables = new HashSet<RegisteredTable>();

    /**
     * Creates a new database connection based on the configuration
     *
     * @param configuration The database configuration
     */
    protected Database(DatabaseConfiguration configuration) throws DatabaseConnectionException
    {
        this.configuration = configuration;
        String driver = configuration.getDriverName();
        try {
            Class.forName(driver);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load driver " + driver + "!");
        }
    }

    /**
     * Gets the name of a table
     *
     * @param clazz The class of the {@link TableObject}.
     * @return The name
     */
    static String getTableName(Class<? extends TableObject> clazz)
    {
        DatabaseTable annotation = clazz.getAnnotation(DatabaseTable.class);
        return annotation == null ? null : annotation.name();
    }

    /**
     * Validates the name of a column
     *
     * @param name The name of the column
     * @return Whether it is valid
     */
    public static boolean validateColumnName(String name)
    {
        return name.matches("^[a-zA-Z]+$");
    }

    /**
     * Validates the name of a table
     *
     * @param name The name of the table
     * @return Whether it is valid
     */
    public static boolean validateTableName(String name)
    {
        return validateColumnName(name);
    }

    /**
     * Closes the connection to the database
     *
     * @throws QueryException
     */
    public abstract void close() throws QueryException;

    /**
     * Creates a new instance of a TableBuilder. This is used to build the queries to create/modify a table.
     *
     * @param table The class of the TableObject
     * @return The TableBuilder
     */
    protected abstract TableBuilder createTableBuilder(Class<? extends TableObject> table);

    /**
     * Registers a new TableObject for further use
     *
     * @param table The object to register
     */
    public final void registerTable(TableObject table)
    {
        registerTable(table.getClass());
    }

    /**
     * Registers a class for further use
     *
     * @param table The class to register
     */
    public synchronized final void registerTable(Class<? extends TableObject> table)
    {
        long start = System.currentTimeMillis();
        TableBuilder builder = createTableBuilder(table);

        long finish = System.currentTimeMillis();
        System.out.printf("Check register took %s!\n", finish - start);
        RegisteredTable registeredTable = new RegisteredTable(builder.getTableName(), table, builder.getColumns(), builder.getDefaultConstructor());

        registeredTable.prepareSaveStatement(this);
        registeredTables.add(registeredTable);

        String tableQuery = builder.createTable().getQuery();
        System.out.println("Create Query:" + tableQuery);

        String modifyQuery = builder.createModifyQuery().getQuery();
        System.out.println("Modify Query:" + modifyQuery);

        executeDirectUpdate(tableQuery);
        executeDirectUpdate(modifyQuery);
    }

    /**
     * Checks whether this the connection to the database is still established
     *
     * @return Whether the the the connection is still established
     */
    public abstract boolean isConnected();

    /**
     * Checks whether the database exists already.
     *
     * @param table The table to check for
     * @return Whether the table exists
     */
    public abstract boolean existsDatabaseTable(String table);

    /**
     * Gets a list of all columns in the database.
     *
     * @param table The table to look up
     * @return A list of columns
     */
    public abstract List<String> getDatabaseColumns(String table);

    /**
     * Returns the RegisteredTable of a registered class
     *
     * @param table The table to look for
     * @return The RegisteredTable
     */
    public synchronized RegisteredTable getRegisteredTable(Class<? extends TableObject> table)
    {
        for (RegisteredTable registeredTable : registeredTables) {
            if (registeredTable.isRegisteredClass(table)) {
                return registeredTable;
            }
        }

        return null;
    }

    /**
     * Constructs a new SelectQuery for further use. This should be synchronized with the Database instance
     *
     * @param <T> a TableObject type
     * @return The SelectQuery
     */
    public abstract <T extends TableObject> SelectQuery<T> select();

    public void save(TableObject tableObject)
    {
        synchronized (this) {
            RegisteredTable table = getRegisteredTable(tableObject.getClass());
            if (table == null) {
                throw new QueryException("The class %s is not registered!");
            }

            Column idColumn = table.getIDColumn();

            if (((Number) idColumn.getValue(tableObject)).longValue() <= 0 || !existsEntry(table, tableObject)) {
                insert(table, tableObject, idColumn);
            } else {
                update(table, tableObject, idColumn);
            }
        }
    }

    private void insert(RegisteredTable registeredTable, TableObject object, Column idColumn)
    {
        PreparedQuery insert = registeredTable.getInsertStatement();
        setColumnValues(insert, registeredTable, object);
        idColumn.setValue(object, getLastEntryId(registeredTable));
        insert.update();
    }

    private void update(RegisteredTable registeredTable, TableObject object, Column idColumn)
    {
        PreparedQuery update = registeredTable.getUpdateStatement();
        int i = setColumnValues(update, registeredTable, object);
        update.set(idColumn, i, idColumn.getValue(object));
        update.update();
    }

    private int setColumnValues(PreparedQuery statement, RegisteredTable registeredTable, TableObject object)
    {
        List<Column> registeredColumns = registeredTable.getRegisteredColumns();
        int i = 0;
        for (; i < registeredColumns.size(); i++) {
            Column column = registeredColumns.get(i);
            Object value = column.getValue(object);

            statement.set(column, i, value);
        }
        return i;
    }

    protected DatabaseConfiguration getConfiguration()
    {
        return configuration;
    }

    public final boolean isDropOldColumns()
    {
        return dropOldColumns;
    }

    public final void setDropOldColumns(boolean dropOldColumns)
    {
        this.dropOldColumns = dropOldColumns;
    }

    /**
     * Creates a new PreparedQuery which can be executed now or later.
     *
     * @param query The query to prepare
     * @return A PreparedQuery
     */
    protected abstract PreparedQuery createPreparedStatement(String query);

    protected abstract boolean executeDirectUpdate(String query);

    protected abstract boolean existsEntry(RegisteredTable table, TableObject object);

    protected abstract boolean existsEntry(TableObject object);

    protected abstract int getLastEntryId(RegisteredTable table);
}
