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
 * Last modified: 18.12.12 18:30
 */

package com.p000ison.dev.sqlapi;

/**
 * Every class which represents a table in a database needs to implements this interface.
 * To define a name of a table you need to use the class-annotation {@link com.p000ison.dev.sqlapi.annotation.Table}.
 * To define columns you can use the annotation {@link com.p000ison.dev.sqlapi.annotation.Column} for fields.
 * <p/>
 * <p>Annotations:</p>
 * <ul>
 * <li>{@link com.p000ison.dev.sqlapi.annotation.Table}</li>
 * <li>{@link com.p000ison.dev.sqlapi.annotation.Column}</li>
 * </ul>
 * <p/>
 * </code>
 */
public interface TableObject {
}
