package com.p000ison.dev.sqlapi.query;

import com.p000ison.dev.sqlapi.Column;
import com.p000ison.dev.sqlapi.TableObject;

/**
 * Represents a WhereQuery
 */
public interface WhereQuery<T extends TableObject> {

    public WhereComparator<T> equals(Column column, Object expected);

    public WhereComparator<T> notEquals(Column column, Object expected);

    public WhereComparator<T> lessThan(Column column, Object expected);

    public WhereComparator<T> greaterThan(Column column, Object expected);
}
