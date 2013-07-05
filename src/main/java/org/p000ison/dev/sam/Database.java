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

package org.p000ison.dev.sam;

import org.p000ison.dev.sam.key.ForeignKey;
import org.p000ison.dev.sam.key.Key;
import org.p000ison.dev.sam.key.PrimaryKey;
import org.p000ison.dev.sam.key.UniqueKey;
import org.p000ison.dev.sam.query.PreparedQuery;
import org.p000ison.dev.sam.query.PreparedSelectQuery;
import org.p000ison.dev.sam.query.SelectQuery;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A Database holds the connection to a database. If you can to extend this API you will have to implement this
 * and modify it for you database engine.
 */
public abstract class Database<C extends DatabaseConfiguration> {

	public static final int UNSUPPORTED_TYPE = Integer.MAX_VALUE;
	private static final Lock accessLock = new ReentrantLock();
	private static Logger logger;
	/**
	 * A map of registered tables (classes) and a list of columns
	 */
	private final Set<RegisteredTable> registeredTables = new HashSet<RegisteredTable>();
	private final List<Key> registeredKeys = new ArrayList<Key>();
	/**
	 * The configuration object which holds all settings
	 */
	private C configuration;
	/**
	 * Whether old columns should be dropped
	 */
	private boolean dropOldColumns = false;
	private QueryFactory queryFactory;

	/**
	 * Creates a new database connection based on the configuration
	 *
	 * @param configuration The database configuration
	 */
	protected Database(C configuration) throws DatabaseConnectionException {
		this(configuration, null);
	}

	/**
	 * Creates a new database connection based on the configuration
	 *
	 * @param configuration The database configuration
	 */
	protected Database(C configuration, QueryFactory queryFactory) throws DatabaseConnectionException {
		this.configuration = configuration;
		this.queryFactory = queryFactory;
		String driver = configuration.getDriverName();

		try {
			Class.forName(driver);
		} catch (Exception ignored) {
			throw new DatabaseConnectionException(configuration, "Failed to load driver " + driver + "!");
		}

		registerKey(new ForeignKey());
		registerKey(new PrimaryKey());
		registerKey(new UniqueKey());
	}

	static void log(Level level, String msg, Object... args) {
		if (logger == null) {
			return;
		}

		logger.log(level, String.format(msg, args));
	}

	public static void setLogger(Logger logger) {
		Database.logger = logger;
	}

	/**
	 * Closes the connection to the database
	 *
	 * @throws QueryException
	 */
	public final void close() throws QueryException {
		for (RegisteredTable table : registeredTables) {
			table.close();
		}
		closeDatabaseConnection();
	}

	protected abstract void closeDatabaseConnection() throws QueryException;

	/**
	 * Checks whether this the connection to the database is still established
	 *
	 * @return Whether the the the connection is still established
	 */
	public abstract boolean isConnected();

	/**
	 * Checks whether the database exists already.
	 *
	 * @param table The table to check for
	 * @return Whether the table exists
	 */
	public abstract boolean existsDatabaseTable(String table);

	/**
	 * Gets a list of all columns in the database.
	 *
	 * @param table The table to look up
	 * @return A list of columns
	 */
	public abstract List<String> getDatabaseColumns(String table);

	public abstract boolean executeDirectUpdate(String query);

	public abstract boolean existsEntry(RegisteredTable table, Model object);

	public abstract boolean existsEntry(Model object);

	protected abstract long getLastID(RegisteredTable table);

	/**
	 * Checks whether the class is supported by this database/database engine
	 *
	 * @param type The type to check for
	 * @return Whether the type is supported
	 */
	public abstract boolean isSupported(Class<?> type);

	public abstract boolean testConnection();

	public abstract boolean isAutoReset();

	public boolean isAutoReconnect() {
		return getConfiguration() != null && getConfiguration().isAutoReconnect();
	}

	protected C getConfiguration() {
		return configuration;
	}

	public final boolean isDropOldColumns() {
		return dropOldColumns;
	}

	public final void setDropOldColumns(boolean dropOldColumns) {
		this.dropOldColumns = dropOldColumns;
	}

	public abstract String getEngineName();

	public abstract void sendKeepAliveQuery();

	/**
	 * Constructs a new SelectQuery for further use. This should be synchronized with the Database instance
	 *
	 * @param <T> a Model type
	 * @return The SelectQuery
	 */
	public <T extends Model> SelectQuery<T> select() {
		return new SelectQuery<T>(this);
	}

	//================================================================================
	// Insert, update, delete
	//================================================================================

	/**
	 * Saves a object to the table in your database. The class of the object must not be not registered!
	 * If the there is already an entry in the database with the id of the object or the id is equal or less than 0 the
	 * table gets updated else a new entry gets inserted.
	 *
	 * @param model The object to insert/update
	 * @throws RegistrationException If the table is not registered
	 */
	public void save(Model model) {
		RegisteredTable table = getRegisteredTable(model);
		DatabaseColumn idColumn = table.getIDColumn();

		if (((Number) idColumn.getValue(model)).intValue() <= 0 || !existsEntry(table, model)) {
			insert(table, model, idColumn);
		} else {
			update(table, model, idColumn);
		}
	}

