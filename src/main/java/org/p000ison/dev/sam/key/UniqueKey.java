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

package org.p000ison.dev.sam.key;

import org.p000ison.dev.sam.DatabaseColumn;

/**
 * Represents a SQLiteUniqueKey
 */
public class UniqueKey extends Key {

	public UniqueKey(boolean modify) {
		super(modify);
	}

	public UniqueKey() {
		super(true);
	}

	@Override
	public String getTableConstraint(DatabaseColumn column, String[] values) {
		return "UNIQUE KEY" + getColumnConstraint(column, values) + "(" + column.getName() + ")";
	}

	@Override
	public String getColumnConstraint(DatabaseColumn column, String[] values) {
		return null;
	}
}
