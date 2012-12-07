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

    protected abstract void init();

    public abstract void close() throws SQLException;

    protected abstract TableBuilder createTableBuilder(TableObject table);

    public <T extends TableObject> T registerTable(T table) throws SQLException
    {
        System.out.println(createTableBuilder(table).createTable().getQuery());
        getConnection().prepareStatement(createTableBuilder(table).createTable().getQuery()).execute();
//        String query = createTableBuilder(table).createModifyQuery().getQuery();
//        System.out.println(query);
//        getConnection().prepareStatement(query).execute();


        return table;
    }

    public static String getTableName(Class<? extends TableObject> clazz)
    {
        DatabaseTable annotation = clazz.getAnnotation(DatabaseTable.class);
        return annotation == null ? null : annotation.name();
    }

    public Set<String> getDatabaseColumns(Class<? extends TableObject> table) throws SQLException
    {
        return getDatabaseColumns(getTableName(table));
    }

    public Set<String> getDatabaseColumns(TableObject table) throws SQLException
    {
        return getDatabaseColumns(getTableName(table.getClass()));
    }

    protected Set<String> getDatabaseColumns(String table) throws SQLException
    {
        Set<String> columns = new HashSet<String>();

        ResultSet columnResult = this.getConnection().getMetaData().getColumns(null, null, table, null);

        while (columnResult.next()) {
            columns.add(columnResult.getString("COLUMN_NAME"));
        }

        return columns;
    }

    public Connection getConnection() throws SQLException
    {
        return connection;
    }

    public DatabaseConfiguration getConfiguration()
    {
        return configuration;
    }

    public boolean isDropOldColumns()
    {
        return dropOldColumns;
    }

    public void setDropOldColumns(boolean dropOldColumns)
    {
        this.dropOldColumns = dropOldColumns;
    }
}
