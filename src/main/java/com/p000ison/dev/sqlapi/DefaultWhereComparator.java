package com.p000ison.dev.sqlapi;

import com.p000ison.dev.sqlapi.query.CompareOperator;
import com.p000ison.dev.sqlapi.query.SelectQuery;
import com.p000ison.dev.sqlapi.query.WhereComparator;
import com.p000ison.dev.sqlapi.query.WhereQuery;

/**
 * Represents a DefaultWhereComparator
 */
public class DefaultWhereComparator implements WhereComparator {

    private DefaultSelectQuery query;
    private boolean and;
    private String column;
    private Object expectedValue;
    private CompareOperator operator;

    public DefaultWhereComparator(DefaultSelectQuery query, CompareOperator operator, String column, Object expectedValue)
    {
        this.query = query;
        this.column = column;
        this.operator = operator;
        this.expectedValue = expectedValue;
    }

    @Override
    public WhereQuery or()
    {
        and = false;
        return query.getWhereQuery();
    }

    @Override
    public WhereQuery and()
    {
        and = true;
        return query.getWhereQuery();
    }

    @Override
    public SelectQuery select()
    {
        return query;
    }

    boolean isOr()
    {
        return !and;
    }

    boolean isAnd()
    {
        return and;
    }
}