	/**
	 * Deletes a object from a database
	 *
	 * @param model The object
	 */
	public void delete(Model model) {
		accessLock.lock();
		try {
			RegisteredTable table = getRegisteredTable(model);
			DatabaseColumn idColumn = table.getIDColumn();

			PreparedQuery statement = table.getPreparedDeleteStatement();
			statement.set(idColumn, 0, idColumn.getValue(model));
			statement.update();
		} finally {
			accessLock.unlock();
		}
	}

	/**
	 * Returns the RegisteredTable of a registered class
	 *
	 * @param obj The table object to look for
	 * @return The RegisteredTable
	 * @throws RegistrationException If the table is not registered
	 */
	private RegisteredTable getRegisteredTable(Model obj) {
		return getRegisteredTable(obj.getClass());
	}

	/**
	 * Attempts to update the object in the database
	 *
	 * @param model The object to update
	 * @throws RegistrationException If the table is not registered
	 */
	public void update(Model model) {
		RegisteredTable table = getRegisteredTable(model);
		DatabaseColumn idColumn = table.getIDColumn();

		update(table, model, idColumn);
	}

	/**
	 * Attempts to insert a entry in the database. Can throw an exception if for example there is a unique column!
	 *
	 * @param model The object to insert
	 * @throws QueryException if the query fails
	 */
	public void insert(Model model) {
		RegisteredTable table = getRegisteredTable(model);
		DatabaseColumn idColumn = table.getIDColumn();

		insert(table, model, idColumn);
	}

	private void insert(RegisteredTable registeredTable, Model object, DatabaseColumn idColumn) {
		accessLock.lock();
		try {
			PreparedQuery insert = registeredTable.getPreparedInsertStatement();
			setColumnValues(insert, registeredTable, object, idColumn);
			insert.update();
			idColumn.setValue(object, getLastID(registeredTable));
		} finally {
			accessLock.unlock();
		}
	}

	private void update(RegisteredTable registeredTable, Model object, DatabaseColumn idColumn) {
		accessLock.lock();
		try {
			PreparedQuery update = registeredTable.getPreparedUpdateStatement();
			int i = setColumnValues(update, registeredTable, object, idColumn);
			update.set(idColumn, i, idColumn.getValue(object));
			update.update();
		} finally {
			accessLock.unlock();
		}
	}

	public void addUpdateBatch(Model object) {
		RegisteredTable table = getRegisteredTable(object);
		PreparedQuery update = table.getPreparedUpdateStatement();
		accessLock.lock();
		try {
			DatabaseColumn id = table.getIDColumn();
			int i = setColumnValues(update, table, object, table.getIDColumn());
			update.set(id, i, id.getValue(object));
			update.addBatch();
		} finally {
			accessLock.unlock();
		}
	}

	public void addInsertBatch(Model object) {
		RegisteredTable table = getRegisteredTable(object);
		PreparedQuery update = table.getPreparedInsertStatement();
		accessLock.lock();
		try {
			setColumnValues(update, table, object, table.getIDColumn());
			update.addBatch();
		} finally {
			accessLock.unlock();
		}
	}

	public void addDeleteBatch(Model object) {
		RegisteredTable table = getRegisteredTable(object);
		PreparedQuery update = table.getPreparedDeleteStatement();
		accessLock.lock();
		try {
			update.set(0, table.getIDColumn().getValue(object));
			update.addBatch();
		} finally {
			accessLock.unlock();
		}
	}

	/**
	 * Runs the batch and executes the stored commands
	 *
	 * @param clazz   The class
	 * @param bitmask Defines whether to run the update, insert or delete statements. Example: 1 | 1 << 1 | 1 << 2 for all
	 */
	public void executeBatch(Class<? extends Model> clazz, int bitmask) {
		executeBatch(getRegisteredTable(clazz), bitmask);
	}

	/**
	 * Runs the batch and executes the stored commands
	 *
	 * @param table   The class
	 * @param bitmask Defines whether to run the update, insert or delete statements. Example: 1 | 1 << 1 | 1 << 2(0111) for all. The first bit defines updating, the second inserting and the last deleting
	 */
	public void executeBatch(RegisteredTable table, int bitmask) {
		if ((bitmask & 1) != 0) {
			executeUpdateBatch(table);
		}
		if ((bitmask & 1 << 1) != 0) {
			executeInsertBatch(table);
		}
		if ((bitmask & 1 << 2) != 0) {
			executeDeleteBatch(table);
		}
	}

	public void executeUpdateBatch(RegisteredTable table) {
		accessLock.lock();
		try {
			PreparedQuery update = table.getPreparedUpdateStatement();
			update.executeBatches();
		} finally {
			accessLock.unlock();
		}
	}

	public void executeUpdateBatch(Class<? extends Model> table) {
		executeUpdateBatch(getRegisteredTable(table));
	}

