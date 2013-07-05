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

package org.p000ison.dev.sam.util;

import org.p000ison.dev.sam.Database;
import org.p000ison.dev.sam.Model;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Represents a OutputQueueConsumer
 * <p/>
 * This Runnable can be used to store Model. In most usages you want another thread handle queries. This thread
 * will wait if there is nothing to save.
 */
public class OutputQueueConsumer extends Thread {
	private BlockingQueue<Model> queue = new LinkedBlockingQueue<Model>();
	private int maxSize = -1;
	private final Database database;
	private AtomicBoolean bool = new AtomicBoolean(true);

	public OutputQueueConsumer(int maxSize, Database database) {
		this.maxSize = maxSize;
		this.database = database;
	}

	public OutputQueueConsumer(Database database) {
		this(-1, database);
	}

	public void addTableObject(Model model) {
		synchronized (this) {
			if (queue.size() >= maxSize) {
				return;
			}
			try {
				queue.put(model);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void run() {
		while (bool.get()) {
			synchronized (this) {
				Model obj;

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

	public void stopThread() {
		bool.set(false);
	}

	protected Database getDatabase() {
		return database;
	}

	public int size() {
		return queue.size();
	}
}
