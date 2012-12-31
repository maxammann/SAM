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
 * Last modified: 20.12.12 20:22
 */

package com.p000ison.dev.sqlapi.query;

/**
 * A enum with the default comparators like '<', '>', '>=', '<=', '=' or LIKE;
 */
public enum CompareOperator {
    EQUALS("="), GREATER_THAN(">"), LESS_THAN("<"), NOT_EQUAL("!="), LIKE("LIKE");

    private String sign;

    private CompareOperator(String sign)
    {
        this.sign = sign;
    }

    @Override
    public String toString()
    {
        return sign;
    }
}
