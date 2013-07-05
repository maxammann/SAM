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

package org.p000ison.dev.sam.mysql;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.p000ison.dev.sam.DatabaseColumn;
import org.p000ison.dev.sam.DatabaseConfiguration;
import org.p000ison.dev.sam.DatabaseConnectionException;
import org.p000ison.dev.sam.RegisteredTable;
import org.p000ison.dev.sam.jbdc.JBDCDatabase;
import org.p000ison.dev.sam.jbdc.JBDCQueryFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Represents a SQLiteDatabase
 */
public final class MySQLDatabase extends JBDCDatabase<MySQLConfiguration> {

	public MySQLDatabase(MySQLConfiguration configuration) throws DatabaseConnectionException {
		super(configuration, null);
		super.setQueryFactory(new JBDCQueryFactory(this) {
			@Override
			public StringBuilder buildColumn(DatabaseColumn column) {
				return buildDatabaseColumn(column);
			}
		});
	}

	@Override
	protected Connection connect(DatabaseConfiguration configuration) throws DatabaseConnectionException {
		MysqlDataSource dataSource = new MysqlDataSource();
		MySQLConfiguration mysqlConfiguration = (MySQLConfiguration) configuration;

		dataSource.setUser(mysqlConfiguration.getUser());
		dataSource.setPassword(mysqlConfiguration.getPassword());
		dataSource.setDatabaseName(mysqlConfiguration.getDatabase());
		dataSource.setServerName(mysqlConfiguration.getHost());
		dataSource.setPort(mysqlConfiguration.getPort());

		dataSource.setAutoClosePStmtStreams(false);

		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			throw new DatabaseConnectionException(configuration, e);
		}
	}

	@Override
	public boolean isAutoReset() {
		return false;
	}

	@Override
	public String getEngineName() {
		return "MySQL";
	}

	protected StringBuilder buildDatabaseColumn(DatabaseColumn column) {
		Class<?> type = column.getType();
		StringBuilder query = new StringBuilder();
		query.append(column.getName()).append(' ');

		if (column.isID()) {
			query.append("INTEGER");
		} else {
			boolean allowModifyLength = true;

			if (type == boolean.class || type == Boolean.class || type == AtomicBoolean.class) {
				query.append("TINYINT(1)");
				allowModifyLength = false;
			} else if (type == byte.class || type == Byte.class) {
				query.append("TINYINT");
				allowModifyLength = false;
			} else if (type == short.class || type == Short.class) {
				query.append("SMALLINT");
				allowModifyLength = false;
			} else if (type == int.class || type == Integer.class || type == AtomicInteger.class
					|| type == long.class || type == Long.class || type == AtomicLong.class) {
				query.append("INTEGER");
			} else if (type == float.class || type == Float.class) {
				query.append("FLOAT");
			} else if (type == double.class || type == Double.class) {
				query.append("DOUBLE");
			} else if (type == char.class || type == Character.class) {
				query.append("CHAR");
			} else if (type == Date.class || type == Timestamp.class) {
				query.append("DATETIME");
			} else if (type == String.class) {
				if (column.getLength().length != 0) {
					query.append("VARCHAR");
				} else {
					query.append("TEXT");
				}
			} else if (RegisteredTable.isSerializable(type)) {
				query.append("BLOB");
				allowModifyLength = false;
			}


			if (column.getLength().length != 0 && allowModifyLength) {
				query.append('(');
				for (int singleLength : column.getLength()) {
					query.append(singleLength).append(',');
				}

				query.deleteCharAt(query.length() - 1);
				query.append(')');
			}
		}

		if (column.isNotNull()) {
			query.append(" NOT NULL");
		}

		if (!column.getDefaultValue().isEmpty()) {
			query.append(" DEFAULT ").append(column.getDefaultValue());
		}

		return query;
	}
}
