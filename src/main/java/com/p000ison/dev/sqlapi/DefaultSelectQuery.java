package com.p000ison.dev.sqlapi;

import com.p000ison.dev.sqlapi.query.SelectQuery;
import com.p000ison.dev.sqlapi.query.WhereQuery;

/**
 * Represents a DefaultSelectQuery
 */
public class DefaultSelectQuery implements SelectQuery {

    private Class<? extends TableObject> tableClass;
    private boolean descending;
    private DefaultWhereQuery whereQuery;

    @Override
    public SelectQuery from(Class<? extends TableObject> object)
    {
        this.tableClass = object;
        return this;
    }

    @Override
    public WhereQuery where()
    {
        return whereQuery = new DefaultWhereQuery(this);
    }

    @Override
    public SelectQuery descending()
    {
        this.descending = true;
        return this;
    }

    @Override
    public SelectQuery orderBy(Column order)
    {
        return this;
    }

    @Override
    public SelectQuery orderBy(String order)
    {
        return this;
    }

    @Override
    public SelectQuery groupBy(Column group)
    {
        return this;
    }

    @Override
    public SelectQuery groupBy(String group)
    {
        return this;
    }

    DefaultWhereQuery getWhereQuery() {
        return whereQuery;
    }
}
