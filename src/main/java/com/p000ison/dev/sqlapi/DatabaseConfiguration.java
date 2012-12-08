package com.p000ison.dev.sqlapi;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a DatabaseConfiguration
 */
public abstract class DatabaseConfiguration {
    private Map<String, Object> properties;
    private final String driver;

    protected DatabaseConfiguration(String driver)
    {
        this.driver = driver;
        properties = new HashMap<String, Object>();
    }

    public final void setProperty(String key, Object property)
    {
        properties.put(key, property);
    }

    public final String getStringProperty(String key)
    {
        return properties.get(key).toString();
    }

    public final int getIntegerProperty(String key)
    {
        return (Integer) properties.get(key);
    }

    public final Object getProperty(String key)
    {
        return properties.get(key);
    }

    public final String getDriverName()
    {
        return driver;
    }

    public final Class getDriver() throws ClassNotFoundException
    {
        return Class.forName(driver);
    }
}
