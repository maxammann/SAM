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

import org.p000ison.dev.sam.*;
import org.p000ison.dev.sam.Model;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a InsertStatement
 */
public class InsertStatement extends AbstractStatement<Model> {

	private List<DatabaseColumn> columns = new LinkedList<DatabaseColumn>();
	private List<Object> values = new LinkedList<Object>();

	public InsertStatement(Database database) {
		super(database);
	}

	@Override
	public String getQuery() {
		if (getTable() == null || columns == null) {
			return null;
		}
		StringBuilder query = new StringBuilder("INSERT INTO ").append(getTable().getName()).append(" (");

		for (DatabaseColumn column : columns) {
			query.append(column.getName()).append(",");
		}
		query.deleteCharAt(query.length() - 1).append(") VALUES (");

		for (Object ignored : columns) {
			query.append("?,");
		}

		query.deleteCharAt(query.length() - 1);

		query.append(");");

		return query.toString();
	}

	@Override
	public PreparedQuery prepare() {
		String query = getQuery();
		if (query == null) {
			throw new QueryException("The query is not prepared!");
		}

		PreparedQuery preparedQuery = getDatabase().getQueryFactory().createPreparedStatement(getQuery());
		if (values != null) {
			for (int i = 0; i < values.size(); i++) {
				preparedQuery.set(i, values.get(i));
			}
		}

		return preparedQuery;
	}

	public InsertStatement columns(List<DatabaseColumn> columns) {
		this.columns = columns;
		return this;
	}

	public InsertStatement column(DatabaseColumn column) {
		this.columns.add(column);
		return this;
	}

	public InsertStatement column(String column) {

		DatabaseColumn databaseColumn = getTable().getColumn(column);

		if (databaseColumn == null) {
			throw new IllegalArgumentException("Column " + column + " does not exist!");
		}

		this.columns.add(databaseColumn);
		return this;
	}

	public InsertStatement values(List<Object> values) {
		this.values = values;
		return this;
	}

	public InsertStatement value(Object value) {
		this.values.add(value);
		return this;
	}

	public InsertStatement insert(DatabaseColumn column, Object value) {
		columns.add(column);
		values.add(value);
		return this;
	}

	public synchronized InsertStatement into(Class<? extends Model> object) {
		super.from(object);
		return this;
	}

	public synchronized InsertStatement into(RegisteredTable table) {
		super.from(table);
		return this;
	}

	/**
	 * This method is not really depreciated, you just should use {@link #into(org.p000ison.dev.sam.RegisteredTable)}
	 */
	@Override
	@Deprecated
	public synchronized AbstractStatement<Model> from(RegisteredTable table) {
		throw new UnsupportedOperationException();
	}

	/**
	 * This method is not really depreciated, you just should use {@link #into(Class)}
	 */
	@Override
	@Deprecated
	public synchronized AbstractStatement<Model> from(Class<? extends Model> object) {
		throw new UnsupportedOperationException();
	}
}
