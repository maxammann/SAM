/*
 * This file is part of SAM (2012).
 *
 * SAM is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SAM is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SAM.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Last modified: 05.07.13 12:21
 */

package org.p000ison.dev.sam.jbdc;

import org.p000ison.dev.sam.*;
import org.p000ison.dev.sam.exception.QueryException;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Represents a JBDCDatabase
 */
public abstract class JBDCDatabase<C extends DatabaseConfiguration> extends Database<C> {
	/**
	 * The connection to the database
	 */
	private Connection connection;

	public JBDCDatabase(C configuration, QueryFactory factory) throws DatabaseConnectionException {
		super(configuration, factory);

		connection = connect(configuration);
		if (!testConnection()) {
			throw new DatabaseConnectionException(configuration, "Failed to connect to the database! Test failed!");
		}
	}

	protected abstract Connection connect(DatabaseConfiguration configuration) throws DatabaseConnectionException;

	@Override
	public void closeDatabaseConnection() throws QueryException {
		try {
			if (connection != null && !connection.isClosed()) {
				getConnection().close();
			}
		} catch (SQLException e) {
			throw new QueryException(e);
		}
	}

	@Override
	public List<String> getDatabaseColumns(String table) {
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

	private DatabaseMetaData getMetadata() {
		try {
			return getConnection().getMetaData();
		} catch (SQLException e) {
			throw new QueryException(e);
		}
	}

	@Override
	public boolean existsDatabaseTable(String table) {
		ResultSet columnResult;
		try {
			columnResult = this.getMetadata().getTables(null, null, null, null);

			while (columnResult.next()) {
				if (table.equals(columnResult.getString("TABLE_NAME"))) {
					return true;
				}
			}

		} catch (SQLException e) {
			throw new QueryException(e);
		}

		return false;
	}

	protected final Connection getConnection() {
		return connection;
	}

	@Override
	public boolean executeDirectUpdate(String query) {
		if (query == null) {
			return false;
		}
		Statement statement = null;
		try {
			statement = getConnection().createStatement();
			return statement.executeUpdate(query) != 0;
		} catch (SQLException e) {
			throw new QueryException(e);
		} finally {
			handleClose(statement, null);
		}
	}

	@Override
	public boolean isConnected() {
		try {
			return getConnection() != null && !getConnection().isClosed();
		} catch (SQLException e) {
			throw new QueryException(e);
		}
	}

	public PreparedStatement prepare(String query) {
		try {
			return getConnection().prepareStatement(query);
		} catch (SQLException e) {
			throw new QueryException(e);
		}
	}

	@Override
	public boolean existsEntry(RegisteredTable table, TableObject object) {
		DatabaseColumn column = table.getIDColumn();

		PreparedStatement check = null;
		ResultSet result = null;
		try {
			check = getConnection().prepareStatement(String
					.format("SELECT %s FROM %s WHERE %s=%s;", column.getName(), table.getName(), column
							.getName(), column.getValue(object)));

			result = check.executeQuery();
			return result.next();
		} catch (SQLException e) {
			throw new QueryException(e);
		} finally {
			handleClose(check, result);
		}
	}

	@Override
	public boolean existsEntry(TableObject object) {
		return this.existsEntry(getRegisteredTable(object.getClass()), object);
	}

	@Override
	protected long getLastID(RegisteredTable table) {
		DatabaseColumn idColumn = table.getIDColumn();
		PreparedStatement check = null;
		ResultSet result = null;
		try {
			check = getConnection().prepareStatement(String
					.format("SELECT %s FROM %s ORDER BY %s DESC LIMIT 1;", idColumn.getName(), table.getName(), idColumn
							.getName()));
			result = check.executeQuery();
			if (!result.next()) {
				return 1;
			}
			long lastId = result.getLong(idColumn.getName());
			result.close();
			check.close();
			return lastId;
		} catch (SQLException e) {
			throw new QueryException(e);
		} finally {
			handleClose(check, result);
		}
	}

	public static void handleClose(Statement check, ResultSet result) {
		try {
			if (check != null) {
				check.close();
			}
			if (result != null) {
				result.close();
			}
		} catch (SQLException e) {
			throw new QueryException(e);
		}
	}

	@Override
	public boolean isSupported(Class<?> type) {
		return isSupportedByDatabase(type);
	}

	static boolean isSupportedByDatabase(Class<?> type) {
		return type.isPrimitive() || Number.class.isAssignableFrom(type)
				|| type == boolean.class || type == Boolean.class || type == AtomicBoolean.class
				|| type == char.class || type == Character.class
				|| type == Date.class || type == Timestamp.class
				|| type == String.class;
	}

	public ResultSet query(String query) {
		Statement statement;
		try {
			statement = getConnection().createStatement();
			return statement.executeQuery(query);
		} catch (SQLException e) {
			throw new QueryException(e);
		}
	}

	@Override
	public void sendKeepAliveQuery() {
		testConnection();
	}

	@Override
	public boolean testConnection() {
		try {
			query("SELECT 1;");
			return true;
		} catch (QueryException ignored) {
			return false;
		}
	}
}