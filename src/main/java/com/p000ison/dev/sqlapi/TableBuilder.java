package com.p000ison.dev.sqlapi;

import com.p000ison.dev.sqlapi.annotation.DatabaseColumn;
import com.p000ison.dev.sqlapi.annotation.DatabaseColumnGetter;
import com.p000ison.dev.sqlapi.annotation.DatabaseColumnSetter;
import com.p000ison.dev.sqlapi.exception.TableBuildingException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Represents a TableBuilder
 */
public abstract class TableBuilder {

    protected StringBuilder query;
    private Class<? extends TableObject> object;
    private List<Column> buildingColumns;
    /**
     * Whether we have already found a primary key
     */
    protected boolean primaryColumn = false;

    public TableBuilder(Class<? extends TableObject> object, Database database)
    {
        query = new StringBuilder();
        buildingColumns = new ArrayList<Column>();
        this.object = object;

        init();
    }

    public TableBuilder(TableObject object, Database database)
    {
        this(object.getClass(), database);
    }

    private static boolean containsColumn(Map<DatabaseColumn, Class<?>> map, String column)
    {
        for (DatabaseColumn cColumn : map.keySet()) {
            if (cColumn.databaseName().hashCode() == column.hashCode() && cColumn.databaseName().endsWith(column)) {
                return true;
            }
        }
        return false;
    }

    private void init()
    {
        setupBuildingColumns();
    }

    public TableBuilder createTable()
    {
        String tableName = Database.getTableName(object);

        if (tableName == null) {
            throw new TableBuildingException("The name of the table is not given! Add the @DatabaseTable annotation!");
        }

        query.append("CREATE TABLE IF NOT EXISTS ").append(Database.getTableName(object)).append('(');

        for (Column column : buildingColumns) {
            System.out.println(column.getColumnName());
        }

        buildColumns();

        query.append(");");

        return this;
    }

    public TableBuilder createModifyQuery()
    {
        String tableName = Database.getTableName(object);

        if (tableName == null) {
            throw new TableBuildingException("The name of the table is not given! Add the @DatabaseTable annotation!");
        }


//        query.append("ALTER TABLE ").append(Database.getTableName(object));

        buildModifyColumns();

        return this;
    }

    public Column getColumn(String dbColumn)
    {
        for (Column column : buildingColumns) {
            if (column.getColumnName().equals(dbColumn)) {
                return column;
            }
        }
        return null;
    }

    public boolean existsColumn(String dbColumn)
    {
        return getColumn(dbColumn) != null;
    }

    public MethodColumn getMethodColumn(String dbColumn)
    {
        for (Column column : buildingColumns) {
            if ((column instanceof MethodColumn) && column.getColumnName().equals(dbColumn)) {
                return (MethodColumn) column;
            }
        }
        return null;
    }

    private void setupBuildingColumns()
    {
        buildingColumns.clear();

        Method[] methods = object.getDeclaredMethods();

        for (Method method : methods) {
            String columnName;

            DatabaseColumnSetter setter = method.getAnnotation(DatabaseColumnSetter.class);
            if (setter != null) {
                MethodColumn.validateSetterethod(method);
                columnName = setter.databaseName();
            } else {
                DatabaseColumnGetter getter = method.getAnnotation(DatabaseColumnGetter.class);
                if (getter == null) {
                    continue;
                }

                MethodColumn.validateGetterMethod(method);

                columnName = getter.databaseName();
            }

            MethodColumn column = getMethodColumn(columnName);
            if (column == null) {
                column = new MethodColumn();
                buildingColumns.add(column);
            }

            if (setter == null) {
                column.setGetter(method);
            } else {
                column.setSetter(method);
                column.setAnnotation(setter);
            }
        }

        for (Iterator<Column> it = buildingColumns.iterator(); it.hasNext(); ) {
            Column column = it.next();
            if (column instanceof MethodColumn) {
                MethodColumn methodColumn = (MethodColumn) column;

                if (methodColumn.isNull()) {
                    System.out.println(column.getColumnName());
                    it.remove();
                } else {
                    methodColumn.validate();
                }
            }
        }

        for (Field field : object.getDeclaredFields()) {
            DatabaseColumn column;
            if ((column = field.getAnnotation(DatabaseColumn.class)) != null) {
                if (existsColumn(column.databaseName())) {
                    throw new TableBuildingException("Duplicate column \"%s\"!", column.databaseName());
                }
                FieldColumn fieldColumn = new FieldColumn(field, column);
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

    private void buildModifyColumns()
    {
//        if (buildingColumns.isEmpty()) {
//
//            throw new TableBuildingException("The table must have at least one column!");
//        }
//
//        Set<String> databaseColumns;
//
//        try {
//            databaseColumns = database.getDatabaseColumns(object);
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return;
//        }
//
//        Set<Map.Entry<DatabaseColumn, Class<?>>> toAdd = new HashSet<Map.Entry<DatabaseColumn, Class<?>>>();
//
//
//        for (Map.Entry<DatabaseColumn, Class<?>> entry : buildingColumns.entrySet()) {
//            if (!databaseColumns.contains(entry.getKey().databaseName())) {
//                //missing in database
//                toAdd.add(entry);
//            }
//        }
//
//        if (!toAdd.isEmpty()) {
//            query.append("ALTER TABLE ").append(Database.getTableName(object)).append(" ADD (");
//
//            for (Map.Entry<DatabaseColumn, Class<?>> entry : toAdd) {
//                buildColumn(entry.getKey(), entry.getValue());
//                query.append(',');
//            }
//
//            query.deleteCharAt(query.length() - 1);
//            query.append(')');
//        }
//
//
//        if (database.isDropOldColumns()) {
//            List<String> toDrop = new ArrayList<String>();
//
//            for (String column : databaseColumns) {
//                if (!containsColumn(buildingColumns, column)) {
//                    toDrop.add(column);
//                }
//            }
//
//            System.out.println(toDrop);
//
//            if (!toDrop.isEmpty()) {
//                if (toAdd.isEmpty()) {
//                    query.append("ALTER TABLE ").append(Database.getTableName(object)).append(" DROP COLUMN ");
//                } else {
//                    query.append(", ");
//                }
//
//                int end = toDrop.size() - 1;
//                for (int i = 0; i < toDrop.size(); i++) {
//
//                    query.append(toDrop.get(i));
//                    if (i != end) {
//                        query.append(",DROP COLUMN ");
//                    }
//                }
//            }
//        }
//
//        query.append(';');
    }

    public String getQuery()
    {
        if (query.length() == 0) {
            return null;
        }

        return query.toString();
    }

    List<Column> getColumns()
    {
        return Collections.unmodifiableList(buildingColumns);
    }

    protected abstract void buildColumn(Column column);

    protected abstract boolean isSupportAddColumns();

    protected abstract boolean isSupportRemoveColumns();

    protected abstract boolean isSupportModifyColumns();
}



