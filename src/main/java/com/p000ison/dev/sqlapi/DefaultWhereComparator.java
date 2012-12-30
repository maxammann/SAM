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
 * Last modified: 18.12.12 18:30
 */

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
    private boolean prepared = false;

    DefaultWhereComparator(DefaultSelectQuery<T> query, CompareOperator operator, String column, Object expectedValue)
    {
        this.query = query;
        this.column = column;
        this.operator = operator;
        this.expectedValue = expectedValue;
    }

    DefaultWhereComparator(DefaultSelectQuery<T> query, CompareOperator operator, String column, boolean prepared)
    {
        this.query = query;
        this.column = column;
        this.operator = operator;
        this.prepared = prepared;
    }

    @Override
    public WhereQuery<T> or()
    {
        or = true;
        return query.getWhereQuery();
    }

    @Override
    public WhereQuery<T> and()
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

    public boolean isPrepared()
    {
        return prepared;
    }
}
