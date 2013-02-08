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
 * Last modified: 25.12.12 17:50
 */

package com.p000ison.dev.sqlapi.exception;

import com.p000ison.dev.sqlapi.DatabaseConfiguration;

import java.sql.SQLException;

/**
 * Represents a DatabaseConnectionException
 */
public class DatabaseConnectionException extends RuntimeException {

    private final DatabaseConfiguration config;

    public DatabaseConnectionException(DatabaseConfiguration config, SQLException cause) {
        super(cause);
        this.config = config;
    }

    public DatabaseConnectionException(DatabaseConfiguration config, String cause) {
        super(cause);
        this.config = config;
    }

    public DatabaseConfiguration getConfig() {
        return config;
    }
}
