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

import com.p000ison.dev.sqlapi.DatabaseColumn;
import com.p000ison.dev.sqlapi.TableObject;

/**
 * The WHERE part of a query. Used in {@link SelectQuery}
 */
@SuppressWarnings("unused")
public interface WhereQuery<T extends TableObject> {

    WhereComparator<T> equals(DatabaseColumn column, Object expected);

    WhereComparator<T> preparedEquals(DatabaseColumn column);

    WhereComparator<T> like(DatabaseColumn column, Object expected);

    WhereComparator<T> preparedLike(DatabaseColumn column);

    WhereComparator<T> notEquals(DatabaseColumn column, Object expected);

    WhereComparator<T> preparedNotEquals(DatabaseColumn column);

    WhereComparator<T> lessThan(DatabaseColumn column, Object expected);

    WhereComparator<T> preparedLessThan(DatabaseColumn column);

    WhereComparator<T> greaterThan(DatabaseColumn column, Object expected);

    WhereComparator<T> preparedGreaterThan(DatabaseColumn column);

    WhereComparator<T> equals(String column, Object expected);

    WhereComparator<T> preparedEquals(String column);

    WhereComparator<T> like(String column, Object expected);

    WhereComparator<T> preparedLike(String column);

    WhereComparator<T> notEquals(String column, Object expected);

    WhereComparator<T> preparedNotEquals(String column);

    WhereComparator<T> lessThan(String column, Object expected);

    WhereComparator<T> preparedLessThan(String column);

    WhereComparator<T> greaterThan(String column, Object expected);

    WhereComparator<T> preparedGreaterThan(String column);
}
