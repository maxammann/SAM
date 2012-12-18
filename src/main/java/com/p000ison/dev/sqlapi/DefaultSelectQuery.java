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

import com.p000ison.dev.sqlapi.exception.QueryException;
import com.p000ison.dev.sqlapi.query.SelectQuery;
import com.p000ison.dev.sqlapi.query.WhereQuery;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a DefaultSelectQuery
 */
public class DefaultSelectQuery<T extends TableObject> implements SelectQuery<T> {

    private RegisteredTable table;
    private boolean descending;
    private DefaultWhereQuery<T> whereQuery;
    private Database database;
    private PreparedStatement preparedStatement;

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

    @Override
    public List<T> list()
    {
        if (preparedStatement == null) {
            prepareQuery();
        }
        List<T> objects = new ArrayList<T>();
        try {
            if (whereQuery != null) {
                List<DefaultWhereComparator<T>> comparators = whereQuery.getComparators();
                for (int i = 0; i < comparators.size(); i++) {
                    preparedStatement.setObject(i + 1, comparators.get(i).getExpectedValue());
                }
            }

            ResultSet result = preparedStatement.executeQuery();
            List<Column> columns = table.getRegisteredColumns();

            while (result.next()) {
                T object = table.createNewInstance();

                for (int i = 0; i < columns.size(); i++) {
                    Column column = columns.get(i);

                    Object obj = null;

                    if (column.isSupported()) {
                        obj = result.getObject(i + 1);
                    } else if (column.isSerializable()) {
                        try {
                            ObjectInputStream inputStream = new ObjectInputStream(result.getBlob(i + 1).getBinaryStream());
                            obj = inputStream.readObject();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }

                    column.setValue(object, obj);
                }

                objects.add(object);
            }
        } catch (SQLException e) {
            throw new QueryException(e);
        }
        return objects;
    }

    public void prepareQuery()
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

        preparedStatement = prepare(query.toString());
    }

    protected PreparedStatement prepare(String query)
    {
        return database.prepare(query);
    }
}
