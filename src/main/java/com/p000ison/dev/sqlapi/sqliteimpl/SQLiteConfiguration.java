package com.p000ison.dev.sqlapi.sqliteimpl;

import com.p000ison.dev.sqlapi.DatabaseConfiguration;

import java.io.File;

/**
 * Represents a SQLiteConfiguration
 */
public final class SQLiteConfiguration extends DatabaseConfiguration {

    public SQLiteConfiguration(File location)
    {
        super("org.sqlite.JDBC");
        setLocation(location);
    }

    public File getLocation()
    {
        return (File) super.getProperty("location");
    }


    public SQLiteConfiguration setLocation(File location)
    {
        super.setProperty("location", location);
        return this;
    }
}
