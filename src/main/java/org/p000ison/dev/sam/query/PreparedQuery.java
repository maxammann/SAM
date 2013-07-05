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

package org.p000ison.dev.sam.query;

import org.p000ison.dev.sam.DatabaseColumn;

/**
 * This is used to prepare queries/statements. If you close this and call a method of this class the prepared statement gets reset.({@link #reset()})
 */
@SuppressWarnings("unused")
public interface PreparedQuery {

	void set(int index, Object value);

	void set(int index, Object value, int databaseType);

	void set(DatabaseColumn column, int index, Object value);

	/**
	 * Clears the stored value
	 */
	void clearParameters();

	/**
	 * Updates the database and performs the query. This method should be synchronized with the {@link org.p000ison.dev.sam.Database}.
	 *
	 * @return Whether the update was successfully
	 */
	boolean update();

	/**
	 * Closes this prepared statement and releases resources
	 */
	void close();

	/**
	 * Resets or reopens the statement. If {@link #isAutoReset()} is true this gets automatically called
	 * when a exception occurs.
	 */
	void reset();

	/**
	 * Automatically calls {@link #reset()} if a exception occurs. Default is the database engine's default.
	 *
	 * @param reset Whether we want to reset it automatically
	 */
	void setAutoReset(boolean reset);

	void addBatch();

	void clearBatch();

	void executeBatches();

	/**
	 * @return Whether we want to reset it automatically
	 */
	boolean isAutoReset();
}
