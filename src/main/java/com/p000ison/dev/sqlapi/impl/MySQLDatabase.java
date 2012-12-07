package com.p000ison.dev.sqlapi.impl;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.p000ison.dev.sqlapi.Database;
import com.p000ison.dev.sqlapi.TableBuilder;
import com.p000ison.dev.sqlapi.TableObject;

import java.sql.SQLException;

/**
 * Represents a MySQLDatabase
 */
public class MySQLDatabase extends Database {

    public MySQLDatabase(MySQLConfiguration configuration) throws SQLException
    {
        super(configuration);
    }

    @Override
    protected void init()
    {
        dataSource = new MysqlDataSource();

        MysqlDataSource mysqlSource = (MysqlDataSource) dataSource;
        MySQLConfiguration mysqlConfiguration = (MySQLConfiguration) configuration;

        mysqlSource.setUser(mysqlConfiguration.getUser());
        mysqlSource.setPassword(mysqlConfiguration.getPassword());
        mysqlSource.setDatabaseName(mysqlConfiguration.getDatabase());
        mysqlSource.setServerName(mysqlConfiguration.getHost());
        mysqlSource.setPort(mysqlConfiguration.getPort());
    }

    @Override
    public void close() throws SQLException
    {
        getConnection().close();
    }

    @Override
    protected TableBuilder createTableBuilder(TableObject table)
    {
        return new MySQLTableBuilder(table, this);
    }

    @Override
    public MySQLConfiguration getConfiguration()
    {
        return (MySQLConfiguration) super.getConfiguration();
    }
}
