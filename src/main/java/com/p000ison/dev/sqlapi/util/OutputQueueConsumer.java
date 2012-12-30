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
 * Last modified: 29.12.12 15:35
 */

package com.p000ison.dev.sqlapi.util;

import com.p000ison.dev.sqlapi.Database;
import com.p000ison.dev.sqlapi.TableObject;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Represents a OutputQueueConsumer
 * <p/>
 * This Runnable can be used to store TableObject. In most usages you want another thread handle queries. This thread
 * will wait if there is nothing to save.
 */
public class OutputQueueConsumer extends Thread {
    private BlockingQueue<TableObject> queue = new LinkedBlockingQueue<TableObject>();
    private int maxSize = -1;
    private final Database database;
    private AtomicBoolean bool = new AtomicBoolean(true);

    public OutputQueueConsumer(int maxSize, Database database)
    {
        this.maxSize = maxSize;
        this.database = database;
    }

    public OutputQueueConsumer(Database database)
    {
        this(-1, database);
    }

    public void addTableObject(TableObject tableObject)
    {
        synchronized (this) {
            if (queue.size() >= maxSize) {
                return;
            }
            try {
                queue.put(tableObject);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void run()
    {
        while (bool.get()) {
            synchronized (this) {
                TableObject obj;

                try {
                    while ((obj = queue.take()) != null) {
                        database.save(obj);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void stopThread()
    {
        bool.set(false);
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
