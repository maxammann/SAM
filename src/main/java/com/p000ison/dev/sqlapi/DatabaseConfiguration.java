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
 * Last modified: 27.12.12 22:31
 */

package com.p000ison.dev.sqlapi;

import java.util.HashMap;
import java.util.Map;

/**
 * The Configuration for a {@link Database} connection.
 */
public abstract class DatabaseConfiguration {
    private final Map<String, Object> properties;
    private final String driver;

    protected DatabaseConfiguration(String driver) {
        this.driver = driver;
        properties = new HashMap<String, Object>();
    }

    public final void setProperty(String key, Object property) {
        properties.put(key, property);
    }

    public final String getStringProperty(String key) {
        return properties.get(key).toString();
    }

    public final int getIntegerProperty(String key) {
        return (Integer) properties.get(key);
    }

    public final Object getProperty(String key) {
        return properties.get(key);
    }

    public final String getDriverName() {
        return driver;
    }

    public final Class getDriver() throws ClassNotFoundException {
        return Class.forName(driver);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DatabaseConfiguration that = (DatabaseConfiguration) o;

        if (driver != null ? !driver.equals(that.driver) : that.driver != null) return false;
        if (properties != null ? !properties.equals(that.properties) : that.properties != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = properties != null ? properties.hashCode() : 0;
        result = 31 * result + (driver != null ? driver.hashCode() : 0);
        return result;
    }

    public boolean isAutoReconnect() {
        Object reconnect = properties.get("autoReconnect");

        if (reconnect instanceof Boolean) {
            return (Boolean) reconnect;
        }

        return false;
    }

    public void setAutoReconnect(boolean reconnect) {
        if (!reconnect) {
            properties.remove("autoReconnect");
        } else {
            properties.put("autoReconnect", true);
        }
    }
}
