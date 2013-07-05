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

/**
 * Thrown when something fails during the registration
 */
public class RegistrationException extends RuntimeException {

	private Class<?> clazz;

	public RegistrationException(Class<?> clazz, String message, Object... args) {
		super(args.length == 0 ? message : String.format(message, args));
		this.clazz = clazz;
	}

	public RegistrationException(Class<?> clazz, Exception cause) {
		super(cause);
		this.clazz = clazz;
	}

	public RegistrationException(Class<?> clazz) {
		this.clazz = clazz;
	}

	public RegistrationException(Throwable cause, String message, Object... args) {
		super(args.length == 0 ? message : String.format(message, args), cause);
	}

	public RegistrationException(String message, Class<?> clazz) {
		super(message);
		this.clazz = clazz;
	}

	public RegistrationException(Throwable cause) {
		super(cause);
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public Class<?> getRegistrationErrorClass() {
		return clazz;
	}
}
