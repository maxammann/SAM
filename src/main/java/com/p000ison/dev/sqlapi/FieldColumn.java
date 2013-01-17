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
 * Last modified: 26.12.12 20:19
 */

package com.p000ison.dev.sqlapi;

import com.p000ison.dev.sqlapi.annotation.DatabaseColumn;
import com.p000ison.dev.sqlapi.exception.QueryException;

import java.lang.reflect.Field;

/**
 * Implementation for fields of {@link Column}
 */
final class FieldColumn extends Column {

    private Field field;
    private DatabaseColumn annotation;

    FieldColumn(Field field, DatabaseColumn annotation) {
        this.field = field;
        field.setAccessible(true);
        this.annotation = annotation;
    }

    @Override
    public Class<?> getType() {
        return field.getType();
    }

    @Override
    public String getName() {
        return annotation.databaseName();
    }

    @Override
    public int getPosition() {
        return annotation.position();
    }

    @Override
    public String getDefaultValue() {
        return annotation.defaultValue();
    }

    @Override
    public int[] getLength() {
        return annotation.lenght();
    }

    @Override
    public boolean isAutoIncrementing() {
        return annotation.autoIncrement();
    }

    @Override
    public boolean isNotNull() {
        return annotation.notNull();
    }

    @Override
    public boolean isUnique() {
        return annotation.unique();
    }

    @Override
    public boolean isSaveInputAfterLoading() {
        return annotation.saveValueAfterLoading();
    }

    @Override
    public void setValue(TableObject tableObject, Object object) {
        try {
            field.set(tableObject, object);
        } catch (IllegalAccessException e) {
            throw new QueryException(e);
        }
    }

    @Override
    public Object getValue(TableObject tableObject) {
        try {
            return field.get(tableObject);
        } catch (IllegalAccessException e) {
            throw new QueryException(e);
        }
    }

    @Override
    public String toString() {
        return "FieldColumn{" +
                "column-name=" + getName() +
                ", field=" + (field == null ? null : field.getName()) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldColumn that = (FieldColumn) o;

        return !(field != null ? !field.equals(that.field) : that.field != null);
    }

    @Override
    public boolean isID() {
        return annotation.id();
    }

    @Override
    public int hashCode() {
        return field != null ? field.hashCode() : 0;
    }
}
