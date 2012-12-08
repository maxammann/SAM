package com.p000ison.dev.sqlapi;

import com.p000ison.dev.sqlapi.annotation.DatabaseTable;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a Database
 */
public abstract class Database {

    protected DataSource dataSource;
    protected Connection connection;
    protected DatabaseConfiguration configuration;
    private boolean dropOldColumns = false;

    protected Database(DatabaseConfiguration configuration) throws SQLException
    {
        this.configuration = configuration;
        init();
        connection = dataSource.getConnection();
    }

    public static String getTableName(Class<? extends TableObject> clazz)
    {
        DatabaseTable annotation = clazz.getAnnotation(DatabaseTable.class);
        return annotation == null ? null : annotation.name();
    }

    protected abstract void init();

    public abstract void close() throws SQLException;

    protected final TableBuilder createTableBuilder(TableObject table)
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
