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
 * Last modified: 23.12.12 17:22
 */

package com.p000ison.dev.sqlapi.query;

import com.p000ison.dev.sqlapi.DatabaseColumn;

/**
 * This is used to prepare queries/statements. If you close this and call a method of this class the prepared statement gets reset.({@link #reset()})
 */
@SuppressWarnings("unused")
public interface PreparedQuery {

    void set(int index, Object value);

    void set(int index, Object value, int databaseType);

    void set(DatabaseColumn column, int index, Object value);

    /**
     * Clears the stored values
     */
    void clearParameters();

    /**
     * Updates the database and performs the query. This method should be synchronized with the {@link com.p000ison.dev.sqlapi.Database}.
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
