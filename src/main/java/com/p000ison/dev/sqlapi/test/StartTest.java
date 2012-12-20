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
 * Last modified: 18.12.12 17:40
 */

package com.p000ison.dev.sqlapi.test;

import com.p000ison.dev.sqlapi.jbdc.JBDCDatabase;
import com.p000ison.dev.sqlapi.jbdc.JBDCSelectQuery;
import com.p000ison.dev.sqlapi.mysql.MySQLConfiguration;
import com.p000ison.dev.sqlapi.mysql.MySQLDatabase;

import java.io.FileNotFoundException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a StartTest
 */
public class StartTest {
    private static final int PORT = 3306;

    public static void main(String[] args) throws FileNotFoundException
    {
        try {
            DriverManager.getConnection("jdbc:mysql://localhost/test?user=root&password=m1nt");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        long start = System.currentTimeMillis();
        try {

            Person person = new Person();

            JBDCDatabase db = new MySQLDatabase(new MySQLConfiguration("root", "m1nt", "localhost", PORT, "test"));
//            Database db = new SQLiteDatabase(new SQLiteConfiguration(new File("/home/max/Arbeitsfläche/test.db")));
            db.setDropOldColumns(true);
            db.registerTable(person);
//            db.getConnection().prepareStatement("SELECT * FROM d").executeQuery();

            Set<Person> result = new JBDCSelectQuery<Person>(db).prepare().getResults(new HashSet<Person>());
            System.out.println(result);

            db.close();


        } catch (Exception e) {
            e.printStackTrace();
        }

        long finish = System.currentTimeMillis();
        System.out.printf("Check took %s!", finish - start);
    }
}
