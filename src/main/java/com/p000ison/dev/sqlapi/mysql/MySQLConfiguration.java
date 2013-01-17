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
 * Last modified: 18.12.12 18:30
 */

package com.p000ison.dev.sqlapi.mysql;

import com.p000ison.dev.sqlapi.DatabaseConfiguration;

/**
 * Represents a SQLiteConfiguration
 */
public final class MySQLConfiguration extends DatabaseConfiguration {

    public MySQLConfiguration(String user, String password, String host, int port, String database) {
        super("com.mysql.jdbc.Driver");
        setUser(user);
        setPassword(password);
        setPort(port);
        setHost(host);
        setDatabase(database);
    }

    public String getUser() {
        return super.getStringProperty("user");
    }

    public String getDatabase() {
        return super.getStringProperty("db");
    }

    public String getPassword() {
        return super.getStringProperty("pw");
    }

    public int getPort() {
        return super.getIntegerProperty("port");
    }

    public String getHost() {
        return super.getStringProperty("host");
    }

    public MySQLConfiguration setUser(String user) {
        super.setProperty("user", user);
        return this;
    }

    public MySQLConfiguration setPassword(String pw) {
        super.setProperty("pw", pw);
        return this;
    }

    public MySQLConfiguration setDatabase(String db) {
        super.setProperty("db", db);
        return this;
    }

    public MySQLConfiguration setPort(int port) {
        super.setProperty("port", port);
        return this;
    }

    public MySQLConfiguration setHost(String host) {
        super.setProperty("host", host);
        return this;
    }
}
