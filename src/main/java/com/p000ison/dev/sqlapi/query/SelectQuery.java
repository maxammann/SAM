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
 * Last modified: 18.12.12 17:27
 */

package com.p000ison.dev.sqlapi.query;

import com.p000ison.dev.sqlapi.Column;
import com.p000ison.dev.sqlapi.RegisteredTable;
import com.p000ison.dev.sqlapi.TableObject;

/**
 *
 */
public interface SelectQuery<T extends TableObject> {

    SelectQuery<T> from(Class<T> object);

    SelectQuery<T> from(RegisteredTable table);

    WhereQuery<T> where();

    SelectQuery<T> orderBy(Column order);

    SelectQuery<T> orderByDescending(Column order);

    SelectQuery<T> orderBy(String order);

    SelectQuery<T> orderByDescending(String order);

    SelectQuery<T> limit(int max);

    SelectQuery<T> limit(int from, int to);

    SelectQuery<T> reset();

    PreparedSelectQuery<T> prepare();
}
