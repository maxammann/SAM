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
 * Last modified: 21.12.12 00:08
 */

package com.p000ison.dev.sqlapi;

/**
 * A order entry, used in {@link com.p000ison.dev.sqlapi.query.SelectQuery}
 * <p/>
 * <strong>Info:</strong>
 * <p/>
 * All Default... classes are pre-made classes which may already work with your database engine.
 */
public class DefaultOrderEntry {

    private final String order;
    private final boolean desc;

    DefaultOrderEntry(String order, boolean desc)
    {
        this.order = order;
        this.desc = desc;
    }


    protected String getOrder()
    {
        return order;
    }

    protected boolean isDescending()
    {
        return desc;
    }
}
