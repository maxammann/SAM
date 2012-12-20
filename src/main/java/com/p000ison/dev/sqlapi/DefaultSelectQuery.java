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
 * Last modified: 18.12.12 17:28
 */

package com.p000ison.dev.sqlapi;

import com.p000ison.dev.sqlapi.query.SelectQuery;
import com.p000ison.dev.sqlapi.query.WhereQuery;

import java.util.List;

/**
 * Represents a DefaultSelectQuery
 */
public abstract class DefaultSelectQuery<T extends TableObject> implements SelectQuery<T> {

    private RegisteredTable table;
    private boolean descending;
    private DefaultWhereQuery<T> whereQuery;
    private Database database;

    public DefaultSelectQuery(Database database)
    {
        this.database = database;
    }

    @Override
    public SelectQuery<T> from(Class<T> object)
    {
        table = database.getRegisteredTable(object);
        return this;
    }

    @Override
    public SelectQuery<T> from(RegisteredTable table)
    {
        this.table = table;
        return this;
    }

    @Override
    public WhereQuery<T> where()
    {
        return whereQuery = new DefaultWhereQuery<T>(this);
    }

    @Override
    public SelectQuery<T> descending()
    {
        this.descending = true;
        return this;
    }

    @Override
    public SelectQuery<T> orderBy(Column order)
    {
        return this;
    }

    @Override
    public SelectQuery<T> orderBy(String order)
    {
        return this;
    }

    @Override
    public SelectQuery<T> groupBy(Column group)
    {
        return this;
    }

    @Override
    public SelectQuery<T> groupBy(String group)
    {
        return this;
    }

    protected DefaultWhereQuery<T> getWhereQuery()
    {
        return whereQuery;
    }

    protected abstract PreparedQuery<T> getPreparedQuery();

    @Override
    public final PreparedQuery<T> prepare()
    {
        PreparedQuery<T> preparedQuery = getPreparedQuery();
        if (whereQuery != null) {
            List<DefaultWhereComparator<T>> comparators = whereQuery.getComparators();
            for (int i = 0; i < comparators.size(); i++) {
                preparedQuery.set(i, comparators.get(i).getExpectedValue());
            }
        }

        return preparedQuery;
    }

    protected String getQuery()
    {
        StringBuilder query = new StringBuilder("SELECT ");
        List<Column> columns = table.getRegisteredColumns();

        int end = columns.size() - 1;
        for (int i = 0; i < columns.size(); i++) {
            Column column = columns.get(i);
            query.append(column.getColumnName());
            if (i != end) {
                query.append(',');
            }
        }

        query.append(" FROM ").append(table.getName());

        if (descending) {
            query.append(" ORDER BY DESC");
        }

        if (getWhereQuery() != null) {
            query.append(" WHERE ");
            List<DefaultWhereComparator<T>> comparators = whereQuery.getComparators();

            if (!comparators.isEmpty()) {
                for (DefaultWhereComparator comparator : comparators) {
                    query.append(comparator.getColumn()).append(comparator.getOperator()).append('?');

                    if (comparator.isAnd()) {
                        query.append(" AND ");
                    } else if (comparator.isOr()) {
                        query.append(" OR ");
                    } else {
                        break;
                    }

                }
            }
        }

        query.append(';');

        return query.toString();
    }

    protected Database getDatabase()
    {
        return database;
    }

    protected RegisteredTable getTable()
    {
        return table;
    }
}
