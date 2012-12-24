/*
 * This file is part of SQLDatabaseAPI (2012).
 *
 * SQLDatabaseAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SQLDatabaseAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SQLDatabaseAPI.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Last modified: 18.12.12 18:21
 */

package com.p000ison.dev.sqlapi;

import com.p000ison.dev.sqlapi.annotation.DatabaseColumn;
import com.p000ison.dev.sqlapi.annotation.DatabaseColumnGetter;
import com.p000ison.dev.sqlapi.annotation.DatabaseColumnSetter;
import com.p000ison.dev.sqlapi.exception.TableBuildingException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Represents a TableBuilder
 */
public abstract class TableBuilder {

    /**
     * A temporary query which is used to create a table or modify a table for example
     */
    private StringBuilder query = new StringBuilder();
    /**
     * The class which represents the table
     */
    private Class<? extends TableObject> object;
    /**
     * The expected columns
     */
    private List<Column> buildingColumns = new ArrayList<Column>();

    private Database database;

    private String tableName;

    private boolean existed;
    /**
     * The constructor we use to build new instances (should have no parameters)
     */
    private Constructor<? extends TableObject> ctor;

    private Set<Column> toAdd;
    private List<String> toDrop;

    public TableBuilder(Class<? extends TableObject> object, Database database)
    {
        this.object = object;
        this.database = database;
        tableName = Database.getTableName(object);

        if (tableName == null) {
            throw new TableBuildingException("The name of the table is not given! Add the @DatabaseTable annotation!");
        }

        if (!Database.validateTableName(tableName)) {
            throw new TableBuildingException("The name of the table %s is not valid!", tableName);
        }

        try {
            ctor = object.getDeclaredConstructor();
            ctor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new TableBuildingException("No default constructor found in class %s!", object.getName());
        }

        existed = database.existsDatabaseTable(tableName);

        setupColumns();
    }

    public TableBuilder(TableObject object, Database database)
    {
        this(object.getClass(), database);
    }

    public TableBuilder createTable()
    {
        clearQuery();
        if (existed) {
            return this;
        }
        query.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append('(');

        buildColumns();

        query.append(");");

        return this;
    }

    public TableBuilder createModifyQuery()
    {
        clearQuery();
        if (!existed) {
            return this;
        }

        if (isSupportAddColumns() || isSupportModifyColumns() || isSupportRemoveColumns()) {
            setupModifyColumns();
            buildModifyColumns();
        }

        return this;
    }

    private void clearQuery()
    {
        query.setLength(0);
    }

    private Column getColumn(String dbColumn)
    {
        for (Column column : buildingColumns) {
            if (column.getName().equals(dbColumn)) {
                return column;
            }
        }
        return null;
    }

    private boolean existsColumn(String dbColumn)
    {
        return getColumn(dbColumn) != null;
    }

    private MethodColumn getMethodColumn(String dbColumn)
    {
        for (Column column : buildingColumns) {
            if ((column instanceof MethodColumn) && column.getName().equals(dbColumn)) {
                return (MethodColumn) column;
            }
        }
        return null;
    }

