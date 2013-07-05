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

/**
 * Represents a AbstractStatement
 */
public abstract class AbstractStatement<T extends Model> implements Statement{

	private final Database database;
	private RegisteredTable table;

	public AbstractStatement(Database database) {
		this.database = database;
	}

	public synchronized AbstractStatement<T> from(Class<? extends T> object) {
		this.table = database.getRegisteredTable(object);
		return this;
	}

	public synchronized AbstractStatement<T> from(RegisteredTable table) {
		this.table = table;
		return this;
	}

	protected synchronized RegisteredTable getTable() {
		return table;
	}

	protected Database getDatabase() {
		return database;
	}

	public AbstractStatement<T> reset() {return this;}
}
