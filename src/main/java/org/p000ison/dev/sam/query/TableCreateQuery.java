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
import org.p000ison.dev.sam.annotation.Index;
import org.p000ison.dev.sam.key.Key;
import org.p000ison.dev.sam.key.PrimaryKey;

import java.util.List;

/**
 * Represents a TableCreateQuery
 */
public class TableCreateQuery {

	private final Database database;
	private String table;
	private DatabaseColumn[] buildingColumns;

	public TableCreateQuery(Database database) {
		this.database = database;
	}

	public synchronized TableCreateQuery table(String table) {
		this.table = table;
		return this;
	}

	public TableCreateQuery columns(DatabaseColumn... columns) {
		this.buildingColumns = columns;
		return this;
	}

	public TableCreateQuery columns(List<DatabaseColumn> columns) {
		this.buildingColumns = columns.toArray(new DatabaseColumn[columns.size()]);
		return this;
	}

	protected String getQuery() {
		StringBuilder query = new StringBuilder("CREATE TABLE ").append(table).append(" (");

		for (DatabaseColumn column : buildingColumns) {
			query.append(database.getQueryFactory().buildColumn(column));

			//todo better way
			if (column.isID()) {
				String columnConstraint = database.getRegisteredKey(PrimaryKey.class).getColumnConstraint(column, null);
				if (columnConstraint != null) {
					query.append(' ').append(columnConstraint);
				}
			}

			for (Index index : column.getIndices()) {
				Key key = database.getRegisteredKey(index.type());

				String columnConstraint = key.getColumnConstraint(column, index.value());
				if (columnConstraint != null) {
					query.append(' ').append(columnConstraint);
				}
			}
			query.append(',');
		}

		for (DatabaseColumn column : buildingColumns) {
			for (Index index : column.getIndices()) {
				Key key = database.getRegisteredKey(index.type());

				//todo better way
				if (column.isID()) {
					String tableConstraint = database.getRegisteredKey(PrimaryKey.class)
							.getTableConstraint(column, null);
					if (tableConstraint != null) {
						query.append(' ').append(tableConstraint);
					}
				}

				String tableConstraint = key.getTableConstraint(column, index.value());
				if (tableConstraint != null) {
					query.append(tableConstraint).append(',');
				}
			}
		}

		query.deleteCharAt(query.length() - 1);
		query.append(");");
		return query.toString();
	}

	public boolean update() {
		return database.executeDirectUpdate(getQuery());
	}

	public PreparedQuery prepare() {
		return database.getQueryFactory().createPreparedStatement(getQuery());
	}
}
