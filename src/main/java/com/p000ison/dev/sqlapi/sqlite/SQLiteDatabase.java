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
 * Last modified: 29.12.12 16:24
 */

package com.p000ison.dev.sqlapi.sqlite;

import com.p000ison.dev.sqlapi.DatabaseConfiguration;
import com.p000ison.dev.sqlapi.TableBuilder;
import com.p000ison.dev.sqlapi.TableObject;
import com.p000ison.dev.sqlapi.exception.DatabaseConnectionException;
import com.p000ison.dev.sqlapi.jbdc.JBDCDatabase;
import org.sqlite.SQLiteDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Represents a SQLiteDatabase
 */
public final class SQLiteDatabase extends JBDCDatabase {

    public SQLiteDatabase(DatabaseConfiguration configuration) throws DatabaseConnectionException {
        super(configuration);
    }

    @Override
    protected Connection connect(DatabaseConfiguration configuration) throws DatabaseConnectionException {
        SQLiteDataSource dataSource = new SQLiteDataSource();

        SQLiteConfiguration SQLiteConfiguration = (SQLiteConfiguration) configuration;

        dataSource.setUrl("jdbc:sqlite:" + SQLiteConfiguration.getLocation().getAbsolutePath());

        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new DatabaseConnectionException(configuration, e);
        }
    }

    @Override
    protected TableBuilder createTableBuilder(Class<? extends TableObject> table) {
        return new SQLiteTableBuilder(table, this);
    }

    @Override
    public SQLiteConfiguration getConfiguration() {
        return (SQLiteConfiguration) super.getConfiguration();
    }

    @Override
    public boolean isAutoReset() {
        return true;
    }

    @Override
    public String getEngineName() {
        return "SQLite";
    }
}
