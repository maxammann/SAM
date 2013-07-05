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


import org.p000ison.dev.sam.DatabaseColumn;
import org.p000ison.dev.sam.RegisteredTable;
import org.p000ison.dev.sam.TableObject;
import org.p000ison.dev.sam.exception.QueryException;
import org.p000ison.dev.sam.query.PreparedSelectQuery;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Represents a JBDCPreparedQuery
 */
public class JBDCPreparedSelectQuery<T extends TableObject> extends JBDCPreparedQuery implements PreparedSelectQuery<T> {
	private final RegisteredTable table;

	protected JBDCPreparedSelectQuery(JBDCDatabase database, String query, RegisteredTable table) {
		super(database, query);
		this.table = table;
	}

	static Object getDatabaseFromResultSet(int index, ResultSet set, Class<?> type) {
		try {
			if (type == boolean.class || type == Boolean.class || type == AtomicBoolean.class) {
				return set.getBoolean(index);
			} else if (type == byte.class || type == Byte.class) {
				return set.getByte(index);
			} else if (type == short.class || type == Short.class) {
				return set.getShort(index);
			} else if (type == int.class || type == Integer.class || type == AtomicInteger.class) {
				return set.getInt(index);
			} else if (type == float.class || type == Float.class) {
				return set.getFloat(index);
			} else if (type == double.class || type == Double.class) {
				return set.getDouble(index);
			} else if (type == long.class || type == Long.class || type == AtomicLong.class) {
				return set.getLong(index);
			} else if (type == char.class || type == Character.class) {
				return (char) set.getInt(index);
			} else if (type == String.class) {
				return set.getString(index);
			} else if (type == java.util.Date.class || type == java.sql.Timestamp.class) {
				return set.getTimestamp(index);
			} else if (RegisteredTable.isSerializable(type)) {
				return set.getBlob(index);
			}
		} catch (SQLException e) {
			throw new QueryException(e);
		}

		return null;
	}

	@Override
	public <C extends Collection<T>> C getResults(C collection) {
		synchronized (getDatabase()) {
			ResultSet result = null;
			try {
				try {
					if (getPreparedStatement().isClosed()) {
						reset();
					}
				} catch (AbstractMethodError ignored) {
				}

				result = getPreparedStatement().executeQuery();
				List<DatabaseColumn> columns = table.getRegisteredColumns();

				while (result.next()) {
					T object = table.createNewInstance();

					for (int i = 0; i < columns.size(); i++) {
						DatabaseColumn column = columns.get(i);

						Object obj = null;


						if (JBDCDatabase.isSupportedByDatabase(column.getType())) {
							obj = getDatabaseFromResultSet(i + 1, result, column.getType());
						} else {
							ObjectInputStream inputStream = null;
							try {
								InputStream selectedInputStream = result.getBinaryStream(i + 1);
								if (selectedInputStream != null) {
									inputStream = new ObjectInputStream(selectedInputStream);
									obj = inputStream.readObject();
								}
							} catch (IOException e) {
								throw new QueryException(e);
							} catch (ClassNotFoundException e) {
								throw new QueryException(e);
							} finally {
								try {
									if (inputStream != null) {
										inputStream.close();
									}
								} catch (IOException e) {
									throw new QueryException(e);
								}
							}
						}

						column.setValue(object, obj);
					}

					collection.add(object);
				}
			} catch (SQLException e) {
				if (isAutoReset()) {
					reset();
				}
				throw new QueryException(e);
			} finally {
				JBDCDatabase.handleClose(null, result);
			}
			return collection;
		}
	}

	@Override
	public List<T> getResults() {
		return getResults(new ArrayList<T>());
	}
}
