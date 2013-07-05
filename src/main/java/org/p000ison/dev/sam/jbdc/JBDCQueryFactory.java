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

package org.p000ison.dev.sam.jbdc;

import org.p000ison.dev.sam.QueryFactory;
import org.p000ison.dev.sam.RegisteredTable;
import org.p000ison.dev.sam.TableObject;
import org.p000ison.dev.sam.query.PreparedQuery;
import org.p000ison.dev.sam.query.PreparedSelectQuery;

/**
 * Represents a JBDCQueryFactory
 */
public abstract class JBDCQueryFactory extends QueryFactory.Default {

	public JBDCQueryFactory(JBDCDatabase database) {
		super(database);
	}

	@Override
	public PreparedQuery createPreparedStatement(String query) {
		return new JBDCPreparedQuery((JBDCDatabase) getDatabase(), query);
	}

	@Override
	public <T extends TableObject> PreparedSelectQuery<T> createPreparedSelectQuery(String query, RegisteredTable table) {
		return new JBDCPreparedSelectQuery<T>((JBDCDatabase) getDatabase(), query, table);
	}
}
