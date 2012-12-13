package com.p000ison.dev.sqlapi;

/**
 * Every class which represents a table in a database needs to implements this interface.
 * To define a name of a table you need to use the class-annotation {@link com.p000ison.dev.sqlapi.annotation.DatabaseTable}.
 * To define columns you can use the annotation {@link com.p000ison.dev.sqlapi.annotation.DatabaseColumn} for fields or
 * the annotation {@link com.p000ison.dev.sqlapi.annotation.DatabaseColumnSetter} and {@link com.p000ison.dev.sqlapi.annotation.DatabaseColumnGetter}.
 * <p/>
 * <p>Annotations:</p>
 * <ul>
 * <li>{@link com.p000ison.dev.sqlapi.annotation.DatabaseTable}</li>
 * <li>{@link com.p000ison.dev.sqlapi.annotation.DatabaseColumn}</li>
 * <li>{@link com.p000ison.dev.sqlapi.annotation.DatabaseColumnSetter} or {@link com.p000ison.dev.sqlapi.annotation.DatabaseColumnGetter}</li>
 * </ul>
 *
 * </code>
 */
public interface TableObject {
}
