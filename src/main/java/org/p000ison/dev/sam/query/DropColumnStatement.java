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
import org.p000ison.dev.sam.Model;
import org.p000ison.dev.sam.RegisteredTable;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a AddColumnStatement
 */
public class DropColumnStatement extends AbstractStatement<Model> {

	private List<String> columns = new LinkedList<String>();

	public DropColumnStatement(Database database) {
		super(database);
	}

	public DropColumnStatement column(String column) {
		columns.add(column);
		return this;
	}

	public DropColumnStatement columns(List<String> columns) {
		this.columns = columns;
		return this;
	}

	@Override
	public String getQuery() {
		StringBuilder alter = new StringBuilder("ALTER TABLE ").append(getTable().getName()).append(" DROP COLUMN ");

		for (String column : columns) {
			alter.append(column).append(',');
		}

		alter.deleteCharAt(alter.length() - 1);
		alter.append(";");

		return alter.toString();
	}

	public boolean update() {
		return getDatabase().executeDirectUpdate(getQuery());
	}

	@Override
	public PreparedQuery prepare() {
		return getDatabase().getQueryFactory().createPreparedStatement(getQuery());
	}

	public synchronized DropColumnStatement in(Class<? extends Model> object) {
		super.from(object);
		return this;
	}

	public synchronized DropColumnStatement in(RegisteredTable table) {
		super.from(table);
		return this;
	}

	/**
	 * This method is not really depreciated, you just should use {@link #in(Class)}
	 */
	@Override
	@Deprecated
	public synchronized AbstractStatement<Model> from(Class<? extends Model> object) {
		throw new UnsupportedOperationException();
	}

	/**
	 * This method is not really depreciated, you just should use {@link #in(org.p000ison.dev.sam.RegisteredTable)}
	 */
	@Override
	@Deprecated
	public synchronized AbstractStatement<Model> from(RegisteredTable table) {
		throw new UnsupportedOperationException();
	}
}
