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
 * Last modified: 25.12.12 17:44
 */

package com.p000ison.dev.sqlapi.exception;

/**
 * Represents a RegistrationException
 */
public class RegistrationException extends RuntimeException {

    private Class<?> clazz;

    public RegistrationException(Class<?> clazz, String message, Object... args)
    {
        super(args.length == 0 ? message : String.format(message, args));
        this.clazz = clazz;
    }

    public RegistrationException(Class<?> clazz, Exception cause)
    {
        super(cause);
        this.clazz = clazz;
    }

    public RegistrationException(Class<?> clazz)
    {
        this.clazz = clazz;
    }

    public Class<?> getRegistrationErrorClass()
    {
        return clazz;
    }
}
