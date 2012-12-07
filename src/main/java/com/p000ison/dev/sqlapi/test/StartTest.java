package com.p000ison.dev.sqlapi.test;

import com.p000ison.dev.sqlapi.Database;
import com.p000ison.dev.sqlapi.impl.MySQLConfiguration;
import com.p000ison.dev.sqlapi.impl.MySQLDatabase;

import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents a StartTest
 */
public class StartTest {
    private static final int PORT = 3306;

    public static void main(String[] args)
    {
        long start = System.currentTimeMillis();
        try {
            Person person = new Person();

            Database db = new MySQLDatabase(new MySQLConfiguration("root", "m1nt", "localhost", PORT, "test"));
            db.setDropOldColumns(true);
            db.registerTable(person);
            db.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


        long finish = System.currentTimeMillis();
        System.out.printf("Check took %s!", finish - start);


    }

    public static void printResultSet(PrintStream output, ResultSet set)
    {
        try {
            for (int i = 1; set.next(); i++) {
                output.println(set.getObject(i));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
