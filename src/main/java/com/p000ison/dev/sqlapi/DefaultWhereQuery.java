/*
 * This file is part of SQLDatabaseAPI (2012).
 *
 * SQLDatabaseAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SQLDatabaseAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SQLDatabaseAPI.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Last modified: 26.12.12 22:16
 */

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
        return equals(column.getName(), expected);
    }

    @Override
    public WhereComparator<T> notEquals(Column column, Object expected)
    {
        return notEquals(column.getName(), expected);
    }

    @Override
    public WhereComparator<T> lessThan(Column column, Object expected)
    {
        return lessThan(column.getName(), expected);
    }

    @Override
    public WhereComparator<T> greaterThan(Column column, Object expected)
    {
        return greaterThan(column.getName(), expected);
    }

    @Override
    public WhereComparator<T> equals(String column, Object expected)
    {
        return addComparator(column, CompareOperator.EQUALS, expected);
    }

    @Override
    public WhereComparator<T> notEquals(String column, Object expected)
    {
        return addComparator(column, CompareOperator.NOT_EQUAL, expected);
    }

    @Override
    public WhereComparator<T> lessThan(String column, Object expected)
    {
        return addComparator(column, CompareOperator.LESS_THAN, expected);
    }

    @Override
    public WhereComparator<T> greaterThan(String column, Object expected)
    {
        return addComparator(column, CompareOperator.GREATER_THAN, expected);
    }

    private WhereComparator<T> addComparator(String column, CompareOperator compareOperator, Object expected)
    {
        DefaultWhereComparator<T> comparator = new DefaultWhereComparator<T>(query, compareOperator, column, expected);
        comparators.add(comparator);
        return comparator;
    }

    protected List<DefaultWhereComparator<T>> getComparators()
    {
        return comparators;
    }
}
