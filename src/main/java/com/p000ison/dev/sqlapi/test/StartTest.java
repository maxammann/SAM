package com.p000ison.dev.sqlapi.test;

import com.p000ison.dev.sqlapi.Database;
import com.p000ison.dev.sqlapi.DefaultSelectQuery;
import com.p000ison.dev.sqlapi.mysql.MySQLConfiguration;
import com.p000ison.dev.sqlapi.mysql.MySQLDatabase;
import com.p000ison.dev.sqlapi.query.SelectQuery;

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
//            Database db = new SQLiteDatabase(new SQLiteConfiguration(new File("/home/max/Arbeitsfläche/test.db")));
            db.setDropOldColumns(true);
            db.registerTable(person);
//            db.getConnection().prepareStatement("SELECT * FROM d").executeQuery();

            SelectQuery<Person> selectQuery = new DefaultSelectQuery<Person>(db);
//           db.save(person);
//            Column column = db.getRegisteredTable(Person.class).getColumn("id");
// selectQuery.from(person.getClass()).descending().orderBy(column).where().equals(column, 5).or().equals(column, 1).select();
//            System.out.println(selectQuery.from(Person.class).descending().getQuery());

//            SelectQuery<Person> selectQuery = new DefaultSelectQuery<Person>(db);
//            selectQuery.from(Person.class).descending();
//            int i = db.prepareStatement(selectQuery);
//
//
//            db.executeStatement(i, db.getRegisteredTable(Person.class));
//
//            SelectQuery<Person> selectQuery1 = new DefaultSelectQuery<Person>(db);
//            selectQuery1.from(Person.class).descending().list();


            db.close();


        } catch (Exception e) {
            e.printStackTrace();
        }

        long finish = System.currentTimeMillis();
        System.out.printf("Check took %s!", finish - start);
    }
}
