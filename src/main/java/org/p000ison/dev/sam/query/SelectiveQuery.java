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

import org.p000ison.dev.sam.Database;
import org.p000ison.dev.sam.TableObject;

import java.util.List;

/**
 * Represents a SelectiveQuery
 */
public abstract class SelectiveQuery<Q extends SelectiveQuery<Q, T>, T extends TableObject> extends AbstractStatement<T> {

	private WhereCondition<Q, T> whereQuery;

	public SelectiveQuery(Database database) {
		super(database);
	}

	@SuppressWarnings("unchecked")
	public WhereCondition<Q, T> where() {
		return whereQuery = new WhereCondition<Q, T>((Q) this);
	}

	public WhereCondition<Q, T> getSelectiveQuery() {
		return whereQuery;
	}

	protected StringBuilder createWhereQuery(StringBuilder query) {
		if (getSelectiveQuery() != null) {
			query.append(" WHERE ");
			List<WhereComparator<Q, T>> comparators = whereQuery.getComparators();

			if (!comparators.isEmpty()) {
				for (WhereComparator<Q, T> comparator : comparators) {
					query.append(comparator.getColumn()).append(comparator.getOperator()).append('?');

					if (comparator.isAnd()) {
						query.append(" AND ");
					} else if (comparator.isOr()) {
						query.append(" OR ");
					} else {
						break;
					}

				}
			}
		}

		return query;
	}

	protected void setWhereValues(int offset, PreparedQuery query) {
		if (getSelectiveQuery() != null) {
			List<WhereComparator<Q, T>> comparators = getSelectiveQuery().getComparators();
			for (int i = 0; i < comparators.size(); i++) {
				WhereComparator<Q, T> comparator = comparators.get(i);
				if (!comparator.isPrepared()) {
					query.set(offset + i, comparator.getExpectedValue());
				}
			}
		}
	}

	@Override
	public SelectiveQuery<Q, T> reset() {
		this.whereQuery = null;
		super.reset();
		return this;
	}
}
