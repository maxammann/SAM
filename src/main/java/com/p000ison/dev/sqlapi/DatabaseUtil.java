package com.p000ison.dev.sqlapi;

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
}
