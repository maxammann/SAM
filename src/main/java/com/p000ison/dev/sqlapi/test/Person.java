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
 * Last modified: 18.12.12 17:29
 */

package com.p000ison.dev.sqlapi.test;

import com.p000ison.dev.sqlapi.TableObject;
import com.p000ison.dev.sqlapi.annotation.DatabaseColumn;
import com.p000ison.dev.sqlapi.annotation.DatabaseColumnGetter;
import com.p000ison.dev.sqlapi.annotation.DatabaseColumnSetter;
import com.p000ison.dev.sqlapi.annotation.DatabaseTable;

@DatabaseTable(name = "tablenamea")
public class Person implements TableObject {

    private String formattedName = "p";

    @DatabaseColumn(position = 1, databaseName = "id", id = true)
    private int id;

    @DatabaseColumn(position = 2, databaseName = "name")
    public String name = "z";


    public Person()
    {
    }

    @DatabaseColumnSetter(position = 3, databaseName = "fname")
    public void setFormattedName(String formattedName)
    {
        this.formattedName = formattedName;
    }

    @DatabaseColumnGetter(databaseName = "fname")
    public String getFormattedName()
    {
        return formattedName;
    }
}
