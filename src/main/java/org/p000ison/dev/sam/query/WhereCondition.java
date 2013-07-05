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

import org.p000ison.dev.sam.DatabaseColumn;
import org.p000ison.dev.sam.TableObject;

import java.util.ArrayList;
import java.util.List;

/**
 * The WHERE part of your query.
 * <p/>
 * <strong>Info:</strong>
 * <p/>
 * All Default... classes are pre-made classes which may already work with your database engine.
 */
public class WhereCondition<Q extends SelectiveQuery<Q, T>, T extends TableObject> {

	private List<WhereComparator<Q, T>> comparators;
	private Q query;

	WhereCondition(Q query) {
		this.query = query;
		this.comparators = new ArrayList<WhereComparator<Q, T>>();
	}

	public WhereComparator<Q, T> equals(DatabaseColumn column, Object expected) {
		return equals(column.getName(), expected);
	}

	public WhereComparator<Q, T> preparedEquals(DatabaseColumn column) {
		return preparedEquals(column.getName());
	}

	public WhereComparator<Q, T> like(DatabaseColumn column, Object expected) {
		return like(column.getName(), expected);
	}

	public WhereComparator<Q, T> preparedLike(DatabaseColumn column) {
		return preparedLike(column.getName());
	}

	public WhereComparator<Q, T> notEquals(DatabaseColumn column, Object expected) {
		return notEquals(column.getName(), expected);
	}

	public WhereComparator<Q, T> preparedNotEquals(DatabaseColumn column) {
		return preparedNotEquals(column.getName());
	}

	public WhereComparator<Q, T> lessThan(DatabaseColumn column, Object expected) {
		return lessThan(column.getName(), expected);
	}

	public WhereComparator<Q, T> preparedLessThan(DatabaseColumn column) {
		return preparedLessThan(column.getName());
	}

	public WhereComparator<Q, T> greaterThan(DatabaseColumn column, Object expected) {
		return greaterThan(column.getName(), expected);
	}

	public WhereComparator<Q, T> preparedGreaterThan(DatabaseColumn column) {
		return preparedGreaterThan(column.getName());
	}

	public WhereComparator<Q, T> equals(String column, Object expected) {
		return addComparator(column, CompareOperator.EQUALS, expected);
	}

	public WhereComparator<Q, T> preparedEquals(String column) {
		return addPreparedComparator(column, CompareOperator.EQUALS);
	}

	public WhereComparator<Q, T> like(String column, Object expected) {
		return addComparator(column, CompareOperator.LIKE, expected);
	}

	public WhereComparator<Q, T> preparedLike(String column) {
		return addPreparedComparator(column, CompareOperator.LIKE);
	}

	public WhereComparator<Q, T> notEquals(String column, Object expected) {
		return addComparator(column, CompareOperator.NOT_EQUAL, expected);
	}

	public WhereComparator<Q, T> preparedNotEquals(String column) {
		return addPreparedComparator(column, CompareOperator.NOT_EQUAL);
	}

	public WhereComparator<Q, T> lessThan(String column, Object expected) {
		return addComparator(column, CompareOperator.LESS_THAN, expected);
	}

	public WhereComparator<Q, T> preparedLessThan(String column) {
		return addPreparedComparator(column, CompareOperator.LESS_THAN);
	}

	public WhereComparator<Q, T> greaterThan(String column, Object expected) {
		return addComparator(column, CompareOperator.GREATER_THAN, expected);
	}

	public WhereComparator<Q, T> preparedGreaterThan(String column) {
		return addPreparedComparator(column, CompareOperator.GREATER_THAN);
	}

	private WhereComparator<Q, T> addComparator(String column, CompareOperator compareOperator, Object expected) {
		WhereComparator<Q, T> comparator = new WhereComparator<Q, T>(query, compareOperator, column, expected);
		comparators.add(comparator);
		return comparator;
	}

	private WhereComparator<Q, T> addPreparedComparator(String column, CompareOperator compareOperator) {
		WhereComparator<Q, T> comparator = new WhereComparator<Q, T>(query, compareOperator, column);
		comparators.add(comparator);
		return comparator;
	}

	protected List<WhereComparator<Q, T>> getComparators() {
		return comparators;
	}
}
