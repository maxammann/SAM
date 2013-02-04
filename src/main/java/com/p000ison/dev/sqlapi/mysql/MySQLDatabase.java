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
 * Last modified: 27.12.12 17:01
 */

package com.p000ison.dev.sqlapi.mysql;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.p000ison.dev.sqlapi.DatabaseConfiguration;
import com.p000ison.dev.sqlapi.TableBuilder;
import com.p000ison.dev.sqlapi.TableObject;
import com.p000ison.dev.sqlapi.exception.DatabaseConnectionException;
import com.p000ison.dev.sqlapi.jbdc.JBDCDatabase;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Represents a SQLiteDatabase
 */
public final class MySQLDatabase extends JBDCDatabase {

    public MySQLDatabase(DatabaseConfiguration configuration) throws DatabaseConnectionException {
        super(configuration);
    }

    @Override
    protected Connection connect(DatabaseConfiguration configuration) throws DatabaseConnectionException {
        MysqlDataSource dataSource = new MysqlDataSource();
        MySQLConfiguration mysqlConfiguration = (MySQLConfiguration) configuration;

        dataSource.setUser(mysqlConfiguration.getUser());
        dataSource.setPassword(mysqlConfiguration.getPassword());
        dataSource.setDatabaseName(mysqlConfiguration.getDatabase());
        dataSource.setServerName(mysqlConfiguration.getHost());
        dataSource.setPort(mysqlConfiguration.getPort());

        dataSource.setAutoClosePStmtStreams(false);
        dataSource.setAutoReconnect(configuration.isAutoReconnect());

        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new DatabaseConnectionException(configuration, e);
        }
    }

    @Override
    protected TableBuilder createTableBuilder(Class<? extends TableObject> table) {
        return new MySQLTableBuilder(table, this);
    }

    @Override
    public MySQLConfiguration getConfiguration() {
        return (MySQLConfiguration) super.getConfiguration();
    }

    @Override
    public boolean isAutoReset() {
        return false;
    }

    @Override
    public String getEngineName() {
        return "MySQL";
    }
}
