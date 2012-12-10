package com.p000ison.dev.sqlapi.test;

import com.p000ison.dev.sqlapi.Database;
import com.p000ison.dev.sqlapi.sqliteimpl.SQLiteConfiguration;
import com.p000ison.dev.sqlapi.sqliteimpl.SQLiteDatabase;

import java.io.File;

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

//            Database db = new MySQLDatabase(new MySQLConfiguration("root", "m1nt", "localhost", PORT, "test"));
            Database db = new SQLiteDatabase(new SQLiteConfiguration(new File("/home/max/Arbeitsfläche/test.db")));
            db.setDropOldColumns(true);
            db.registerTable(person);
//            db.getConnection().prepareStatement("SELECT * FROM d").executeQuery();

//            SelectQuery selectQuery = new TestQuery();
//
//            Column column = db.getColumn(person.getClass(), "id");
//
//            selectQuery.from(person.getClass()).descending().orderBy(column).where().equals(column, 5).or().equals(column, 1).select();


            db.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


        long finish = System.currentTimeMillis();
        System.out.printf("Check took %s!", finish - start);


    }
}
