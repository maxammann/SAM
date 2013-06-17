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
 * Last modified: 29.12.12 16:43
 */

package com.p000ison.dev.sqlapi;

import com.p000ison.dev.sqlapi.annotation.Column;
import com.p000ison.dev.sqlapi.exception.TableBuildingException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The main part to build tables.
 * <p/>
 * <p/>
 * <strong>Implementation notes:</strong>
 * <p/>
 * If you want to modify the table creation query override createTable(). To alter the table altering query override
 * buildModifyColumns(). The columns are prepared in setupColumns(); so do not override this. Once the columns are prepared
 * they are unmodifiable. Implement isSupportAddColumns(), isSupportRemoveColumns() and isSupportModifyColumns() to
 * specify what your database engine can handle. getBuilders() finally returns the queries.
 */

public abstract class TableBuilder {
    /**
     * The class which represents the table
     */
    private Class<? extends TableObject> object;
    /**
     * The expected columns
     */
    private List<DatabaseColumn> buildingColumns = new ArrayList<DatabaseColumn>();

    private Database database;

    private String tableName;

    private boolean existed;
    /**
     * The constructor we use to build new instances (should have no parameters)
     */
    private Constructor<? extends TableObject> ctor;

    private Set<DatabaseColumn> toAdd;
    private Set<String> toDrop;

    private final Set<StringBuilder> builders = new HashSet<StringBuilder>();

    public TableBuilder(Class<? extends TableObject> object, Database database) {
        this.object = object;
        this.database = database;
        tableName = Database.getTableName(object);

        if (tableName == null) {
            throw new TableBuildingException("The name of the table is not given! Add the @Table annotation!");
        }

        try {
            ctor = object.getDeclaredConstructor();
            ctor.setAccessible(true);
        } catch (NoSuchMethodException ignored) {
        }

        existed = database.existsDatabaseTable(tableName);

        setupColumns();
    }

    public TableBuilder(TableObject object, Database database) {
        this(object.getClass(), database);
    }

    public TableBuilder createTable() {
        if (existed) {
            return this;
        }

        StringBuilder query = new StringBuilder();
        query.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append('(');

        for (DatabaseColumn column : buildingColumns) {
            query.append(buildColumn(column));
            query.append(',');
        }

        query.deleteCharAt(query.length() - 1);

        query.append(");");

        builders.add(query);

        return this;
    }

    public TableBuilder createModifyQuery() {
        if (!existed) {
            return this;
        }

        if (isSupportAddColumns() || isSupportModifyColumns() || isSupportRemoveColumns()) {
            setupModifyColumns();
            buildModifyColumns();
        }

        return this;
    }

    private DatabaseColumn getColumn(String dbColumn) {
        for (DatabaseColumn column : buildingColumns) {
            if (column.getName().equals(dbColumn)) {
                return column;
            }
        }
        return null;
    }

    private boolean existsColumn(String dbColumn) {
        return getColumn(dbColumn) != null;
    }

    /**
     * Setups the columns of a table and produces a unmodifiable list
     */
    private void setupColumns() {
        buildingColumns.clear();

        //Find all FieldColumns and add them
        for (Field field : object.getDeclaredFields()) {
            Column column;
            if ((column = field.getAnnotation(Column.class)) != null) {
                if (existsColumn(column.databaseName())) {
                    throw new TableBuildingException("Duplicate column \"%s\" in class %s!", column.databaseName(), object.getName());
                }
                if (column.id() && !(field.getType() != long.class || field.getType() != Long.class || field.getType() != AtomicLong.class)) {
                    throw new TableBuildingException("Your id column must have the type long!");
                }
                DatabaseColumn fieldColumn = new FieldColumn(field, column);
                if (!database.isSupported(fieldColumn.getType())) {
                    throw new TableBuildingException("The type %s of the column %s is not supported by the database!", fieldColumn.getType().getName(), fieldColumn.getName());
                }
                buildingColumns.add(fieldColumn);
            }
        }

        //
        // Sort the columns by the given position, since getDeclaredFields and getDeclaredMethods do not have a specific order
        //
        Collections.sort(buildingColumns, new Comparator<DatabaseColumn>() {
            @Override
            public int compare(DatabaseColumn o1, DatabaseColumn o2) {
                int p1 = o1.getPosition();
                int p2 = o2.getPosition();
                return p1 < p2 ? -1 : p1 > p2 ? 1 : 0;
            }
        });

        buildingColumns = Collections.unmodifiableList(buildingColumns);
    }

    private void setupModifyColumns() {
        if (buildingColumns.isEmpty()) {
            throw new TableBuildingException("The table must have at least one column!");
        }

        List<String> databaseColumns = database.getDatabaseColumns(tableName);

        if (isSupportAddColumns()) {
            toAdd = new HashSet<DatabaseColumn>();
            for (DatabaseColumn column : buildingColumns) {
                if (!databaseColumns.contains(column.getName())) {
                    //missing in database
                    toAdd.add(column);
                }
            }
        }

        if (database.isDropOldColumns() && isSupportRemoveColumns()) {
            toDrop = new HashSet<String>();

            for (String column : databaseColumns) {
                if (!existsColumn(column)) {
                    toDrop.add(column);
                }
            }
        }
    }

    protected void buildModifyColumns() {
        StringBuilder query = new StringBuilder();
        boolean complete = false;

        if (toAdd != null && !toAdd.isEmpty()) {

            query.append("ALTER TABLE ").append(tableName).append(" ADD COLUMN (");
            for (DatabaseColumn column : toAdd) {
                query.append(buildColumn(column));
                query.append(',');
            }

            query.deleteCharAt(query.length() - 1);
            query.append(')');
            complete = true;
        }

        if (toDrop != null && !toDrop.isEmpty()) {

            if (toAdd.isEmpty()) {
                query.append("ALTER TABLE ").append(tableName);
            } else {
                query.append(',');
            }

            for (String column : toDrop) {
                query.append(" DROP COLUMN ").append(column);
                query.append(',');
            }

            query.deleteCharAt(query.length() - 1);
            complete = true;
        }

        if (complete) {
            query.append(';');
        }

        if (query.length() != 0) {
            addQuery(query);
        }
    }

    /**
     * Builds a column. it returns for example: "column INTEGER(5) NOT NULL UNIQUE KEY"
     *
     * @param column The DatabaseColumn object which holds all information about the column.
     */
    protected abstract StringBuilder buildColumn(DatabaseColumn column);

    protected abstract boolean isSupportAddColumns();

    protected abstract boolean isSupportRemoveColumns();

    protected abstract boolean isSupportModifyColumns();

    final Constructor<? extends TableObject> getDefaultConstructor() {
        return ctor;
    }


    final List<DatabaseColumn> getColumns() {
        return buildingColumns;
    }

    public final String getTableName() {
        return tableName;
    }

    protected Set<DatabaseColumn> getColumnsToAdd() {
        return toAdd;
    }

    protected Set<String> getColumnsToRemove() {
        return toDrop;
    }

    public Set<StringBuilder> getBuilders() {
        return builders;
    }

    protected void addQuery(StringBuilder builder) {
        builders.add(builder);
    }
}



