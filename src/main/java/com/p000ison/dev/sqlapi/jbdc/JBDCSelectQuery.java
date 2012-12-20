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
 * Last modified: 20.12.12 17:39
 */

package com.p000ison.dev.sqlapi.jbdc;

import com.p000ison.dev.sqlapi.DefaultSelectQuery;
import com.p000ison.dev.sqlapi.TableObject;

/**
 * Represents a JBDCSelectQuery
 */
public class JBDCSelectQuery<T extends TableObject> extends DefaultSelectQuery<T> {

    public JBDCSelectQuery(JBDCDatabase database)
    {
        super(database);
    }

    @Override
    protected JBDCPreparedSelectQuery<T> getPreparedQuery()
    {
        return new JBDCPreparedSelectQuery<T>((JBDCDatabase) getDatabase(), getQuery(), getTable());
    }
}