	public void executeInsertBatch(RegisteredTable table) {
		accessLock.lock();
		try {
			PreparedQuery insert = table.getPreparedInsertStatement();
			insert.executeBatches();
		} finally {
			accessLock.unlock();
		}
	}

	public void executeInsertBatch(Class<? extends Model> table) {
		executeInsertBatch(getRegisteredTable(table));
	}

	public void executeDeleteBatch(RegisteredTable table) {
		accessLock.lock();
		try {
			PreparedQuery delete = table.getPreparedDeleteStatement();
			delete.executeBatches();
		} finally {
			accessLock.unlock();
		}
	}

	public void executeDeleteBatch(Class<? extends Model> table) {
		executeDeleteBatch(getRegisteredTable(table));
	}

	private int setColumnValues(PreparedQuery statement, RegisteredTable registeredTable, Model object, DatabaseColumn idColumn) {
		List<DatabaseColumn> registeredColumns = registeredTable.getRegisteredColumns();
		int i = 0;
		for (DatabaseColumn column : registeredColumns) {
			if (column.equals(idColumn)) {
				continue;
			}

			Object value = column.getValue(object);
			statement.set(column, i, value);
			i++;
		}

		return i;
	}

	/**
	 * Copies all entries of a table from this database to another database.
	 * The is a 100% copy. If the a entry with the same id exists it gets updated.
	 *
	 * @param table The table
	 * @param to    The destination
	 * @param <T>   The type of the table
	 */
	public <T extends Model> void copy(Class<T> table, Database to) {
		RegisteredTable registeredTable = to.getRegisteredTable(table);
		PreparedSelectQuery<T> prepare = this.<T>select().from(table).prepare();
		PreparedQuery statement = this.getQueryFactory().createInsertStatement().into(registeredTable)
				.columns(registeredTable.getRegisteredColumns()).prepare();

		for (T entry : prepare.getResults()) {

			if (to.existsEntry(registeredTable, entry)) {
				to.update(entry);
			} else {
				int i = 0;
				for (DatabaseColumn column : registeredTable.getRegisteredColumns()) {
					statement.set(column, i, column.getValue(entry));
					i++;
				}
				statement.update();
			}
		}

		prepare.close();
		statement.update();
	}

	/**
	 * Registers a new Model for further use
	 *
	 * @param table The object to register
	 */
	public final RegisteredTable registerTable(Model table) {
		return registerTable(table.getClass());
	}

	//================================================================================
	// Registration
	//================================================================================

	/**
	 * Registers a class for further use
	 *
	 * @param table The class to register
	 */
	public final synchronized RegisteredTable registerTable(Class<? extends Model> table) {
		RegisteredTable registeredTable = new RegisteredTable(table);
		registeredTable.initTable(this, true, false);
		registeredTables.add(registeredTable);

//		for (StringBuilder query : builder.getBuilders()) {
//			if (query.length() == 0) {
//				continue;
//			}
//			log(Level.INFO, "Generating and updating table %s!", registeredTable.getName());
//			executeDirectUpdate(query.toString());
//		}
		return registeredTable;
	}

	/**
	 * Returns the RegisteredTable of a registered class
	 *
	 * @param table The table to look for
	 * @return The RegisteredTable
	 * @throws RegistrationException If the table is not registered
	 */
	public synchronized RegisteredTable getRegisteredTable(Class<? extends Model> table) {
		for (RegisteredTable registeredTable : registeredTables) {
			if (registeredTable.isRegisteredClass(table)) {
				return registeredTable;
			}
		}
		throw new RegistrationException(table, "The class %s is not registered!", table.getName());
	}

	public boolean isRegistered(Class<? extends Model> table) {
		try {
			getRegisteredTable(table);
			return true;
		} catch (RegistrationException ignored) {
			return false;
		}
	}

	public boolean isRegistered(RegisteredTable table) {
		return registeredTables.contains(table);
	}

	public void registerKey(Key key) {
		Class<? extends Key> keyClass = key.getClass();

		//if we find the key override it
		for (int i = 0; i < registeredKeys.size(); i++) {
			if (registeredKeys.get(i).getClass().isAssignableFrom(keyClass)) {
				registeredKeys.set(i, key);
				return;
			}
		}

		registeredKeys.add(key);
	}

	public Key getRegisteredKey(Class<? extends Key> lookUp) {
		for (Key key : registeredKeys) {
			if (lookUp.isAssignableFrom(key.getClass())) {
				return key;
			}
		}

		throw new IllegalStateException("Could not find the key " + lookUp
				.getCanonicalName() + "! Maybe you need to register it?");
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Database database = (Database) o;

		return !(configuration != null ? !configuration
				.equals(database.configuration) : database.configuration != null);
	}

	//================================================================================
	// Overridden methods
	//================================================================================

	@Override
	public int hashCode() {
		return configuration != null ? configuration.hashCode() : 0;
	}

	public QueryFactory getQueryFactory() {
		return queryFactory;
	}

	public void setQueryFactory(QueryFactory queryFactory) {
		this.queryFactory = queryFactory;
	}
}
