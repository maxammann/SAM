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

    public static boolean validateColumnName(String name)
    {
        return name.matches("^[a-zA-Z]+$");
    }

    public static boolean validateTableName(String name)
    {
        return validateColumnName(name);
    }

//    private int prepareStatement(String query)
//    {
//        PreparedStatement statement;
//        try {
//            statement = getConnection().prepareStatement(query);
//        } catch (SQLException e) {
//            throw new QueryException(e);
//        }
//        int statementId;
//        if (preparedStatements.isEmpty()) {
//            statementId = 0;
//        } else {
//            statementId = preparedStatements.lastKey() + 1;
//        }
//        preparedStatements.put(statementId, statement);
//        return statementId;
//    }


    /**
     * Closes the connection to the database
     *
     * @throws QueryException
     */
    public abstract void close() throws QueryException;

    protected abstract TableBuilder createTableBuilder(Class<? extends TableObject> table);

    public final Database registerTable(TableObject table)
    {
        return registerTable(table.getClass());
    }

    public final Database registerTable(Class<? extends TableObject> table)
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
        return this;
    }

    public abstract boolean isConnected();

    boolean existsDatabaseTable(String table)
    {
        return getDatabaseTables().contains(table);
    }

//    private List<String> getDatabaseColumns(Class<? extends TableObject> table)
//    {
//        return getDatabaseColumns(getTableName(table));
//    }
//
//    private List<String> getDatabaseColumns(TableObject table)
//    {
//        return getDatabaseColumns(getTableName(table.getClass()));
//    }

    public abstract List<String> getDatabaseColumns(String table);

    public abstract Set<String> getDatabaseTables();

    public RegisteredTable getRegisteredTable(Class<? extends TableObject> table)
    {
        for (RegisteredTable registeredTable : registeredTables) {
            if (registeredTable.isRegisteredClass(table)) {
                return registeredTable;
            }
        }

        return null;
    }

    public void save(TableObject tableObject)
    {
        RegisteredTable table = getRegisteredTable(tableObject.getClass());
        if (table == null) {
            throw new QueryException("The class %s is not registered!");
        }
        if (existsEntry(table, tableObject)) {
            update(table, tableObject);
        } else {
            insert(table, tableObject);
        }
    }

    private void insert(RegisteredTable registeredTable, TableObject object)
    {
        PreparedQuery insert = registeredTable.getInsertStatement();
        setColumnValues(insert, registeredTable, object);
        Column idColumn = registeredTable.getIDColumn();
        idColumn.setValue(object, getLastEntryId(registeredTable));
        insert.update();
    }

    private void update(RegisteredTable registeredTable, TableObject object)
    {
        System.out.println("update");
        PreparedQuery update = registeredTable.getUpdateStatement();
        int i = setColumnValues(update, registeredTable, object);
        Column idColumn = registeredTable.getIDColumn();
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

    protected abstract PreparedQuery createPreparedStatement(String query);

    protected abstract boolean executeDirectUpdate(String query);

    protected abstract boolean existsEntry(RegisteredTable table, TableObject object);

    protected abstract boolean existsEntry(TableObject object);

    protected abstract int getLastEntryId(RegisteredTable table);
}
