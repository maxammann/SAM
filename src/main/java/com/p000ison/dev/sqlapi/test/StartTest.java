package com.p000ison.dev.sqlapi.test;

import com.p000ison.dev.sqlapi.Database;
import com.p000ison.dev.sqlapi.DefaultSelectQuery;
import com.p000ison.dev.sqlapi.query.SelectQuery;
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


//            db.save(person);
//            Column column = db.getRegisteredTable(Person.class).getColumn("id");
// selectQuery.from(person.getClass()).descending().orderBy(column).where().equals(column, 5).or().equals(column, 1).select();
//            System.out.println(selectQuery.from(Person.class).descending().getQuery());
//            System.out.println(selectQuery.from(Person.class).descending().list().get(0).getFormattedName());


            SelectQuery<Person> selectQuery = new DefaultSelectQuery<Person>(db);
            selectQuery.from(Person.class).descending();
            int i = db.prepareStatement(selectQuery);


            long start1 = System.currentTimeMillis();
            for (int j = 0; j < 1000; j++) {
                db.executeStatement(i, db.getRegisteredTable(Person.class));
            }
            long finish1 = System.currentTimeMillis();
            System.out.printf("Check 1 took %s!", finish1 - start1);

            long start2 = System.currentTimeMillis();
            for (int j = 0; j < 1000; j++) {
                SelectQuery<Person> selectQuery1 = new DefaultSelectQuery<Person>(db);
                selectQuery1.from(Person.class).descending().list();
            }
            long finish2 = System.currentTimeMillis();
            System.out.printf("Check 2 took %s!", finish2 - start2);

            db.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


        long finish = System.currentTimeMillis();
        System.out.printf("Check took %s!", finish - start);
    }
}
