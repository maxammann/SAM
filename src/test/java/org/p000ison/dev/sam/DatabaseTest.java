/*
 * This file is part of SAM (2012).
 *
 * SAM is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SAM is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SAM.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Last modified: 05.07.13 12:21
 */

package org.p000ison.dev.sam;

import org.p000ison.dev.sam.annotation.Column;
import org.p000ison.dev.sam.annotation.Index;
import org.p000ison.dev.sam.annotation.Table;
import org.p000ison.dev.sam.key.ForeignKey;
import org.p000ison.dev.sam.sqlite.SQLiteConfiguration;
import org.p000ison.dev.sam.sqlite.SQLiteDatabase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;

/**
 * Represents a DatabaseTest
 */
@RunWith(JUnit4.class)
public class DatabaseTest {

	private final Database<?> database;
	private final File location;

	public DatabaseTest() {
		location = new File("./test.db");
		database = new SQLiteDatabase(new SQLiteConfiguration(location));
	}

	@Before
	public void testRegister() {
		long start = System.currentTimeMillis();
		database.registerTable(Entry.class);
		long finish = System.currentTimeMillis();
		System.out.printf("Check took %s!%n", finish - start);
		database.insert(new Entry("Test1", 1));
	}

	@Test
	public void testSelect() {
		long start = System.currentTimeMillis();
		database.<Entry>select().from(Entry.class).where().equals("amount", 1).select().prepare().getResults();
		long finish = System.currentTimeMillis();
		System.out.printf("Check took %s!%n", finish - start);
	}

	@After
	public void cleanUp() {
		location.delete();
	}

	@Table(name = "test")
	private static class Entry implements Model {
		@Column(databaseName = "id", id = true)
		private long id;
		@Column(databaseName = "name", length = 5)
		private String name;
		@Column(databaseName = "amount", indices = {@Index(type = ForeignKey.class, value = {"amount", "test", "id"})})
		private int amount;

		private Entry() {
		}

		private Entry(String name, int amount) {
			this.name = name;
			this.amount = amount;
		}

		@Override
		public String toString() {
			return "Entry{" +
					"id=" + id +
					", name='" + name + '\'' +
					", amount=" + amount +
					'}';
		}
	}
}
