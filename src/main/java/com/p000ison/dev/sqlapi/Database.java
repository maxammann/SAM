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

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.*;
import java.util.ArrayList;
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
     * The connection to the database
     */
    private Connection connection;
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
        long start = System.currentTimeMillis();
        connection = connect(configuration);
        long finish = System.currentTimeMillis();
        System.out.printf("Check connection took %s!\n", finish - start);
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

    protected abstract Connection connect(DatabaseConfiguration configuration) throws DatabaseConnectionException;

    /**
     * Closes the connection to the database
     *
     * @throws SQLException
     */
    public abstract void close() throws SQLException;

    protected abstract TableBuilder createTableBuilder(Class<? extends TableObject> table);

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

    boolean existsDatabaseTable(String table)
    {
        return getDatabaseTables().contains(table);
    }

    public final Database registerTable(TableObject table)
    {
        return registerTable(table.getClass());
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

    List<String> getDatabaseColumns(String table)
    {
        List<String> columns = new ArrayList<String>();

        try {
            ResultSet columnResult = getMetadata().getColumns(null, null, table, null);


            while (columnResult.next()) {
                columns.add(columnResult.getString("COLUMN_NAME"));
            }
        } catch (SQLException e) {
            throw new QueryException(e);
        }

        return columns;
    }

    private DatabaseMetaData getMetadata()
    {
        try {
            return getConnection().getMetaData();
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }

    public final Set<String> getDatabaseTables()
    {
        Set<String> columns = new HashSet<String>();

        ResultSet columnResult;
        try {
            columnResult = this.getMetadata().getTables(null, null, null, null);

            while (columnResult.next()) {
                columns.add(columnResult.getString("TABLE_NAME"));
            }

        } catch (SQLException e) {
            throw new QueryException(e);
        }

        return columns;
    }

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
        insert(table, tableObject);
    }

    private void insert(RegisteredTable registeredTable, TableObject object)
    {
        registerSaveStatement(registeredTable.getInsertStatement(), registeredTable, object);
    }

    private void update(RegisteredTable registeredTable, TableObject object)
    {
        registerSaveStatement(registeredTable.getUpdateStatement(), registeredTable, object);
    }

    private void registerSaveStatement(PreparedStatement statement, RegisteredTable registeredTable, TableObject object)
    {
        try {
            List<Column> registeredColumns = registeredTable.getRegisteredColumns();
            for (int i = 0; i < registeredColumns.size(); i++) {
                Column column = registeredColumns.get(i);
                Object value = column.getValue(object);

                if (value != null) {
                    if (column.isSupported()) {
                        statement.setObject(i + 1, value, column.getDatabaseDataType());
                    } else if (column.isSerializable()) {
                        Blob blob = getConnection().createBlob();
                        ObjectOutputStream stream = new ObjectOutputStream(blob.setBinaryStream(1));
                        stream.writeObject(value);

                        statement.setBlob(i + 1, blob);
                    }
                } else {
                    statement.setNull(i + 1, column.getDatabaseDataType());
                }
            }

            registeredTable.getInsertStatement().executeUpdate();
        } catch (SQLException e) {
            throw new QueryException(e);
        } catch (IOException e) {

        }
    }

    PreparedStatement prepare(String query)
    {
        try {
            return getConnection().prepareStatement(query);
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }

    protected final Connection getConnection()
    {
        return connection;
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

    void executeDirectUpdate(String query)
    {
        if (query == null) {
            return;
        }
        try {
            getConnection().createStatement().executeUpdate(query);
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }
//
//    ResultSet executeDirectQuery(String query)
//    {
//        if (query == null) {
//            return null;
//        }
//        try {
//            return getConnection().createStatement().executeQuery(query);
//        } catch (SQLException e) {
//            throw new QueryException(e);
//        }
//    }
}
