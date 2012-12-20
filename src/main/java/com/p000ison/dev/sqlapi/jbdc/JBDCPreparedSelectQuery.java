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
 * Last modified: 20.12.12 20:02
 */

package com.p000ison.dev.sqlapi.jbdc;


import com.p000ison.dev.sqlapi.Column;
import com.p000ison.dev.sqlapi.RegisteredTable;
import com.p000ison.dev.sqlapi.TableObject;
import com.p000ison.dev.sqlapi.exception.QueryException;
import com.p000ison.dev.sqlapi.query.PreparedSelectQuery;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a JBDCPreparedQuery
 */
public class JBDCPreparedSelectQuery<T extends TableObject> extends JBDCPreparedQuery implements PreparedSelectQuery<T> {
    private final RegisteredTable table;

    protected JBDCPreparedSelectQuery(JBDCDatabase database, String query, RegisteredTable table)
    {
        super(database, query);
        this.table = table;
    }

    protected JBDCPreparedSelectQuery(JBDCDatabase database, String query, Class<? extends TableObject> table)
    {
        super(database, query);
        this.table = database.getRegisteredTable(table);
    }

    @Override
    public <C extends Collection<T>> C getResults(C collection)
    {
        try {
            ResultSet result = getPreparedStatement().executeQuery();
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
