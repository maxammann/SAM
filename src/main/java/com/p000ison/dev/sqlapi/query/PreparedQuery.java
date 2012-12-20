package com.p000ison.dev.sqlapi.query;

import com.p000ison.dev.sqlapi.Column;

/**
 * Represents a PreparedQuery
 */
public interface PreparedQuery {

    void set(int index, Object value);

    void set(int index, Object value, int databaseType);

    void set(Column column, int index, Object value);

    void clearParameters();

    boolean update();
}
