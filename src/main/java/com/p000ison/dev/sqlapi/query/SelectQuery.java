package com.p000ison.dev.sqlapi.query;

import com.p000ison.dev.sqlapi.Column;
import com.p000ison.dev.sqlapi.TableObject;

/**
 *
 */
public interface SelectQuery {

    SelectQuery from(Class<? extends TableObject> object);

    WhereQuery where();

    SelectQuery descending();

    SelectQuery orderBy(Column order);

    SelectQuery orderBy(String order);

    SelectQuery groupBy(Column group);

    SelectQuery groupBy(String group);
}
