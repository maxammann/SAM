package com.p000ison.dev.sqlapi.impl;

import com.p000ison.dev.sqlapi.DatabaseConfiguration;

/**
 * Represents a MySQLConfiguration
 */
public class MySQLConfiguration extends DatabaseConfiguration {

    public MySQLConfiguration(String user, String password, String host, int port, String database)
    {
        super("com.mysql.jdbc.Driver");
        setUser(user);
        setPassword(password);
        setPort(port);
        setHost(host);
        setDatabase(database);
    }

    public final String getUser()
    {
        return super.getStringProperty("user");
    }

    public final String getDatabase()
    {
        return super.getStringProperty("db");
    }

    public final String getPassword()
    {
        return super.getStringProperty("pw");
    }

    public final int getPort()
    {
        return super.getIntegerProperty("port");
    }

    public final String getHost()
    {
        return super.getStringProperty("host");
    }

    public final MySQLConfiguration setUser(String user)
    {
        super.setProperty("user", user);
        return this;
    }

    public final MySQLConfiguration setPassword(String pw)
    {
        super.setProperty("pw", pw);
        return this;
    }

    public final MySQLConfiguration setDatabase(String db)
    {
        super.setProperty("db", db);
        return this;
    }

    public final MySQLConfiguration setPort(int port)
    {
        super.setProperty("port", port);
        return this;
    }

    public final MySQLConfiguration setHost(String host)
    {
        super.setProperty("host", host);
        return this;
    }
}
