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
 * Last modified: 27.12.12 14:45
 */

package com.p000ison.dev.sqlapi;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Represents a OutputQueueConsumer
 * <p/>
 * This Runnable can be used to store TableObject. In most usages you want another thread handle queries. This thread
 * will wait if there is nothing to save.
 */
public class OutputQueueConsumer implements Runnable {
    private BlockingQueue<TableObject> queue = new LinkedBlockingQueue<TableObject>();
    private int maxSize = -1;
    private final Database database;

    public OutputQueueConsumer(int maxSize, Database database)
    {
        this.maxSize = maxSize;
        this.database = database;
    }

    public OutputQueueConsumer(Database database)
    {
        this(-1, database);
    }

    public synchronized void addTableObject(TableObject tableObject)
    {
        if (queue.size() >= maxSize) {
            return;
        }
        try {
            queue.put(tableObject);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void run()
    {
        TableObject obj;

        try {
            while ((obj = queue.take()) != null) {
                database.save(obj);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected Database getDatabase()
    {
        return database;
    }

    public int size()
    {
        return queue.size();
    }
}
