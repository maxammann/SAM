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