    /**
     * Setups the columns of a table and produces a unmodifiable list
     */
    private void setupColumns()
    {
        buildingColumns.clear();

        Method[] methods = object.getDeclaredMethods();

        //
        // Math getters and setters together and validate the methods
        //
        for (Method method : methods) {
            String columnName;

            DatabaseColumnSetter setter = method.getAnnotation(DatabaseColumnSetter.class);
            if (setter != null) {
                columnName = setter.databaseName();
            } else {
                DatabaseColumnGetter getter = method.getAnnotation(DatabaseColumnGetter.class);
                if (getter == null) {
                    continue;
                }

                columnName = getter.databaseName();
            }

            if (!Database.validateColumnName(columnName)) {
                throw new TableBuildingException("The name of the column %s is not valid!", columnName);
            }

            MethodColumn column = getMethodColumn(columnName);
            if (column == null) {
                column = new MethodColumn();
                buildingColumns.add(column);
            }

            if (setter == null) {
                column.setGetter(method);
                if (!database.isSupported(column.getType())) {
                    throw new TableBuildingException("The type %s of the column %s is not supported by the database!");
                }
            } else {
                column.setSetter(method);
                column.setAnnotation(setter);
            }
        }

        //Check if all MethodColumns are correct
        for (Iterator<Column> it = buildingColumns.iterator(); it.hasNext(); ) {
            Column column = it.next();
            if (column instanceof MethodColumn) {
                MethodColumn methodColumn = (MethodColumn) column;

                if (methodColumn.isNull()) {
                    it.remove();
                } else {
                    methodColumn.validate();
                }
            }
        }

        //Find all FieldColumns and add them
        for (Field field : object.getDeclaredFields()) {
            DatabaseColumn column;
            if ((column = field.getAnnotation(DatabaseColumn.class)) != null) {
                if (existsColumn(column.databaseName())) {
                    throw new TableBuildingException("Duplicate column \"%s\"!", column.databaseName());
                }
                Column fieldColumn = new FieldColumn(field, column);
                if (!database.isSupported(fieldColumn.getType())) {
                    throw new TableBuildingException("The type %s of the column %s is not supported by the database!");
                }
                buildingColumns.add(fieldColumn);
            }
        }

        //
        // Sort the columns by the given position, since getDeclaredFields and getDeclaredMethods do not have a specific order
        //
        Collections.sort(buildingColumns, new Comparator<Column>() {
            @Override
            public int compare(Column o1, Column o2)
            {
                int p1 = o1.getPosition();
                int p2 = o2.getPosition();
                return p1 < p2 ? -1 : p1 > p2 ? 1 : 0;
            }
        });

        buildingColumns = Collections.unmodifiableList(buildingColumns);
    }

    private void buildColumns()
    {
        if (buildingColumns.isEmpty()) {
            throw new TableBuildingException("The table must have at least one column!");
        }

        for (Column column : buildingColumns) {
            buildColumn(column);
            query.append(',');
        }

        query.deleteCharAt(query.length() - 1);
    }

    private void setupModifyColumns()
    {
        if (buildingColumns.isEmpty()) {
            throw new TableBuildingException("The table must have at least one column!");
        }

        List<String> databaseColumns = database.getDatabaseColumns(tableName);

        if (isSupportAddColumns()) {
            toAdd = new HashSet<Column>();
            for (Column column : buildingColumns) {
                if (!databaseColumns.contains(column.getName())) {
                    //missing in database
                    toAdd.add(column);
                }
            }
        }

        if (database.isDropOldColumns() && isSupportRemoveColumns()) {
            toDrop = new ArrayList<String>();

            for (String column : databaseColumns) {
                if (!existsColumn(column)) {
                    toDrop.add(column);
                }
            }
        }
    }

    protected void buildModifyColumns()
    {
        boolean complete = false;
        if (toAdd != null && !toAdd.isEmpty()) {

            query.append("ALTER TABLE ").append(Database.getTableName(object)).append(" ADD COLUMN (");

            for (Column column : toAdd) {
                buildColumn(column);
                query.append(',');
            }

            query.deleteCharAt(query.length() - 1);
            query.append(')');
        }

        if (toDrop != null && !toDrop.isEmpty()) {

            if (toAdd.isEmpty()) {
                query.append("ALTER TABLE ").append(Database.getTableName(object));
            } else {
                query.append(',');
            }

            complete = true;

            for (String column : toDrop) {
                query.append(" DROP COLUMN ").append(column);
                query.append(',');
            }

            query.deleteCharAt(query.length() - 1);
        }

        if (complete) {
            query.append(';');
        }
    }

    /**
     * Builds a column. it returns for example: "column INTEGER(5) NOT NULL UNIQUE KEY"
     *
     * @param column The Column object which holds all information about the column.
     */
    protected abstract void buildColumn(Column column);

    protected abstract boolean isSupportAddColumns();

    protected abstract boolean isSupportRemoveColumns();

    protected abstract boolean isSupportModifyColumns();

    final Constructor<? extends TableObject> getDefaultConstructor()
    {
        return ctor;
    }

    public final String getQuery()
    {
        if (query.length() == 0) {
            return null;
        }

        return query.toString();
    }

    final List<Column> getColumns()
    {
        return buildingColumns;
    }

    final String getTableName()
    {
        return tableName;
    }

    protected final StringBuilder getBuilder()
    {
        return query;
    }
}



