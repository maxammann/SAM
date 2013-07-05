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

package org.p000ison.dev.sam;

import org.p000ison.dev.sam.query.*;

/**
 * Represents a QueryFactory
 */
public interface QueryFactory {

	<T extends TableObject> SelectQuery<T> createSelectQuery();

	UpdateStatement createUpdateStatement();

	InsertStatement createInsertStatement();

	DeleteStatement createDeleteStatement();

	TableCreateQuery createTableCreateStatement();

	AddColumnStatement createAddColumnStatement();

	DropColumnStatement createDropColumnStatement();

	/**
	 * Builds a column. it returns for example: "column INTEGER(5) NOT NULL UNIQUE KEY"
	 *
	 * @param column The DatabaseColumn object which holds all information about the column.
	 */
	StringBuilder buildColumn(DatabaseColumn column);

	/**
	 * Creates a new PreparedQuery which can be executed now or later.
	 *
	 * @param query The query to prepare
	 * @return A PreparedQuery
	 */
	PreparedQuery createPreparedStatement(String query);

	<T extends TableObject> PreparedSelectQuery<T> createPreparedSelectQuery(String query, RegisteredTable table);

	public static abstract class Default implements QueryFactory {

		private final Database database;

		public Default(Database database) {
			this.database = database;
		}

		@Override
		public <T extends TableObject> SelectQuery<T> createSelectQuery() {
			return new SelectQuery<T>(database);
		}

		@Override
		public UpdateStatement createUpdateStatement() {
			return new UpdateStatement(database);
		}

		@Override
		public InsertStatement createInsertStatement() {
			return new InsertStatement(database);
		}

		@Override
		public DeleteStatement createDeleteStatement() {
			return new DeleteStatement(database);
		}

		@Override
		public TableCreateQuery createTableCreateStatement() {
			return new TableCreateQuery(database);
		}

		public Database getDatabase() {
			return database;
		}

		@Override
		public AddColumnStatement createAddColumnStatement() {
			return new AddColumnStatement(database);
		}

		@Override
		public DropColumnStatement createDropColumnStatement() {
			return new DropColumnStatement(database);
		}
	}
}
