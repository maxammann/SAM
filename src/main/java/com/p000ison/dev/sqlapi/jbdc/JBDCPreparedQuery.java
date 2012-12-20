package com.p000ison.dev.sqlapi.jbdc;


import com.p000ison.dev.sqlapi.Column;
import com.p000ison.dev.sqlapi.PreparedQuery;
import com.p000ison.dev.sqlapi.RegisteredTable;
import com.p000ison.dev.sqlapi.TableObject;
import com.p000ison.dev.sqlapi.exception.QueryException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a JBDCPreparedQuery
 */
public class JBDCPreparedQuery<T extends TableObject> implements PreparedQuery<T> {
    private final RegisteredTable table;
    private final PreparedStatement preparedStatement;

    protected JBDCPreparedQuery(JBDCDatabase database, String query, RegisteredTable table)
    {
        this.table = table;
        preparedStatement = database.prepare(query);
    }

    protected JBDCPreparedQuery(JBDCDatabase database, String query, Class<? extends TableObject> table)
    {
        this.table = database.getRegisteredTable(table);
        preparedStatement = database.prepare(query);
    }

    @Override
    public void set(int index, Object value)
    {
        if (index < 0) {
            throw new IllegalArgumentException("The index must be more or equal 0!");
        }

        try {
            preparedStatement.setObject(index + 1, value);
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }

    @Override
    public <C extends Collection<T>> C getResults(C collection)
    {
        try {
            ResultSet result = preparedStatement.executeQuery();
            List<Column> columns = table.getRegisteredColumns();

            while (result.next()) {
                T object = table.createNewInstance();

                for (int i = 0; i < columns.size(); i++) {
                    Column column = columns.get(i);

                    Object obj;

                    if (column.isSupported()) {
                        obj = result.getObject(i + 1);
                    } else if (column.isSerializable()) {
                        try {
                            ObjectInputStream inputStream = new ObjectInputStream(result.getBlob(i + 1).getBinaryStream());
                            obj = inputStream.readObject();
                        } catch (IOException e) {
                            e.printStackTrace();
                            return collection;
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                            return collection;
                        }
                    } else {
                        throw new QueryException("The type %s is not supported!", column.getType().getName());
                    }

                    column.setValue(object, obj);
                }

                collection.add(object);
            }
        } catch (SQLException e) {
            throw new QueryException(e);
        }
        return collection;
    }

    @Override
    public List<T> getResults()
    {
        return getResults(new ArrayList<T>());
    }
}
