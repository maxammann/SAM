package com.p000ison.dev.sqlapi;

import com.p000ison.dev.sqlapi.query.CompareOperator;
import com.p000ison.dev.sqlapi.query.SelectQuery;
import com.p000ison.dev.sqlapi.query.WhereComparator;
import com.p000ison.dev.sqlapi.query.WhereQuery;

/**
 * Represents a DefaultWhereComparator
 */
class DefaultWhereComparator<T extends TableObject> implements WhereComparator<T> {

    private DefaultSelectQuery<T> query;
    private boolean and, or;
    private String column;
    private Object expectedValue;
    private CompareOperator operator;

    DefaultWhereComparator(DefaultSelectQuery<T> query, CompareOperator operator, String column, Object expectedValue)
    {
        this.query = query;
        this.column = column;
        this.operator = operator;
        this.expectedValue = expectedValue;
    }

    @Override
    public WhereQuery or()
    {
        or = true;
        return query.getWhereQuery();
    }

    @Override
    public WhereQuery and()
    {
        and = true;
        return query.getWhereQuery();
    }

    @Override
    public SelectQuery<T> select()
    {
        return query;
    }

    protected boolean isOr()
    {
        return or;
    }

    protected boolean isAnd()
    {
        return and;
    }
    protected boolean isFinished() {
        return !or && !and;
    }

    protected String getColumn()
    {
        return column;
    }

    protected Object getExpectedValue()
    {
        return expectedValue;
    }

    protected CompareOperator getOperator()
    {
        return operator;
    }
}
