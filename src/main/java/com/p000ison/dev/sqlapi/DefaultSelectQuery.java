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
 * Last modified: 26.12.12 19:50
 */

package com.p000ison.dev.sqlapi;

import com.p000ison.dev.sqlapi.exception.QueryException;
import com.p000ison.dev.sqlapi.query.PreparedSelectQuery;
import com.p000ison.dev.sqlapi.query.SelectQuery;
import com.p000ison.dev.sqlapi.query.WhereQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a DefaultSelectQuery
 */
public class DefaultSelectQuery<T extends TableObject> implements SelectQuery<T> {

    private RegisteredTable table;
    private DefaultWhereQuery<T> whereQuery;
    private Database database;
    private List<DefaultOrderEntry> orderBy = new ArrayList<DefaultOrderEntry>();
    private int[] limits;

    public DefaultSelectQuery(Database database)
    {
        this.database = database;
    }

    @Override
    public SelectQuery<T> from(Class<T> object)
    {
        this.table = database.getRegisteredTable(object);
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
    public SelectQuery<T> orderBy(Column order)
    {
        return orderBy(order.getName());
    }

    @Override
    public SelectQuery<T> orderByDescending(Column order)
    {
        return orderByDescending(order.getName());
    }

    @Override
    public SelectQuery<T> orderByDescending(String order)
    {
        orderBy.add(new DefaultOrderEntry(order, true));
        return this;
    }

    @Override
    public SelectQuery<T> orderBy(String order)
    {
        orderBy.add(new DefaultOrderEntry(order, false));
        return this;
    }

    protected DefaultWhereQuery<T> getWhereQuery()
    {
        return whereQuery;
    }

    @Override
    public final synchronized PreparedSelectQuery<T> prepare()
    {
        String query = getQuery();
        if (query == null) {
            throw new QueryException("The query is not prepared!");
        }
        PreparedSelectQuery<T> preparedQuery = database.createPreparedSelectQuery(getQuery(), table);
        if (whereQuery != null) {
            List<DefaultWhereComparator<T>> comparators = whereQuery.getComparators();
            for (int i = 0; i < comparators.size(); i++) {
                DefaultWhereComparator<T> comparator = comparators.get(i);
                if (!comparator.isPrepared()) {
                    preparedQuery.set(i, comparator.getExpectedValue());
                }
            }
        }

        return preparedQuery;
    }

    protected synchronized String getQuery()
    {
        if (table == null) {
            return null;
        }

        StringBuilder query = new StringBuilder("SELECT ");
        List<Column> columns = table.getRegisteredColumns();

        int end = columns.size() - 1;
        for (int i = 0; i < columns.size(); i++) {
            Column column = columns.get(i);
            query.append(column.getName());
            if (i != end) {
                query.append(',');
            }
        }

        query.append(" FROM ").append(table.getName());

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

        if (!orderBy.isEmpty()) {
            query.append(" ORDER BY ");
            for (DefaultOrderEntry entry : orderBy) {
                if (entry.getOrder() != null) {
                    query.append(entry.getOrder());
                    if (!entry.isDescending()) {
                        query.append(',');
                    }
                }
                if (entry.isDescending()) {
                    query.append(" DESC,");
                }
            }

            query.deleteCharAt(query.length() - 1);
        }

        if (limits != null) {
            query.append(" LIMIT ");
            if (limits.length == 1) {
                query.append(limits[0]);
            } else {
                query.append(limits[0]).append(',').append(limits[1]);
            }
        }

        query.append(';');

        return query.toString();
    }

    @Override
    public SelectQuery<T> limit(int max)
    {
        if (max < 1) {
            throw new IllegalArgumentException("The limit must be greater than 0!");
        }
        limits = new int[]{max};
        return this;
    }

    @Override
    public SelectQuery<T> limit(int from, int to)
    {
        if (from > 0 || to > 0) {
            throw new IllegalArgumentException("The limit must be greater than 0!");
        } else if (from > to) {
            throw new IllegalArgumentException("The from limit must be less than the to limit!");
        }

        limits = new int[]{from, to};
        return this;
    }

    @Override
    public SelectQuery<T> reset()
    {
        this.table = null;
        this.whereQuery = null;
        this.orderBy = new ArrayList<DefaultOrderEntry>();
        this.limits = null;
        return this;
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
