package com.p000ison.dev.sqlapi.exception;

import java.sql.SQLException;

/**
 * Represents a DatabaseConnectionException
 */
public class DatabaseConnectionException extends Exception {

    public DatabaseConnectionException(SQLException cause)
    {
        super(cause);
    }
}
