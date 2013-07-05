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

package org.p000ison.dev.sam.query;

import org.p000ison.dev.sam.Model;

import java.util.Collection;
import java.util.List;

/**
 * This is used to prepare queries/statements.
 */
@SuppressWarnings("unused")
public interface PreparedSelectQuery<T extends Model> extends PreparedQuery {

	/**
	 * Queries the database and returns a collection of Model. This method should be synchronized with the {@link org.p000ison.dev.sam.Database}.
	 *
	 * @return Whether the update was successfully
	 */
	<C extends Collection<T>> C getResults(C collection);

	/**
	 * Queries the database and returns a list of Model. This method should be synchronized with the {@link org.p000ison.dev.sam.Database}.
	 *
	 * @return Whether the update was successfully
	 */
	List<T> getResults();
}
