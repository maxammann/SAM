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
import org.p000ison.dev.sam.RegisteredTable;
import org.p000ison.dev.sam.Model;
import org.p000ison.dev.sam.QueryException;

/**
 * Represents a DeleteStatement
 */
public class DeleteStatement extends SelectiveQuery<DeleteStatement, Model> {

	public DeleteStatement(Database database) {
		super(database);
	}

	@Override
	public String getQuery() {
		if (getTable() == null) {
			return null;
		}
		StringBuilder query = new StringBuilder("DELETE FROM ").append(getTable().getName());
		super.createWhereQuery(query);

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
		setWhereValues(0, preparedQuery);

		return preparedQuery;
	}

	@Override
	public synchronized DeleteStatement from(Class<? extends Model> object) {
		super.from(object);
		return this;
	}

	@Override
	public synchronized DeleteStatement from(RegisteredTable table) {
		super.from(table);
		return this;
	}

}
