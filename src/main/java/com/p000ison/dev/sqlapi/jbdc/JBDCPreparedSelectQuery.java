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
 * Last modified: 29.12.12 15:57
 */

package com.p000ison.dev.sqlapi.jbdc;


import com.p000ison.dev.sqlapi.Column;
import com.p000ison.dev.sqlapi.RegisteredTable;
import com.p000ison.dev.sqlapi.TableObject;
import com.p000ison.dev.sqlapi.exception.QueryException;
import com.p000ison.dev.sqlapi.query.PreparedSelectQuery;

import java.io.IOException;
import java.io.InputStream;
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

    @Override
    public <C extends Collection<T>> C getResults(C collection)
    {
        synchronized (getDatabase()) {
            ResultSet result = null;
            try {
                if (getPreparedStatement().isClosed()) {
                    reset();
                }

                result = getPreparedStatement().executeQuery();
                List<Column> columns = table.getRegisteredColumns();

                while (result.next()) {
                    T object = table.createNewInstance();

                    for (int i = 0; i < columns.size(); i++) {
                        Column column = columns.get(i);

                        Object obj = null;


                        if (JBDCDatabase.isSupportedByDatabase(column.getType())) {
                            obj = JBDCDatabase.getDatabaseFromResultSet(i + 1, result, column.getType());
                        } else {
                            ObjectInputStream inputStream = null;
                            try {
                                InputStream selectedInputStream = result.getBinaryStream(i + 1);
                                if (selectedInputStream != null) {
                                    inputStream = new ObjectInputStream(selectedInputStream);
                                    obj = inputStream.readObject();
                                }
                            } catch (IOException e) {
                                throw new QueryException(e);
                            } catch (ClassNotFoundException e) {
                                throw new QueryException(e);
                            } finally {
                                try {
                                    if (inputStream != null) {
                                        inputStream.close();
                                    }
                                } catch (IOException e) {
                                    throw new QueryException(e);
                                }
                            }
                        }

                        if (column.isSaveInputAfterLoading()) {
                            //set this value after returning getResults
                            table.storeColumnValue(column, obj, object);
                        } else {
                            column.setValue(object, obj);
                        }
                    }

                    collection.add(object);
                }
            } catch (SQLException e) {
                if (isAutoReset()) {
                    reset();
                }
                throw new QueryException(e);
            } finally {
                JBDCDatabase.handleClose(null, result);
            }
            return collection;
        }
    }

    @Override
    public List<T> getResults()
    {
        return getResults(new ArrayList<T>());
    }
}
