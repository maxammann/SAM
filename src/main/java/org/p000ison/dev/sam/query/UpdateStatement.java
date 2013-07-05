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

package org.p000ison.dev.sam.query;

import org.p000ison.dev.sam.Database;
import org.p000ison.dev.sam.DatabaseColumn;
import org.p000ison.dev.sam.RegisteredTable;
import org.p000ison.dev.sam.TableObject;
import org.p000ison.dev.sam.exception.QueryException;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a UpdateStatement
 */
public class UpdateStatement extends SelectiveQuery<UpdateStatement, TableObject> {
	private List<DatabaseColumn> columns = new LinkedList<DatabaseColumn>();
	private List<Object> values = new LinkedList<Object>();

	public UpdateStatement(Database database) {
		super(database);
	}

	@Override
	protected String getQuery() {
		if (getTable() == null || columns == null || values == null) {
			return null;
		}
		StringBuilder query = new StringBuilder("UPDATE ").append(getTable().getName()).append(" SET ");

		for (DatabaseColumn column : columns) {
			query.append(column.getName()).append("=?,");
		}
		query.deleteCharAt(query.length() - 1);

		createWhereQuery(query);

		query.append(';');

		return query.toString();
	}

	@Override
	public PreparedQuery prepare() {
		String query = getQuery();
		if (query == null) {
			throw new QueryException("The query is not prepared!");
		}

		PreparedQuery preparedQuery = getDatabase().getQueryFactory().createPreparedStatement(getQuery());

		int i;
		if (values != null) {
			for (i = 0; i < values.size(); i++) {
				preparedQuery.set(i, values.get(i));
			}
		} else {
			i = columns.size() - 1;
		}

		setWhereValues(i, preparedQuery);

		return preparedQuery;
	}

	public UpdateStatement columns(List<DatabaseColumn> columns) {
		this.columns = columns;
		return this;
	}

	public UpdateStatement column(DatabaseColumn column) {
		this.columns.add(column);
		return this;
	}

	public UpdateStatement column(String column) {

		DatabaseColumn databaseColumn = getTable().getColumn(column);

		if (databaseColumn == null) {
			throw new IllegalArgumentException("Column " + column + " does not exist!");
		}

		this.columns.add(databaseColumn);
		return this;
	}

	public UpdateStatement values(List<Object> values) {
		this.values = values;
		return this;
	}

	public UpdateStatement value(Object value) {
		this.values.add(value);
		return this;
	}

	public UpdateStatement update(DatabaseColumn column, Object value) {
		columns.add(column);
		values.add(value);
		return this;
	}

	public synchronized UpdateStatement in(Class<? extends TableObject> object) {
		super.from(object);
		return this;
	}

	public synchronized UpdateStatement in(RegisteredTable table) {
		super.from(table);
		return this;
	}

	/**
	 * This method is not really depreciated, you just should use {@link #in(Class)}
	 */
	@Override
	@Deprecated
	public synchronized AbstractStatement<TableObject> from(Class<? extends TableObject> object) {
		throw new UnsupportedOperationException();
	}

	/**
	 * This method is not really depreciated, you just should use {@link #in(org.p000ison.dev.sam.RegisteredTable)}
	 */
	@Override
	@Deprecated
	public synchronized AbstractStatement<TableObject> from(RegisteredTable table) {
		throw new UnsupportedOperationException();
	}
}
