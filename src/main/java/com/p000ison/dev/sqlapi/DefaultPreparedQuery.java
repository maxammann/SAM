package com.p000ison.dev.sqlapi;


import com.mysql.jdbc.PreparedStatement;

/**
 * Represents a DefaultPreparedQuery
 */
public class DefaultPreparedQuery<T extends TableObject> implements PreparedQuery<T> {
    private PreparedStatement preparedStatement;
}
