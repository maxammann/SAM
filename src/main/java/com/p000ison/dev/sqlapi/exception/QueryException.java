package com.p000ison.dev.sqlapi.exception;

import java.sql.SQLException;

/**
 * Represents a QueryException
 */
public class QueryException extends RuntimeException {

    public QueryException(String message)
    {
        super(message);
    }

    public QueryException(String message, SQLException cause)
    {
        super(message, cause);
    }

    public QueryException(SQLException cause)
    {
        super(cause);
    }
}
