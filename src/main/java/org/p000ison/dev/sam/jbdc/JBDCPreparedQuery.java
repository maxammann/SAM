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


import org.p000ison.dev.sam.Database;
import org.p000ison.dev.sam.DatabaseColumn;
import org.p000ison.dev.sam.RegisteredTable;
import org.p000ison.dev.sam.exception.QueryException;
import org.p000ison.dev.sam.query.PreparedQuery;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Represents a JBDCPreparedQuery
 */
public class JBDCPreparedQuery implements PreparedQuery {
	private PreparedStatement preparedStatement;
	private final JBDCDatabase database;
	private final String query;
	private boolean autoReset;

	protected JBDCPreparedQuery(JBDCDatabase database, String query) {
		this.query = query;
		this.preparedStatement = database.prepare(query);
		this.autoReset = database.isAutoReset();
		this.database = database;
	}

	static int getDatabaseDataType(Class<?> type) {
		if (type == boolean.class || type == Boolean.class || type == AtomicBoolean.class) {
			return Types.TINYINT;
		} else if (type == byte.class || type == Byte.class) {
			return Types.TINYINT;
		} else if (type == short.class || type == Short.class) {
			return Types.SMALLINT;
		} else if (type == int.class || type == Integer.class || type == AtomicInteger.class) {
			return Types.INTEGER;
		} else if (type == float.class || type == Float.class) {
			return Types.FLOAT;
		} else if (type == double.class || type == Double.class) {
			return Types.DOUBLE;
		} else if (type == long.class || type == Long.class || type == AtomicLong.class) {
			return Types.INTEGER;
		} else if (type == char.class || type == Character.class) {
			return Types.CHAR;
		} else if (type == String.class) {
			return Types.VARCHAR;
		} else if (type == java.util.Date.class || type == java.sql.Timestamp.class) {
			return Types.TIMESTAMP;
		} else if (RegisteredTable.isSerializable(type)) {
			return Types.BLOB;
		}

		return Database.UNSUPPORTED_TYPE;
	}

	@Override
	public void set(int index, Object value) {
		if (index < 0) {
			throw new IllegalArgumentException("The index must be more or equal 0!");
		}

		try {
			try {
				if (preparedStatement.isClosed()) {
					reset();
				}
			} catch (AbstractMethodError ignored) {
			}
			preparedStatement.setObject(index + 1, value);
		} catch (SQLException e) {
			if (autoReset) {
				reset();
			}
			throw new QueryException(e);
		}
	}

	@Override
	public void set(int index, Object value, int databaseType) {
		if (index < 0) {
			throw new IllegalArgumentException("The index must be more or equal 0!");
		}

		try {
			try {
				if (preparedStatement.isClosed()) {
					reset();
				}
			} catch (AbstractMethodError ignored) {
			}
			preparedStatement.setObject(index + 1, value, databaseType);
		} catch (SQLException e) {
			if (autoReset) {
				reset();
			}
			throw new QueryException(e);
		}
	}

	@Override
	public void set(DatabaseColumn column, int index, Object value) {
		if (index < 0) {
			throw new IllegalArgumentException("The index must be more or equal 0!");
		}

		index++;

		try {
			try {
				if (preparedStatement.isClosed()) {
					reset();
				}
			} catch (AbstractMethodError ignored) {
			}
			int type = getDatabaseDataType(column.getType());
			if (type != Database.UNSUPPORTED_TYPE) {
				if (value == null) {
					preparedStatement.setNull(index, type);
				} else {
					if (value instanceof AtomicBoolean) {
						value = ((AtomicBoolean) value).get();
					}
					if (value instanceof AtomicInteger) {
						value = ((AtomicInteger) value).get();
					}
					if (value instanceof AtomicLong) {
						value = ((AtomicLong) value).get();
					}
					preparedStatement.setObject(index, value, type);
				}
			} else if (column.isSerializable()) {
				if (value == null) {
					preparedStatement.setNull(index, Types.BLOB);
				} else {
					ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
					ObjectOutput outputStream = new ObjectOutputStream(byteBuffer);
					outputStream.writeObject(value);
					byte[] bytes = byteBuffer.toByteArray();

					preparedStatement.setBytes(index, bytes);
				}
			}

		} catch (SQLException e) {
			if (autoReset) {
				reset();
			}
			throw new QueryException(e);
		} catch (IOException e) {
			throw new QueryException(e);
		}
	}

	@Override
	public void clearParameters() {
		try {
			if (preparedStatement.isClosed()) {
				reset();
			} else {
				preparedStatement.clearParameters();
			}
		} catch (AbstractMethodError ignored) {
		} catch (SQLException e) {
			if (autoReset) {
				reset();
			}
			throw new QueryException(e);
		}
	}

	@Override
	public boolean update() {
		synchronized (database) {
			try {
				try {
					if (preparedStatement.isClosed()) {
						reset();
					}
				} catch (AbstractMethodError ignored) {
				}
				return preparedStatement.executeUpdate() != 0;
			} catch (SQLException e) {
				if (autoReset) {
					reset();
				}
				throw new QueryException(e);
			}
		}
	}

	public ResultSet query() {
		synchronized (database) {
			try {
				try {
					if (preparedStatement.isClosed()) {
						reset();
					}
				} catch (AbstractMethodError ignored) {
				}
				return preparedStatement.executeQuery();
			} catch (SQLException e) {
				if (autoReset) {
					reset();
				}
				throw new QueryException(e);
			}
		}
	}

	@Override
	public void close() {
		try {
			try {
				if (preparedStatement.isClosed()) {
					return;
				}
			} catch (AbstractMethodError ignored) {
			}
			getPreparedStatement().close();
		} catch (SQLException e) {
			throw new QueryException(e);
		}
	}


	@Override
	public void reset() {
		preparedStatement = database.prepare(query);
	}

	@Override
	public void setAutoReset(boolean reset) {
		autoReset = reset;
	}

	@Override
	public void addBatch() {
		synchronized (database) {
			try {
				try {
					if (preparedStatement.isClosed()) {
						reset();
					}
				} catch (AbstractMethodError ignored) {
				}
				preparedStatement.addBatch();
			} catch (SQLException e) {
				if (autoReset) {
					reset();
				}
				throw new QueryException(e);
			}
		}
	}

	@Override
	public void clearBatch() {
		synchronized (database) {
			try {
				try {
					if (preparedStatement.isClosed()) {
						reset();
					}
				} catch (AbstractMethodError ignored) {
				}
				preparedStatement.clearBatch();
			} catch (SQLException e) {
				if (autoReset) {
					reset();
				}
				throw new QueryException(e);
			}
		}
	}

	@Override
	public void executeBatches() {
		synchronized (database) {
			try {
				try {
					if (preparedStatement.isClosed()) {
						reset();
					}
				} catch (AbstractMethodError ignored) {
				}
				preparedStatement.executeBatch();
			} catch (SQLException e) {
				if (autoReset) {
					reset();
				}
				throw new QueryException(e);
			}
		}
	}

	@Override
	public boolean isAutoReset() {
		return autoReset;
	}

	protected PreparedStatement getPreparedStatement() {
		return preparedStatement;
	}

	protected JBDCDatabase getDatabase() {
		return database;
	}
}
