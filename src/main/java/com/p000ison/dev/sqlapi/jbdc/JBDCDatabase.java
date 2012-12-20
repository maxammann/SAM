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
 * Last modified: 19.12.12 18:40
 */

package com.p000ison.dev.sqlapi.jbdc;

import com.p000ison.dev.sqlapi.*;
import com.p000ison.dev.sqlapi.exception.DatabaseConnectionException;
import com.p000ison.dev.sqlapi.exception.QueryException;
import com.p000ison.dev.sqlapi.query.PreparedQuery;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a JBDCDatabase
 */
public abstract class JBDCDatabase extends Database {
    /**
     * The connection to the database
     */
    private Connection connection;

    public JBDCDatabase(DatabaseConfiguration configuration) throws DatabaseConnectionException
    {
        super(configuration);

        long start = System.currentTimeMillis();
        connection = connect(configuration);
        long finish = System.currentTimeMillis();
        System.out.printf("Check connection took %s!\n", finish - start);
    }

    protected abstract Connection connect(DatabaseConfiguration configuration) throws DatabaseConnectionException;

    @Override
    public void close() throws QueryException
    {
        try {
            getConnection().close();
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }

    @Override
    public List<String> getDatabaseColumns(String table)
    {
        List<String> columns = new ArrayList<String>();

        try {
            ResultSet columnResult = getMetadata().getColumns(null, null, table, null);


            while (columnResult.next()) {
                columns.add(columnResult.getString("COLUMN_NAME"));
            }
        } catch (SQLException e) {
            throw new QueryException(e);
        }

        return columns;
    }

    private DatabaseMetaData getMetadata()
    {
        try {
            return getConnection().getMetaData();
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }

    @Override
    public final Set<String> getDatabaseTables()
    {
        Set<String> columns = new HashSet<String>();

        ResultSet columnResult;
        try {
            columnResult = this.getMetadata().getTables(null, null, null, null);

            while (columnResult.next()) {
                columns.add(columnResult.getString("TABLE_NAME"));
            }

        } catch (SQLException e) {
            throw new QueryException(e);
        }

        return columns;
    }


    protected final Connection getConnection()
    {
        return connection;
    }

    @Override
    protected boolean executeDirectUpdate(String query)
    {
        if (query == null) {
            return false;
        }
        try {
            return getConnection().createStatement().executeUpdate(query) != 0;
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }

    @Override
    public boolean isConnected()
    {
        try {
            return getConnection() != null && !getConnection().isClosed();
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }

    PreparedStatement prepare(String query)
    {
        try {
            return getConnection().prepareStatement(query);
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }

    Blob createBlob()
    {
        try {
            return getConnection().createBlob();
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }

    @Override
    protected PreparedQuery createPreparedStatement(String query)
    {
        return new JBDCPreparedQuery(this, query);
    }

    @Override
    protected boolean existsEntry(RegisteredTable table, TableObject object)
    {
        Column column = table.getIDColumn();

        try {
            PreparedStatement check = getConnection().prepareStatement(String.format("SELECT %s FROM %s WHERE %s=%s;", column.getColumnName(), table.getName(), column.getColumnName(), column.getValue(object)));

//            check.setString(1, column.getColumnName());
//            check.setString(2, column.getColumnName());
//            check.setObject(2, column.getValue(object), column.getDatabaseDataType());
            ResultSet result = check.executeQuery();
            return result.next();
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }

    @Override
    protected boolean existsEntry(TableObject object)
    {
        return this.existsEntry(getRegisteredTable(object.getClass()), object);
    }

    @Override
    protected int getLastEntryId(RegisteredTable table)
    {
        Column idColumn = table.getIDColumn();
        try {
            PreparedStatement check = getConnection().prepareStatement(String.format("SELECT %s FROM %s ORDER BY %s DESC LIMIT 1;", idColumn.getColumnName(), table.getName(), idColumn.getColumnName()));
            ResultSet set = check.executeQuery();
            if (!set.next()) {
                return 1;
            }
            return set.getInt(idColumn.getColumnName());
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }
}