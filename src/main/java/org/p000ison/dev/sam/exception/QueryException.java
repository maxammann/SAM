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

package org.p000ison.dev.sam.exception;

import java.sql.SQLException;

/**
 * Represents a QueryException
 */
public class QueryException extends RuntimeException {

	public QueryException(String message, Object... args) {
		super(args.length == 0 ? message : String.format(message, args));
	}

	public QueryException(SQLException cause) {
		super(cause);
	}

	public QueryException(Throwable cause) {
		super(cause);
	}
}
