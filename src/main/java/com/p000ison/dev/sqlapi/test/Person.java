package com.p000ison.dev.sqlapi.test;

import com.p000ison.dev.sqlapi.TableObject;
import com.p000ison.dev.sqlapi.annotation.DatabaseColumn;
import com.p000ison.dev.sqlapi.annotation.DatabaseColumnGetter;
import com.p000ison.dev.sqlapi.annotation.DatabaseColumnSetter;
import com.p000ison.dev.sqlapi.annotation.DatabaseTable;

@DatabaseTable(name = "tablename")
public class Person implements TableObject {

    @DatabaseColumn(position = 2, databaseName = "name", primary = true, autoIncrement = true)
    private String name = "b";

    private String formattedName= "a";

    @DatabaseColumn(position = 1, databaseName = "id", unique = true)
    private int id;


    public Person()
    {
    }

    @DatabaseColumnSetter(position = 3, databaseName = "fname")
    public void setFormattedName(String formattedName)
    {
        this.formattedName = formattedName.replace(' ', '_');
    }

    @DatabaseColumnGetter(databaseName = "fname")
    public String getFormattedName()
    {
        return formattedName;
    }
}
