package com.p000ison.dev.sqlapi.exception;

import java.sql.SQLException;

/**
 * Represents a QueryException
 */
public class QueryException extends RuntimeException {


    public QueryException(String message, Object... args)
    {
        super(args.length == 0 ? message : String.format(message, args));
    }

    public QueryException(SQLException cause)
    {
        super(cause);
    }
}
