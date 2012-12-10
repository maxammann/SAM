package com.p000ison.dev.sqlapi;

import com.p000ison.dev.sqlapi.query.CompareOperator;
import com.p000ison.dev.sqlapi.query.WhereComparator;
import com.p000ison.dev.sqlapi.query.WhereQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a DefaultWhereQuery
 */
public class DefaultWhereQuery implements WhereQuery {

    private List<DefaultWhereComparator> comparators;
    private DefaultSelectQuery query;

    public DefaultWhereQuery(DefaultSelectQuery query)
    {
        this.query = query;
        this.comparators = new ArrayList<DefaultWhereComparator>();
    }

    @Override
    public WhereComparator equals(Column column, Object expected)
    {
        DefaultWhereComparator comparator = new DefaultWhereComparator(query, CompareOperator.EQUALS, column.getColumnName(), expected);
        comparators.add(comparator);
        return comparator;
    }

    @Override
    public WhereComparator notEquals(Column column, Object expected)
    {
        return null;
    }

    @Override
    public WhereComparator lessThan(Column column, Object expected)
    {
        return null;
    }

    @Override
    public WhereComparator greaterThan(Column column, Object expected)
    {
        return null;
    }
}
