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

import com.p000ison.dev.sqlapi.TableObject;

/**
 *
 */
public interface WhereComparator<T extends TableObject> {

    WhereQuery<T> or();

    WhereQuery<T> and();

    /**
     * Return the SelectQuery you used previously.
     *
     * @return The SelectQuery
     */
    SelectQuery<T> select();
}
