/*
 * This file is part of SQLDatabaseAPI (2012).
 *
 * SQLDatabaseAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General  License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SQLDatabaseAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General  License for more details.
 *
 * You should have received a copy of the GNU General  License
 * along with SQLDatabaseAPI.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Last modified: 26.12.12 22:14
 */

package com.p000ison.dev.sqlapi.query;

import com.p000ison.dev.sqlapi.Column;
import com.p000ison.dev.sqlapi.TableObject;

/**
 * Represents a WhereQuery
 */
@SuppressWarnings("unused")
public interface WhereQuery<T extends TableObject> {

    WhereComparator<T> equals(Column column, Object expected);

    WhereComparator<T> preparedEquals(Column column);

    WhereComparator<T> notEquals(Column column, Object expected);

    WhereComparator<T> preparedNotEquals(Column column);

    WhereComparator<T> lessThan(Column column, Object expected);

    WhereComparator<T> preparedLessThan(Column column);

    WhereComparator<T> greaterThan(Column column, Object expected);

    WhereComparator<T> preparedGreaterThan(Column column);

    WhereComparator<T> equals(String column, Object expected);

    WhereComparator<T> preparedEquals(String column);

    WhereComparator<T> notEquals(String column, Object expected);

    WhereComparator<T> preparedNotEquals(String column);

    WhereComparator<T> lessThan(String column, Object expected);

    WhereComparator<T> preparedLessThan(String column);

    WhereComparator<T> greaterThan(String column, Object expected);

    WhereComparator<T> preparedGreaterThan(String column);
}
