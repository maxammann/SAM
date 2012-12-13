package com.p000ison.dev.sqlapi;

import com.p000ison.dev.sqlapi.exception.QueryException;
import com.p000ison.dev.sqlapi.query.SelectQuery;
import com.p000ison.dev.sqlapi.query.WhereQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a DefaultSelectQuery
 */
public class DefaultSelectQuery<T extends TableObject> implements SelectQuery<T> {

    private Class<? extends TableObject> tableClass;
    private boolean descending;
    private DefaultWhereQuery<T> whereQuery;
    private Database database;

    public DefaultSelectQuery(Database database)
    {
        this.database = database;
    }

    @Override
    public SelectQuery<T> from(Class<? extends T> object)
    {
        this.tableClass = object;
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

    protected DefaultWhereQuery getWhereQuery()
    {
        return whereQuery;
    }

    @Override
    public List<T> list()
    {
        List<T> objects = new ArrayList<T>();
        RegisteredTable table = database.getRegisteredTable(tableClass);


        ResultSet result = database.executeDirectQuery(getQuery());

        try {
            while (result.next()) {
                T object = table.createNewInstance();
                objects.add(object);
            }
        } catch (SQLException e) {
            throw new QueryException(e);
        }
        return objects;
    }

    @Override
    public String getQuery()
    {
        StringBuilder query = new StringBuilder("SELECT ");

        RegisteredTable table = database.getRegisteredTable(tableClass);
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
            query.append(" DESC");
        }

        if (getWhereQuery() != null) {
            query.append(" WHERE ");
            List<DefaultWhereComparator> comparators = whereQuery.getComparators();

            if (!comparators.isEmpty()) {
                for (DefaultWhereComparator comparator : comparators) {
                    query.append(comparator.getColumn()).append(comparator.getOperator()).append(comparator.getExpectedValue());

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
}
