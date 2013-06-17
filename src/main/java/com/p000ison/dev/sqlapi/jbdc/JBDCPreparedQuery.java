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
 * Last modified: 29.12.12 15:06
 */

package com.p000ison.dev.sqlapi.jbdc;


import com.p000ison.dev.sqlapi.DatabaseColumn;
import com.p000ison.dev.sqlapi.Database;
import com.p000ison.dev.sqlapi.exception.QueryException;
import com.p000ison.dev.sqlapi.query.PreparedQuery;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Represents a JBDCPreparedQuery
 */
public class JBDCPreparedQuery implements PreparedQuery {
    private PreparedStatement preparedStatement;
    private final JBDCDatabase database;
    private final String query;
    private boolean autoReset;

    protected JBDCPreparedQuery(JBDCDatabase database, String query) {
        this.query = query;
        this.preparedStatement = database.prepare(query);
        this.autoReset = database.isAutoReset();
        this.database = database;
    }

    @Override
    public void set(int index, Object value) {
        if (index < 0) {
            throw new IllegalArgumentException("The index must be more or equal 0!");
        }

        try {
            try {
                if (preparedStatement.isClosed()) {
                    reset();
                }
            } catch (AbstractMethodError ignored) {
            }
            preparedStatement.setObject(index + 1, value);
        } catch (SQLException e) {
            if (autoReset) {
                reset();
            }
            throw new QueryException(e);
        }
    }

    @Override
    public void set(int index, Object value, int databaseType) {
        if (index < 0) {
            throw new IllegalArgumentException("The index must be more or equal 0!");
        }

        try {
            try {
                if (preparedStatement.isClosed()) {
                    reset();
                }
            } catch (AbstractMethodError ignored) {
            }
            preparedStatement.setObject(index + 1, value, databaseType);
        } catch (SQLException e) {
            if (autoReset) {
                reset();
            }
            throw new QueryException(e);
        }
    }

    @Override
    public void set(DatabaseColumn column, int index, Object value) {
        if (index < 0) {
            throw new IllegalArgumentException("The index must be more or equal 0!");
        }

        index++;

        try {
            try {
                if (preparedStatement.isClosed()) {
                    reset();
                }
            } catch (AbstractMethodError ignored) {
            }
            int type = JBDCDatabase.getDatabaseDataType(column.getType());
            if (type != Database.UNSUPPORTED_TYPE) {
                if (value == null) {
                    preparedStatement.setNull(index, type);
                } else {
                    if (value instanceof AtomicBoolean) {
                        value = ((AtomicBoolean) value).get();
                    }
                    if (value instanceof AtomicInteger) {
                        value = ((AtomicInteger) value).get();
                    }
                    if (value instanceof AtomicLong) {
                        value = ((AtomicLong) value).get();
                    }
                    preparedStatement.setObject(index, value, type);
                }
            } else if (column.isSerializable()) {
                if (value == null) {
                    preparedStatement.setNull(index, Types.BLOB);
                } else {
                    ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
                    ObjectOutput outputStream = new ObjectOutputStream(byteBuffer);
                    outputStream.writeObject(value);
                    byte[] bytes = byteBuffer.toByteArray();

                    preparedStatement.setBytes(index, bytes);
                }
            }

        } catch (SQLException e) {
            if (autoReset) {
                reset();
            }
            throw new QueryException(e);
        } catch (IOException e) {
            throw new QueryException(e);
        }
    }

    @Override
    public void clearParameters() {
        try {
            if (preparedStatement.isClosed()) {
                reset();
            } else {
                preparedStatement.clearParameters();
            }
        } catch (AbstractMethodError ignored) {
        } catch (SQLException e) {
            if (autoReset) {
                reset();
            }
            throw new QueryException(e);
        }
    }

    @Override
    public boolean update() {
        synchronized (database) {
            try {
                try {
                    if (preparedStatement.isClosed()) {
                        reset();
                    }
                } catch (AbstractMethodError ignored) {
                }
                return preparedStatement.executeUpdate() != 0;
            } catch (SQLException e) {
                if (autoReset) {
                    reset();
                }
                throw new QueryException(e);
            }
        }
    }

    public ResultSet query() {
        synchronized (database) {
            try {
                try {
                    if (preparedStatement.isClosed()) {
                        reset();
                    }
                } catch (AbstractMethodError ignored) {
                }
                return preparedStatement.executeQuery();
            } catch (SQLException e) {
                if (autoReset) {
                    reset();
                }
                throw new QueryException(e);
            }
        }
    }

    @Override
    public void close() {
        try {
            try {
                if (preparedStatement.isClosed()) {
                    return;
                }
            } catch (AbstractMethodError ignored) {
            }
            getPreparedStatement().close();
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }


    @Override
    public void reset() {
        preparedStatement = database.prepare(query);
    }

    @Override
    public void setAutoReset(boolean reset) {
        autoReset = reset;
    }

    @Override
    public void addBatch() {
        synchronized (database) {
            try {
                try {
                    if (preparedStatement.isClosed()) {
                        reset();
                    }
                } catch (AbstractMethodError ignored) {
                }
                preparedStatement.addBatch();
            } catch (SQLException e) {
                if (autoReset) {
                    reset();
                }
                throw new QueryException(e);
            }
        }
    }

    @Override
    public void clearBatch() {
        synchronized (database) {
            try {
                try {
                    if (preparedStatement.isClosed()) {
                        reset();
                    }
                } catch (AbstractMethodError ignored) {
                }
                preparedStatement.clearBatch();
            } catch (SQLException e) {
                if (autoReset) {
                    reset();
                }
                throw new QueryException(e);
            }
        }
    }

    @Override
    public void executeBatches() {
        synchronized (database) {
            try {
                try {
                    if (preparedStatement.isClosed()) {
                        reset();
                    }
                } catch (AbstractMethodError ignored) {
                }
                preparedStatement.executeBatch();
            } catch (SQLException e) {
                if (autoReset) {
                    reset();
                }
                throw new QueryException(e);
            }
        }
    }

    @Override
    public boolean isAutoReset() {
        return autoReset;
    }

    protected PreparedStatement getPreparedStatement() {
        return preparedStatement;
    }

    protected JBDCDatabase getDatabase() {
        return database;
    }
}
