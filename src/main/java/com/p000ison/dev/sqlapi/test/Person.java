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
 * Last modified: 24.12.12 11:47
 */

package com.p000ison.dev.sqlapi.test;

import com.p000ison.dev.sqlapi.TableObject;
import com.p000ison.dev.sqlapi.annotation.DatabaseColumn;
import com.p000ison.dev.sqlapi.annotation.DatabaseColumnGetter;
import com.p000ison.dev.sqlapi.annotation.DatabaseColumnSetter;
import com.p000ison.dev.sqlapi.annotation.DatabaseTable;

@DatabaseTable(name = "testdf")
public class Person implements TableObject {

    @DatabaseColumn(position = 1, databaseName = "id", id = true)
    private int id = 0;

    private String name;

    @DatabaseColumn(position = 3, databaseName = "age")
    private int age;

    @DatabaseColumn(position = 3, databaseName = "hage")
    private int ageg;

    public Person() {
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }

    @DatabaseColumnSetter(position = 2, databaseName = "name", lenght = 100)
    public void setFormattedName(String name) {
        if (name != null) {
            this.name = name.toUpperCase();
        }
    }

    @DatabaseColumnGetter(databaseName = "name")
    public String getFormattedName() {
        return name;
    }
}