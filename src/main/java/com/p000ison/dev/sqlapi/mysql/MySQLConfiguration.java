package com.p000ison.dev.sqlapi.mysql;

import com.p000ison.dev.sqlapi.DatabaseConfiguration;

/**
 * Represents a SQLiteConfiguration
 */
public final class MySQLConfiguration extends DatabaseConfiguration {

    public MySQLConfiguration(String user, String password, String host, int port, String database)
    {
        super("com.mysql.jdbc.Driver");
        setUser(user);
        setPassword(password);
        setPort(port);
        setHost(host);
        setDatabase(database);
    }

    public String getUser()
    {
        return super.getStringProperty("user");
    }

    public String getDatabase()
    {
        return super.getStringProperty("db");
    }

    public String getPassword()
    {
        return super.getStringProperty("pw");
    }

    public int getPort()
    {
        return super.getIntegerProperty("port");
    }

    public String getHost()
    {
        return super.getStringProperty("host");
    }

    public MySQLConfiguration setUser(String user)
    {
        super.setProperty("user", user);
        return this;
    }

    public MySQLConfiguration setPassword(String pw)
    {
        super.setProperty("pw", pw);
        return this;
    }

    public MySQLConfiguration setDatabase(String db)
    {
        super.setProperty("db", db);
        return this;
    }

    public MySQLConfiguration setPort(int port)
    {
        super.setProperty("port", port);
        return this;
    }

    public MySQLConfiguration setHost(String host)
    {
        super.setProperty("host", host);
        return this;
    }
}
