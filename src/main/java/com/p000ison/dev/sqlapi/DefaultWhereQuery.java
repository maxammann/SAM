package com.p000ison.dev.sqlapi;

import com.p000ison.dev.sqlapi.query.CompareOperator;
import com.p000ison.dev.sqlapi.query.WhereComparator;
import com.p000ison.dev.sqlapi.query.WhereQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a DefaultWhereQuery
 */
class DefaultWhereQuery<T extends TableObject> implements WhereQuery<T> {

    private List<DefaultWhereComparator<T>> comparators;
    private DefaultSelectQuery<T> query;

    DefaultWhereQuery(DefaultSelectQuery<T> query)
    {
        this.query = query;
        this.comparators = new ArrayList<DefaultWhereComparator<T>>();
    }

    @Override
    public WhereComparator<T> equals(Column column, Object expected)
    {
        DefaultWhereComparator<T> comparator = new DefaultWhereComparator<T>(query, CompareOperator.EQUALS, column.getColumnName(), expected);
        comparators.add(comparator);
        return comparator;
    }

    @Override
    public WhereComparator<T> notEquals(Column column, Object expected)
    {
        return null;
    }

    @Override
    public WhereComparator<T> lessThan(Column column, Object expected)
    {
        return null;
    }

    @Override
    public WhereComparator<T> greaterThan(Column column, Object expected)
    {
        return null;
    }

    protected List<DefaultWhereComparator<T>> getComparators()
    {
        return comparators;
    }
}
