package com.p000ison.dev.sqlapi.mysql;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.p000ison.dev.sqlapi.Database;
import com.p000ison.dev.sqlapi.DatabaseConfiguration;
import com.p000ison.dev.sqlapi.TableBuilder;
import com.p000ison.dev.sqlapi.TableObject;
import com.p000ison.dev.sqlapi.exception.DatabaseConnectionException;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Represents a SQLiteDatabase
 */
public final class MySQLDatabase extends Database {

    private Connection connection;

    public MySQLDatabase(MySQLConfiguration configuration) throws SQLException
    {
        super(configuration);
    }

    @Override
    protected void connect(DatabaseConfiguration configuration) throws DatabaseConnectionException
    {
        MysqlDataSource dataSource = new MysqlDataSource();
        MySQLConfiguration mysqlConfiguration = (MySQLConfiguration) configuration;

        dataSource.setUser(mysqlConfiguration.getUser());
        dataSource.setPassword(mysqlConfiguration.getPassword());
        dataSource.setDatabaseName(mysqlConfiguration.getDatabase());
        dataSource.setServerName(mysqlConfiguration.getHost());
        dataSource.setPort(mysqlConfiguration.getPort());

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
        return new MySQLTableBuilder(table, this);
    }

    @Override
    protected Connection getConnection()
    {
        return connection;
    }

    @Override
    public MySQLConfiguration getConfiguration()
    {
        return (MySQLConfiguration) super.getConfiguration();
    }
}
