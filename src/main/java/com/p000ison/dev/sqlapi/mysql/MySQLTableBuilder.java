package com.p000ison.dev.sqlapi.mysql;

import com.p000ison.dev.sqlapi.*;
import com.p000ison.dev.sqlapi.exception.TableBuildingException;

/**
 * Represents a SQLiteTableBuilder
 */
public final class MySQLTableBuilder extends TableBuilder {

    public MySQLTableBuilder(TableObject object, Database database)
    {
        super(object, database);
    }

    public MySQLTableBuilder(Class<? extends TableObject> object, Database database)
    {
        super(object, database);
    }

    @Override
    protected void buildColumn(Column column)
    {
        Class<?> type = column.getType();
        query.append(column.getColumnName()).append(' ');

        boolean allowModifyLength = true;

        if (type == boolean.class || type == Boolean.class) {
            query.append("TINYINT(1)");
            allowModifyLength = false;
        } else if (type == byte.class || type == Byte.class) {
            query.append("TINYINT");
            allowModifyLength = false;
        } else if (type == short.class || type == Short.class) {
            query.append("SMALLINT");
            allowModifyLength = false;
        } else if (type == int.class || type == Integer.class) {
            query.append("TINYINT");
        } else if (type == float.class || type == Float.class) {
            query.append("FLOAT");
        } else if (type == double.class || type == Double.class) {
            query.append("DOUBLE");
        } else if (type == long.class || type == Long.class) {
            query.append("LONG");
        } else if (type == char.class || type == Character.class) {
            query.append("CHAR");
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

        if (column.isNotNull()) {
            query.append(" NOT NULL");
        }

        if (column.isUnique()) {
            query.append(" UNIQUE KEY");
        }

        if (column.isAutoIncrementing()) {
            query.append(" AUTO_INCREMENT");
        }

        if (column.isPrimary()) {
            if (primaryColumn) {
                throw new TableBuildingException("Duplicate primary/autoincrementing column %s!", column.getColumnName());
            }
            primaryColumn = true;
            query.append(" PRIMARY KEY");
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
        return true;
    }

    @Override
    protected boolean isSupportModifyColumns()
    {
        return true;
    }

    @Override
    protected String getTypeName(Class<?> type)
    {
        if (type == boolean.class || type == Boolean.class) {
            return "BOOLEAN";
        } else if (type == byte.class || type == Byte.class) {
            return null;
        } else if (type == short.class || type == Short.class) {
            return null;
        } else if (type == int.class || type == Integer.class) {
            return null;
        } else if (type == float.class || type == Float.class) {
            return null;
        } else if (type == double.class || type == Double.class) {
            return null;
        } else if (type == long.class || type == Long.class) {
            return null;
        } else if (type == char.class || type == Character.class) {
            return null;
        } else if (type == String.class) {
            return null;
        } else if (RegisteredTable.isSerializable(type)) {
            return null;
        }

        return null;
    }
}
