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
import org.p000ison.dev.sam.DatabaseColumn;
import org.p000ison.dev.sam.RegisteredTable;
import org.p000ison.dev.sam.TableObject;
import org.p000ison.dev.sam.exception.QueryException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A default selection query which may work with your database engine
 * <p/>
 * <strong>Info:</strong>
 * <p/>
 * All Default... classes are pre-made classes which may already work with your database engine.
 */
public class SelectQuery<T extends TableObject> extends SelectiveQuery<SelectQuery<T>, T> {
	private List<OrderEntry> orderBy = new CopyOnWriteArrayList<OrderEntry>();
	private int[] limits;

	public SelectQuery(Database database) {
		super(database);
	}

	@Override
	public synchronized SelectQuery<T> from(Class<? extends T> object) {
		super.from(object);
		return this;
	}

	@Override
	public synchronized SelectQuery<T> from(RegisteredTable table) {
		super.from(table);
		return this;
	}

	public SelectQuery<T> orderBy(DatabaseColumn order) {
		return orderBy(order.getName());
	}

	public SelectQuery<T> orderByDescending(DatabaseColumn order) {
		return orderByDescending(order.getName());
	}

	public SelectQuery<T> orderByDescending(String order) {
		orderBy.add(new OrderEntry(order, true));
		return this;
	}

	public SelectQuery<T> orderBy(String order) {
		orderBy.add(new OrderEntry(order, false));
		return this;
	}

	@Override
	public final synchronized PreparedSelectQuery<T> prepare() {
		String query = getQuery();
		if (query == null) {
			throw new QueryException("The query is not prepared!");
		}

		PreparedSelectQuery<T> preparedQuery = getDatabase().getQueryFactory()
				.createPreparedSelectQuery(getQuery(), getTable());
		setWhereValues(0, preparedQuery);

		return preparedQuery;
	}

	@Override
	protected synchronized String getQuery() {
		if (getTable() == null) {
			return null;
		}

		StringBuilder query = new StringBuilder("SELECT ");
		List<DatabaseColumn> columns = getTable().getRegisteredColumns();

		int end = columns.size() - 1;
		for (int i = 0; i < columns.size(); i++) {
			DatabaseColumn column = columns.get(i);
			query.append(column.getName());
			if (i != end) {
				query.append(',');
			}
		}

		query.append(" FROM ").append(getTable().getName());

		createWhereQuery(query);

		if (!orderBy.isEmpty()) {
			query.append(" ORDER BY ");
			for (OrderEntry entry : orderBy) {
				if (entry.getOrder() != null) {
					query.append(entry.getOrder());
					if (!entry.isDescending()) {
						query.append(',');
					}
				}
				if (entry.isDescending()) {
					query.append(" DESC,");
				}
			}

			query.deleteCharAt(query.length() - 1);
		}

		if (limits != null) {
			query.append(" LIMIT ");
			if (limits.length == 1) {
				query.append(limits[0]);
			} else {
				query.append(limits[0]).append(',').append(limits[1]);
			}
		}

		query.append(';');

		return query.toString();
	}

	public synchronized SelectQuery<T> limit(int max) {
		if (max < 1) {
			throw new IllegalArgumentException("The limit must be greater than 0!");
		}
		limits = new int[]{max};
		return this;
	}

	public synchronized SelectQuery<T> limit(int from, int to) {
		if (from > 0 || to > 0) {
			throw new IllegalArgumentException("The limit must be greater than 0!");
		} else if (from > to) {
			throw new IllegalArgumentException("The from limit must be less than the to limit!");
		}

		limits = new int[]{from, to};
		return this;
	}

	@Override
	public synchronized SelectQuery<T> reset() {
		this.orderBy = new ArrayList<OrderEntry>();
		this.limits = null;
		super.reset();
		return this;
	}

	/**
	 * A order entry, used in {@link SelectQuery}
	 * <p/>
	 * <strong>Info:</strong>
	 * <p/>
	 * All Default... classes are pre-made classes which may already work with your database engine.
	 */
	private static class OrderEntry {

		private final String order;
		private final boolean desc;

		OrderEntry(String order, boolean desc) {
			this.order = order;
			this.desc = desc;
		}

		protected String getOrder() {
			return order;
		}

		protected boolean isDescending() {
			return desc;
		}
	}

}
