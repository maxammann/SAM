package com.p000ison.dev.sqlapi;

import com.p000ison.dev.sqlapi.annotation.DatabaseColumnGetter;
import com.p000ison.dev.sqlapi.annotation.DatabaseColumnSetter;
import com.p000ison.dev.sqlapi.exception.TableBuildingException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Represents a FieldColumn
 */
final class MethodColumn implements Column {

    private Method getter, setter;
    private DatabaseColumnSetter annotation;

    MethodColumn()
    {
    }

    MethodColumn(Method setter, DatabaseColumnSetter annotation)
    {
        this.setter = setter;
        this.annotation = annotation;
    }

    MethodColumn(Method setter)
    {
        this.setter = setter;
        this.annotation = setter.getAnnotation(DatabaseColumnSetter.class);
        if (annotation == null) {
            throw new TableBuildingException("The field %s is missing the DatabaseColumn annotation! Maybe this method is no column?");
        }
    }

    static void validateGetterMethod(Method method)
    {
        Class<?> type = method.getReturnType();
        if (type.equals(void.class)) {
            throw new TableBuildingException("The return type of a getter method can not be \"void\": %s", method.getName());
        }

        if (method.getParameterTypes().length != 0) {
            throw new TableBuildingException("A getter method can not have any parameters!: %s", method.getName());
        }
    }

    static void validateSetterMethod(Method method)
    {
        Class<?> type = method.getReturnType();

        if (!type.equals(void.class)) {
            throw new TableBuildingException("The return type of a getter method must be \"void\": %s", method.getName());
        }

        if (method.getParameterTypes().length != 1) {
            throw new TableBuildingException("A getter method must have 1 parameter!: \"%s\"", method.getName());
        }
    }

    public Method getGetter()
    {
        return getter;
    }

    void setGetter(Method getter)
    {
        if (this.getter != null) {
            throw new TableBuildingException("Duplicate column \"%s\"!", getColumnName());
        }
        this.getter = getter;
        getter.setAccessible(true);
    }

    public Method getSetter()
    {
        return setter;
    }

    void setSetter(Method setter)
    {
        if (this.setter != null) {
            throw new TableBuildingException("Duplicate column \"%s\"!", getColumnName());
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

        if (getter != null ? !getter.equals(that.getter) : that.getter != null) return false;
        if (setter != null ? !setter.equals(that.setter) : that.setter != null) return false;

        return true;
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
    public String getColumnName()
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

    void validate()
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
    public boolean isPrimary()
    {
        return annotation.primary();
    }

    @Override
    public void setValue(TableObject tableObject, Object object)
    {
        try {
            setter.invoke(tableObject, object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
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
                "getter=" + getter +
                ", setter=" + setter +
                '}';
    }
}
