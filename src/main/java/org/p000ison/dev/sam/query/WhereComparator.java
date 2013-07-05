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

/**
 * A where entry, used in {@link SelectQuery}
 * <p/>
 * <strong>Info:</strong>
 * <p/>
 * All Default... classes are pre-made classes which may already work with your database engine.
 */
public class WhereComparator<Q extends SelectiveQuery<Q, T>, T extends Model> {

	private Q query;
	private boolean and, or;
	private String column;
	private Object expectedValue;
	private CompareOperator operator;
	private boolean prepared = false;

	WhereComparator(Q query, CompareOperator operator, String column, Object expectedValue) {
		this.query = query;
		this.column = column;
		this.operator = operator;
		this.expectedValue = expectedValue;
	}

	WhereComparator(Q query, CompareOperator operator, String column) {
		this.query = query;
		this.column = column;
		this.operator = operator;
		this.prepared = true;
	}

	public WhereCondition<Q, T> or() {
		or = true;
		return query.getSelectiveQuery();
	}

	public WhereCondition<Q, T> and() {
		and = true;
		return query.getSelectiveQuery();
	}

	/**
	 * Return the SelectQuery you used previously.
	 *
	 * @return The SelectQuery
	 */
	public Q select() {
		return query;
	}

	protected boolean isOr() {
		return or;
	}

	protected boolean isAnd() {
		return and;
	}

	protected String getColumn() {
		return column;
	}

	protected Object getExpectedValue() {
		return expectedValue;
	}

	protected CompareOperator getOperator() {
		return operator;
	}

	public boolean isPrepared() {
		return prepared;
	}
}
