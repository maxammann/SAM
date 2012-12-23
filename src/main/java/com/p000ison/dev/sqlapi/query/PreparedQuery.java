package com.p000ison.dev.sqlapi.query;

import com.p000ison.dev.sqlapi.Column;

/**
 * Represents a PreparedQuery
 */
public interface PreparedQuery {

    void set(int index, Object value);

    void set(int index, Object value, int databaseType);

    void set(Column column, int index, Object value);

    /**
     * Clears the stored values
     */
    void clearParameters();

    /**
     * Updates the database and performs the query. This method should be synchronized with the {@link com.p000ison.dev.sqlapi.Database}.
     *
     * @return Whether the update was successfully
     */
    boolean update();

    void close();
}
