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
 * Last modified: 26.12.12 23:53
 */

package com.p000ison.dev.sqlapi;

import com.p000ison.dev.sqlapi.annotation.DatabaseColumnGetter;
import com.p000ison.dev.sqlapi.annotation.DatabaseColumnSetter;
import com.p000ison.dev.sqlapi.exception.QueryException;
import com.p000ison.dev.sqlapi.exception.TableBuildingException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Represents a FieldColumn
 */
final class MethodColumn extends Column {

    private Method getter, setter;
    private DatabaseColumnSetter annotation;

    MethodColumn()
    {
    }

    public Method getGetter()
    {
        return getter;
    }

    void setGetter(Method getter)
    {
        if (this.getter != null) {
            throw new TableBuildingException("Duplicate column \"%s\"!", getName());
        }

        Class<?> type = getter.getReturnType();

        if (type.equals(void.class)) {
            throw new TableBuildingException("The return type of a getter method can not be \"void\": %s", getter.getName());
        }

        if (getter.getParameterTypes().length != 0) {
            throw new TableBuildingException("A getter method can not have any parameters!: %s", getter.getName());
        }
        this.getter = getter;
        this.getter.setAccessible(true);
    }

    public Method getSetter()
    {
        return setter;
    }

    void setSetter(Method setter)
    {
        if (this.setter != null) {
            throw new TableBuildingException("Duplicate column \"%s\"!", getName());
        }

        Class<?> type = setter.getReturnType();

        if (!type.equals(void.class)) {
            throw new TableBuildingException("The return type of a getter method must be \"void\": %s", setter.getName());
        }

        if (setter.getParameterTypes().length != 1) {
            throw new TableBuildingException("A getter method must have 1 parameter!: \"%s\"", setter.getName());
        }

        this.setter = setter;
        setter.setAccessible(true);
    }

    void setAnnotation(DatabaseColumnSetter annotation)
    {
        this.annotation = annotation;
    }

    public boolean isNull()
    {
        return getter == null || setter == null;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MethodColumn that = (MethodColumn) o;

        return (setter != null && setter.equals(that.setter)) || (getter != null && getter.equals(that.getter));
    }

    @Override
    public int hashCode()
    {
        int result = getter != null ? getter.hashCode() : 0;
        result = 31 * result + (setter != null ? setter.hashCode() : 0);
        return result;
    }

    @Override
    public Class<?> getType()
    {
        return getter.getReturnType();
    }

    @Override
    public String getName()
    {
        if (getter != null) {
            return getter.getAnnotation(DatabaseColumnGetter.class).databaseName();
        }
        if (setter != null) {
            return setter.getAnnotation(DatabaseColumnSetter.class).databaseName();
        }
        return null;
    }

    @Override
    public int getPosition()
    {
        return annotation.position();
    }

    public void validate()
    {
        if (!getType().equals(getSetter().getParameterTypes()[0])) {
            throw new TableBuildingException("The parameter of the setter method and the return type of the getter method do not equal: %s != %s", getSetter().getName(), getGetter().getName());
        }
    }

    @Override
    public String getDefaultValue()
    {
        return annotation.defaultValue();
    }

    @Override
    public int[] getLength()
    {
        return annotation.lenght();
    }

    @Override
    public boolean isAutoIncrementing()
    {
        return annotation.autoIncrement();
    }

    @Override
    public boolean isNotNull()
    {
        return annotation.notNull();
    }

    @Override
    public boolean isUnique()
    {
        return annotation.unique();
    }

    @Override
    public boolean isID()
    {
        return annotation.id();
    }

    @Override
    public boolean isSaveInputAfterLoading()
    {
        return annotation.saveValueAfterLoading();
    }

    @Override
    public void setValue(TableObject tableObject, Object object)
    {
        try {

            setter.invoke(tableObject, object);
        } catch (IllegalAccessException e) {
            throw new QueryException(e);
        } catch (InvocationTargetException e) {
            throw new QueryException(e.getCause());
        }   catch (IllegalArgumentException e) {
            System.out.println("object: " + object);
            System.out.println("class: " + object.getClass().getName());
        }
    }

    @Override
    public Object getValue(TableObject tableObject)
    {
        try {
            return getter.invoke(tableObject);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString()
    {
        return "MethodColumn{" +
                "column-name=" + getName() +
                ", getter=" + (getter == null ? "null" : getter.getName()) +
                ", setter=" + (setter == null ? "null" : setter.getName()) +
                '}';
    }

}
