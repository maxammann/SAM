package com.p000ison.dev.sqlapi.impl;

import com.p000ison.dev.sqlapi.Column;
import com.p000ison.dev.sqlapi.Database;
import com.p000ison.dev.sqlapi.TableBuilder;
import com.p000ison.dev.sqlapi.TableObject;

/**
 * Represents a MySQLTableBuilder
 */
public class MySQLTableBuilder extends TableBuilder {

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

        if (type.equals(int.class)) {
            query.append("INT");
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

        if (column.isNotNull()) {
            query.append(' ').append("NOT NULL");
        }

        if (column.isAutoIncrementing()) {
            query.append(' ').append("AUTO_INCREMENT");
        }

        if (column.isPrimary()) {
            query.append(' ').append("PRIMARY KEY");
        }

        if (!column.getDefaultValue().isEmpty()) {
            query.append(' ').append("DEFAULT").append(' ').append(column.getDefaultValue());
        }
    }
}
