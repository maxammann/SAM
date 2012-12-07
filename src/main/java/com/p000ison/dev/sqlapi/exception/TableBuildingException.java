package com.p000ison.dev.sqlapi.exception;

public class TableBuildingException extends RuntimeException {

    public TableBuildingException()
    {
        super("Failed at building table!");
    }

    public TableBuildingException(String message)
    {
        super(message);
    }

    public TableBuildingException(String message, Object... args)
    {
        super(String.format(message, args));
    }

    public TableBuildingException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public TableBuildingException(Throwable cause)
    {
        super(cause);
    }
}
