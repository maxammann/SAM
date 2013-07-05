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

import org.p000ison.dev.sam.annotation.Column;
import org.p000ison.dev.sam.annotation.Table;
import org.p000ison.dev.sam.exception.QueryException;
import org.p000ison.dev.sam.query.DeleteStatement;
import org.p000ison.dev.sam.query.InsertStatement;
import org.p000ison.dev.sam.query.PreparedQuery;
import org.p000ison.dev.sam.query.UpdateStatement;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Used to register TableObjects. Holds the columns, the registered constructor, some prepared statements and of the class
 * or the TableObject.
 */
public class RegisteredTable {
	private String name;
	private Class<? extends TableObject> registeredClass;
	private List<DatabaseColumn> registeredColumns = new ArrayList<DatabaseColumn>();
	private RegisteredConstructor constructor;
	private PreparedQuery updateStatement, insertStatement, deleteStatement;

	RegisteredTable(Class<? extends TableObject> registeredClass) {
		this.registeredClass = registeredClass;
	}

	public static boolean isSerializable(Class<?> clazz) {
		for (Class interfacee : clazz.getInterfaces()) {
			if (interfacee == Serializable.class) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Gets the name of a table
	 *
	 * @param clazz The class of the {@link TableObject}.
	 * @return The name
	 */
	static String getTableName(Class<? extends TableObject> clazz) {
		Table annotation = clazz.getAnnotation(Table.class);
		return annotation == null ? null : annotation.name();
	}

	public boolean isRegistered(TableObject obj) {
		return isRegisteredClass(obj.getClass());
	}

	/**
	 * Gets a registered column from this table
	 *
	 * @param columnName The name
	 * @return The registered column
	 */
	public DatabaseColumn getColumn(String columnName) {
		for (DatabaseColumn column : registeredColumns) {
			String name = column.getName();
			if (name.hashCode() == columnName.hashCode() && name.equals(columnName)) {
				return column;
			}
		}
		return null;
	}

	public boolean existsColumn(String columnName) {
		return getColumn(columnName) != null;
	}

	public DatabaseColumn getIDColumn() {
		for (DatabaseColumn column : registeredColumns) {
			if (column.isID()) {
				return column;
			}
		}
		return null;
	}

	public List<DatabaseColumn> getRegisteredColumns() {
		return registeredColumns;
	}

	public boolean isRegisteredClass(Class<? extends TableObject> registeredClass) {
		return this.registeredClass.equals(registeredClass);
	}

	public <T extends TableObject> T createNewInstance() {
		if (constructor == null) {
			throw new QueryException("No default constructor and no constructor registered for class %s!", registeredClass
					.getName());
		}

		return constructor.newInstance();
	}

	public String getName() {
		return name;
	}

	/**
	 * Registers a constructor which will be used to build the objects, just pass for example: "test", 5 in it to
	 * find a constructor with the parameters String and int.
	 *
	 * @param arguments Will be used to build the object, pass in nothing to use the default constructor
	 * @return A registered constructor
	 */
	public RegisteredConstructor registerConstructor(Object... arguments) {
		try {
			if (arguments.length == 0) {
				constructor = new RegisteredConstructor(registeredClass.getConstructor());
			} else {
				constructor = new RegisteredConstructor(registeredClass, arguments);
			}
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(String.format("Constructor for class %s not found!", getName()), e);
		}

		return constructor;
	}

	/**
	 * Registers a constructor which will be used to build the objects, just pass for example: String and int in it to
	 * find a constructor with the parameters String and int.
	 *
	 * @param arguments Will be used to build the object, pass in nothing to use the default constructor
	 * @return A registered constructor
	 */
	public RegisteredConstructor registerConstructor(Class... arguments) {
		try {
			if (arguments.length == 0) {
				constructor = new RegisteredConstructor(registeredClass.getConstructor());
			} else {
				constructor = new RegisteredConstructor(registeredClass, arguments);
			}
		} catch (NoSuchMethodException e) {
			throw new RegistrationException(e, "Failed at registering the constructor! Constructor not found!");
		}

		return constructor;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		RegisteredTable that = (RegisteredTable) o;

		return !(name != null ? !name.equals(that.name) : that.name != null);
	}

	@Override
	public int hashCode() {
		return name != null ? name.hashCode() : 0;
	}

	public void close() {
		updateStatement.close();
		insertStatement.close();
		deleteStatement.close();
	}

	public void initTable(Database<?> database, boolean supportAdd, boolean supportDrop) {

		this.name = getTableName(registeredClass);

		if (this.name == null) {
			throw new RegistrationException(registeredClass, "The name of the table is not given! Add the @Table annotation!");
		}

		try {
			Constructor<? extends TableObject> ctor = registeredClass.getDeclaredConstructor();
			ctor.setAccessible(true);
			this.constructor = new RegisteredConstructor(ctor);
		} catch (NoSuchMethodException ignored) {
		}

		boolean existed = database.existsDatabaseTable(getName());

		setupColumns(database);

		if (!existed) {
			database.getQueryFactory().createTableCreateStatement().table(getName()).columns(registeredColumns)
					.update();
		} else {
			if (supportAdd || supportDrop) {
				if (registeredColumns.isEmpty()) {
					throw new RegistrationException(registeredClass, "The table must have at least one column!");
				}

				List<String> databaseColumns = database.getDatabaseColumns(getName());

				if (supportAdd) {
					List<DatabaseColumn> columnsToAdd = getColumnsToAdd(databaseColumns);
					if (columnsToAdd != null && !columnsToAdd.isEmpty()) {
						database.getQueryFactory().createAddColumnStatement().in(registeredClass).columns(columnsToAdd)
								.update();
					}
				}

				if (database.isDropOldColumns() && supportDrop) {
					List<String> columnsToDrop = getColumnsToDrop(databaseColumns);
					if (columnsToDrop != null && !columnsToDrop.isEmpty()) {
						database.getQueryFactory().createDropColumnStatement().in(registeredClass)
								.columns(columnsToDrop)
								.update();
					}
				}
			}
		}


		prepareDeleteStatement(database);
		prepareInsertStatement(database);
		prepareUpdateStatement(database);
	}

	/**
	 * Setups the columns of a table and produces a unmodifiable list
	 */
	private void setupColumns(Database<?> database) {
		registeredColumns.clear();

		//Find all FieldColumns and add them
		for (Field field : registeredClass.getDeclaredFields()) {
			Column column;
			if ((column = field.getAnnotation(Column.class)) != null) {
				if (existsColumn(column.databaseName())) {
					throw new RegistrationException(registeredClass, "Duplicate column \"%s\" in class %s!", column
							.databaseName(), registeredClass.getName());
				}
				if (column.id() && !(field.getType() != long.class || field.getType() != Long.class || field
						.getType() != AtomicLong.class)) {
					throw new RegistrationException(registeredClass, "Your id column must have the type long!");
				}
				DatabaseColumn fieldColumn = new FieldColumn(field, column);
				if (!database.isSupported(fieldColumn.getType())) {
					throw new RegistrationException(registeredClass, "The type %s of the column %s is not supported by the database!", fieldColumn
							.getType().getName(), fieldColumn.getName());
				}
				registeredColumns.add(fieldColumn);
			}
		}

		//
		// Sort the columns by the given position, since getDeclaredFields and getDeclaredMethods do not have a specific order
		//
		Collections.sort(registeredColumns, new Comparator<DatabaseColumn>() {
			@Override
			public int compare(DatabaseColumn o1, DatabaseColumn o2) {
				int p1 = o1.getPosition();
				int p2 = o2.getPosition();
				return p1 < p2 ? -1 : p1 > p2 ? 1 : 0;
			}
		});

		registeredColumns = Collections.unmodifiableList(registeredColumns);
	}

	private List<DatabaseColumn> getColumnsToAdd(List<String> databaseColumns) {
		List<DatabaseColumn> toAdd = new LinkedList<DatabaseColumn>();
		for (DatabaseColumn column : registeredColumns) {
			if (!databaseColumns.contains(column.getName())) {
				//missing in database
				toAdd.add(column);
			}
		}

		return toAdd;
	}

	private List<String> getColumnsToDrop(List<String> databaseColumns) {

		List<String> toDrop = new LinkedList<String>();

		for (String column : databaseColumns) {
			if (!existsColumn(column)) {
				toDrop.add(column);
			}
		}

		return toDrop;
	}

	private void prepareUpdateStatement(Database database) {
		UpdateStatement update = database.getQueryFactory().createUpdateStatement().in(this);

		DatabaseColumn id = null;
		for (DatabaseColumn column : getRegisteredColumns()) {
			if (column.isID()) {
				id = column;
				continue;
			}
			update.column(column);
		}

		if (id == null) {
			throw new RegistrationException(registeredClass, "The table %s does not have an id!", getName());
		}

		update.where().preparedEquals(id);
		updateStatement = update.prepare();
	}

	private void prepareDeleteStatement(Database database) {
		DeleteStatement delete = database.getQueryFactory().createDeleteStatement().from(this).where()
				.preparedEquals(getIDColumn()).select();
		deleteStatement = delete.prepare();
	}

	private void prepareInsertStatement(Database database) {
		InsertStatement insert = database.getQueryFactory().createInsertStatement().into(this);

		DatabaseColumn id = null;
		for (DatabaseColumn column : getRegisteredColumns()) {
			if (column.isID()) {
				id = column;
				continue;
			}
			insert.column(column);
		}

		if (id == null) {
			throw new RegistrationException(registeredClass, "The table %s does not have an id!", getName());
		}

		insertStatement = insert.prepare();
	}

	public PreparedQuery getPreparedInsertStatement() {
		return insertStatement;
	}

	public PreparedQuery getPreparedUpdateStatement() {
		return updateStatement;
	}

	public PreparedQuery getPreparedDeleteStatement() {
		return deleteStatement;
	}
}
