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
 * Last modified: 26.12.12 20:26
 */

package com.p000ison.dev.sqlapi;

import com.p000ison.dev.sqlapi.exception.RegistrationException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * This holds a constructor. You can register any constructor for your {@link RegisteredTable}.
 * The default constructor has no parameters.
 */
public class RegisteredConstructor {
    private Constructor<? extends TableObject> constructor;
    private Object[] currentArgumentValues;

    public RegisteredConstructor(Constructor<? extends TableObject> constructor) {
        this.constructor = constructor;
    }

    public RegisteredConstructor(Class<? extends TableObject> clazz, Object... args) throws NoSuchMethodException {
        Class[] types = new Class[args.length];

        for (int i = 0; i < args.length; i++) {
            types[i] = args[i].getClass();
        }
        constructor = clazz.getConstructor(types);
        setArguments(args);
    }

    public RegisteredConstructor(Class<? extends TableObject> clazz, Class... args) throws NoSuchMethodException {
        constructor = clazz.getConstructor(args);
    }

    public void setArguments(Object... arguments) {
        Class<?>[] types = constructor.getParameterTypes();

        if (arguments.length != types.length) {
            throw new IllegalArgumentException("The length of the new arguments must match the size of the parameter types!");
        }

        currentArgumentValues = arguments;
    }

    @SuppressWarnings("unchecked")
    public <T extends TableObject> T newInstance() {
        try {
            if (currentArgumentValues == null) {
                return (T) constructor.newInstance();
            }
            return (T) constructor.newInstance(currentArgumentValues);
        } catch (InstantiationException e) {
            throw new RegistrationException(e);
        } catch (IllegalAccessException e) {
            throw new RegistrationException(e);
        } catch (InvocationTargetException e) {
            throw new RegistrationException(e);
        }
    }

    public void clear() {
        currentArgumentValues = null;
    }
}
