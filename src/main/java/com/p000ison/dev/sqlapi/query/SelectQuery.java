package com.p000ison.dev.sqlapi.query;

import com.p000ison.dev.sqlapi.Column;
import com.p000ison.dev.sqlapi.TableObject;

import java.util.List;

/**
 *
 */
public interface SelectQuery<T extends TableObject> {

    SelectQuery<T> from(Class<T> object);

    WhereQuery<T> where();

    SelectQuery<T> descending();

    SelectQuery<T> orderBy(Column order);

    SelectQuery<T> orderBy(String order);

    SelectQuery<T> groupBy(Column group);

    SelectQuery<T> groupBy(String group);

    String getQuery();

    List<T> list();
}
