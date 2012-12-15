package com.p000ison.dev.sqlapi.sqlite;

import com.p000ison.dev.sqlapi.Database;
import com.p000ison.dev.sqlapi.DatabaseConfiguration;
import com.p000ison.dev.sqlapi.TableBuilder;
import com.p000ison.dev.sqlapi.TableObject;
import com.p000ison.dev.sqlapi.exception.DatabaseConnectionException;
import org.sqlite.SQLiteDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Represents a SQLiteDatabase
 */
public final class SQLiteDatabase extends Database {

    private Connection connection;

    public SQLiteDatabase(SQLiteConfiguration configuration) throws SQLException
    {
        super(configuration);
    }

    @Override
    protected void connect(DatabaseConfiguration configuration) throws DatabaseConnectionException
    {
        SQLiteDataSource dataSource = new SQLiteDataSource();

        SQLiteConfiguration SQLiteConfiguration = (SQLiteConfiguration) configuration;

        dataSource.setUrl("jdbc:sqlite:" + SQLiteConfiguration.getLocation().getAbsolutePath());

        try {
            this.connection = dataSource.getConnection();
        } catch (SQLException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    @Override
    public void close() throws SQLException
    {
        getConnection().close();
    }

    @Override
    protected TableBuilder createTableBuilder(Class<? extends TableObject> table)
    {
        return new SQLiteTableBuilder(table, this);
    }

    @Override
    protected Connection getConnection()
    {
        return connection;
    }

    @Override
    public SQLiteConfiguration getConfiguration()
    {
        return (SQLiteConfiguration) super.getConfiguration();
    }
}
