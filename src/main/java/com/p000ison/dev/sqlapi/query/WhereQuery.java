package com.p000ison.dev.sqlapi.query;

import com.p000ison.dev.sqlapi.Column;

/**
 * Represents a WhereQuery
 */
public interface WhereQuery {

    public WhereComparator equals(Column column, Object expected);

    public WhereComparator notEquals(Column column, Object expected);

    public WhereComparator lessThan(Column column, Object expected);

    public WhereComparator greaterThan(Column column, Object expected);
}
