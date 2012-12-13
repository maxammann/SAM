package com.p000ison.dev.sqlapi.query;

import com.p000ison.dev.sqlapi.TableObject;

/**
 *
 */
public interface WhereComparator<T extends TableObject> {

    WhereQuery<T> or();

    WhereQuery<T> and();

    SelectQuery<T> select();
}
