package com.p000ison.dev.sqlapi;

import com.p000ison.dev.sqlapi.annotation.DatabaseTable;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

/**
 * Represents a Database
 */
public abstract class Database {

    protected DataSource dataSource;
    protected Connection connection;
    protected DatabaseConfiguration configuration;
    private boolean dropOldColumns = false;
    private TreeMap<Integer, PreparedStatement> preparedStatements = new TreeMap<Integer, PreparedStatement>();

    /**
     * Creates a new database connection based on the configuration
     *
     * @param configuration The database configuration
     * @throws SQLException
     */
    protected Database(DatabaseConfiguration configuration) throws SQLException
    {
        this.configuration = configuration;
        init(configuration);
        connection = dataSource.getConnection();
    }

    /**
     * Gets the name of a table
     *
     * @param clazz The class of the {@link TableObject}.
     * @return The name
     */
    public static String getTableName(Class<? extends TableObject> clazz)
    {
        DatabaseTable annotation = clazz.getAnnotation(DatabaseTable.class);
        return annotation == null ? null : annotation.name();
    }

    public PreparedStatement createPreparedStatement(int id, String query) throws SQLException
    {
        PreparedStatement statement = getConnection().prepareStatement(query);
        preparedStatements.put(id, statement);
        return statement;
    }

    public int createPreparedStatement(String query) throws SQLException
    {
        PreparedStatement statement = getConnection().prepareStatement(query);
        int statementId;
        if (preparedStatements.isEmpty()) {
            statementId = 0;
        } else {
            statementId = preparedStatements.lastKey() + 1;
        }
        preparedStatements.put(statementId, statement);
        return statementId;
    }

    protected abstract void init(DatabaseConfiguration configuration);

    /**
     * Closes the connection to the database
     *
     * @throws SQLException
     */
    public abstract void close() throws SQLException;

    TableBuilder createTableBuilder(TableObject table)
    {
        return createTableBuilder(table.getClass());
    }

    protected abstract TableBuilder createTableBuilder(Class<? extends TableObject> table);

    public final Database registerTable(Class<? extends TableObject> table) throws SQLException
    {
        TableBuilder builder = createTableBuilder(table);
        String tableQuery = builder.createTable().getQuery();
        System.out.println(tableQuery);
        getConnection().prepareStatement(tableQuery).execute();
        return this;
    }

    public boolean existsDatabaseTable(String table) throws SQLException
    {
        ResultSet set = getConnection().getMetaData().getTables(null, null, table, null);
        return set.next();
    }


    public final Database registerTable(TableObject table) throws SQLException
    {
        return registerTable(table.getClass());
    }

    public final Set<String> getDatabaseColumns(Class<? extends TableObject> table) throws SQLException
    {
        return getDatabaseColumns(getTableName(table));
    }

    public final Set<String> getDatabaseColumns(TableObject table) throws SQLException
    {
        return getDatabaseColumns(getTableName(table.getClass()));
    }

    protected final Set<String> getDatabaseColumns(String table) throws SQLException
    {
        Set<String> columns = new HashSet<String>();

        ResultSet columnResult = this.getConnection().getMetaData().getColumns(null, null, table, null);

        while (columnResult.next()) {
            columns.add(columnResult.getString("COLUMN_NAME"));
        }

        return columns;
    }

    public final Set<String> getDatabaseTables() throws SQLException
    {
        Set<String> columns = new HashSet<String>();

        ResultSet columnResult = this.getConnection().getMetaData().getTables(null, null, null, null);

        while (columnResult.next()) {
            columns.add(columnResult.getString("TABLE_NAME"));
        }

        return columns;
    }

    public final Connection getConnection() throws SQLException
    {
        return connection;
    }

    public DatabaseConfiguration getConfiguration()
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
}
