package com.p000ison.dev.sqlapi.test;

import com.p000ison.dev.sqlapi.Database;
import com.p000ison.dev.sqlapi.sqlite.SQLiteConfiguration;
import com.p000ison.dev.sqlapi.sqlite.SQLiteDatabase;

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
            db.save(person);
            db.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


        long finish = System.currentTimeMillis();
        System.out.printf("Check took %s!", finish - start);


        String röm = "IIX";

        int[] numbers = new int[röm.length()];

        for (int i = 0; i < röm.length(); i++) {
            char character = röm.charAt(i);
            int diff;

            switch (character) {
                case 'I':
                    diff = 1;
                    break;
                case 'V':
                    diff = 5;
                    break;
                case 'X':
                    diff = 10;
                    break;
                case 'L':
                    diff = 50;
                    break;
                case 'C':
                    diff = 100;
                    break;
                case 'M':
                    diff = 1000;
                    break;
                case 'ↁ':
                    diff = 5000;
                    break;
                case 'ↂ':
                    diff = 10000;
                    break;
                default:
                    throw new IllegalArgumentException("Failed to parse character " + character + "!");
            }

            numbers[i] = diff;
        }

        int value = 0;

        for (int i = 0; i < numbers.length; i++) {
            if (i == 0) {
                value += numbers[i];
            } else if (numbers[i - 1] >= numbers[i]) {
                value += numbers[i];
            } else {
                value += numbers[i] - 2 * numbers[i - 1];
            }
        }

        System.out.println(value);
    }
}
