package com.p000ison.dev.sqlapi.jbdc;


import com.p000ison.dev.sqlapi.Column;
import com.p000ison.dev.sqlapi.exception.QueryException;
import com.p000ison.dev.sqlapi.query.PreparedQuery;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Represents a JBDCPreparedQuery
 */
public class JBDCPreparedQuery implements PreparedQuery {
    private final PreparedStatement preparedStatement;
    private final JBDCDatabase database;
    ;

    protected JBDCPreparedQuery(JBDCDatabase database, String query)
    {
        this.database = database;
        preparedStatement = database.prepare(query);
    }

    @Override
    public void set(int index, Object value)
    {
        if (index < 0) {
            throw new IllegalArgumentException("The index must be more or equal 0!");
        }

        try {
            preparedStatement.setObject(index + 1, value);
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }

    @Override
    public void set(int index, Object value, int databaseType)
    {
        if (index < 0) {
            throw new IllegalArgumentException("The index must be more or equal 0!");
        }

        try {
            preparedStatement.setObject(index + 1, value, databaseType);
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }

    @Override
    public void set(Column column, int index, Object value)
    {
        if (index < 0) {
            throw new IllegalArgumentException("The index must be more or equal 0!");
        }

        index++;

        try {
            if (column.isSupported()) {
                if (value == null) {
                    preparedStatement.setNull(index, column.getDatabaseDataType());
                } else {
                    preparedStatement.setObject(index, value, column.getDatabaseDataType());
                }
            } else if (column.isSerializable()) {
                if (value == null) {
                    preparedStatement.setNull(index, Types.BLOB);
                } else {
                    Blob blob = database.createBlob();
                    ObjectOutputStream stream = new ObjectOutputStream(blob.setBinaryStream(1));
                    stream.writeObject(value);

                    preparedStatement.setBlob(index, blob);
                }
            }
        } catch (SQLException e) {
            throw new QueryException(e);
        } catch (IOException e) {
            throw new QueryException(e);
        }
    }

    @Override
    public void clearParameters()
    {
        try {
            preparedStatement.clearParameters();
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }

    @Override
    public boolean update()
    {
        try {
            return preparedStatement.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }

    protected PreparedStatement getPreparedStatement()
    {
        return preparedStatement;
    }
}
