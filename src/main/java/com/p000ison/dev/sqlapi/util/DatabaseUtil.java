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
 * Last modified: 18.12.12 18:22
 */

package com.p000ison.dev.sqlapi.util;

import com.p000ison.dev.sqlapi.Column;

/**
 * Represents a DatabaseUtil
 */
public final class DatabaseUtil {

    private DatabaseUtil()
    {
    }

    public static Object validateColumnValue(Object value, Column column)
    {
        Class type = column.getType();
        if (type.equals(String.class)) {
            return "\"" + value + "\"";
        }
        return value;
    }

    public static boolean validateColumnName(String name)
    {
        return name.matches("^[a-zA-Z]+$");
    }

    public static boolean validateTableName(String name)
    {
        return validateColumnName(name);
    }

    public static boolean isSupported(Class type)
    {
        return type == boolean.class
                || type == Boolean.class
                || type == byte.class
                || type == Byte.class
                || type == short.class
                || type == Short.class
                || type == int.class
                || type == Integer.class
                || type == float.class
                || type == Float.class
                || type == double.class
                || type == Double.class
                || type == long.class
                || type == Long.class
                || type == char.class
                || type == Character.class
                || type == String.class;
    }
}
