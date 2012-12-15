package com.p000ison.dev.sqlapi.sqlite;

import com.p000ison.dev.sqlapi.*;
import com.p000ison.dev.sqlapi.exception.TableBuildingException;

/**
 * Represents a SQLiteTableBuilder
 */
public final class SQLiteTableBuilder extends TableBuilder {

    public SQLiteTableBuilder(TableObject object, Database database)
    {
        super(object, database);
    }

    public SQLiteTableBuilder(Class<? extends TableObject> object, Database database)
    {
        super(object, database);
    }


    @Override
    protected void buildColumn(Column column)
    {
        Class<?> type = column.getType();
        query.append(column.getColumnName()).append(' ');

        if (type.equals(int.class)) {
            query.append("INTEGER");
        } else if (type.equals(String.class)) {
            query.append("TEXT");
        }

        if (column.getLength().length != 0) {
            query.append('(');
            for (int singleLength : column.getLength()) {
                query.append(singleLength).append(',');
            }

            query.deleteCharAt(query.length() - 1);
            query.append(')');
        }

        if (column.isPrimary() || column.isAutoIncrementing()) {
            if (primaryColumn) {
                throw new TableBuildingException("Duplicate primary/autoincrementing column %s!", column.getColumnName());
            }
            primaryColumn = true;
            query.append(" PRIMARY KEY");
        }

        if (column.isUnique()) {
            query.append(" UNIQUE");
        }

        if (column.isNotNull()) {
            query.append(" NOT NULL");
        }

        if (!column.getDefaultValue().isEmpty()) {
            query.append(" DEFAULT ").append(column.getDefaultValue());
        }
    }

    @Override
    protected boolean isSupportAddColumns()
    {
        return true;
    }

    @Override
    protected boolean isSupportRemoveColumns()
    {
        return false;
    }

    @Override
    protected boolean isSupportModifyColumns()
    {
        return false;
    }

    @Override
    public int getDatabaseDataType(Class<?> type)
    {
        return 0;
    }

//    @Override
//    protected String getTypeName(Class<?> type)
//    {
//        if (type == boolean.class || type == Boolean.class) {
//            return null;
//        } else if (type == byte.class || type == Byte.class) {
//            return null;
//        } else if (type == short.class || type == Short.class) {
//            return null;
//        } else if (type == int.class || type == Integer.class) {
//            return null;
//        } else if (type == float.class || type == Float.class) {
//            return null;
//        } else if (type == double.class || type == Double.class) {
//            return null;
//        } else if (type == long.class || type == Long.class) {
//            return null;
//        } else if (type == char.class || type == Character.class) {
//            return null;
//        } else if (type == String.class) {
//            return null;
//        } else if (RegisteredTable.isSerializable(type)) {
//            return null;
//        }
//
//        return null;
//    }
}
