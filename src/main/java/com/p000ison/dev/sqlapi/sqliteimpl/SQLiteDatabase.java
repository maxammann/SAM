package com.p000ison.dev.sqlapi.sqliteimpl;

import com.p000ison.dev.sqlapi.Database;
import com.p000ison.dev.sqlapi.DatabaseConfiguration;
import com.p000ison.dev.sqlapi.TableBuilder;
import com.p000ison.dev.sqlapi.TableObject;
import org.sqlite.SQLiteDataSource;

import java.sql.SQLException;

/**
 * Represents a SQLiteDatabase
 */
public final class SQLiteDatabase extends Database {

    public SQLiteDatabase(SQLiteConfiguration configuration) throws SQLException
    {
        super(configuration);
    }

    @Override
    protected void init(DatabaseConfiguration configuration)
    {
        dataSource = new SQLiteDataSource();

        SQLiteDataSource sqliteSource = (SQLiteDataSource) dataSource;
        SQLiteConfiguration SQLiteConfiguration = (SQLiteConfiguration) configuration;

        sqliteSource.setUrl("jdbc:sqlite:" + SQLiteConfiguration.getLocation().getAbsolutePath());
    }

    @Override
    public void close() throws SQLException
    {
        getConnection().close();
    }

    @Override
    protected TableBuilder createTableBuilder(Class<? extends TableObject> table)
    {
        return new SQLiteTableBuilder(table, this);
    }

    @Override
    public SQLiteConfiguration getConfiguration()
    {
        return (SQLiteConfiguration) super.getConfiguration();
    }
}
