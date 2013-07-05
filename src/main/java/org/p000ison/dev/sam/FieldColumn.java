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
import org.p000ison.dev.sam.exception.QueryException;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implementation for fields of {@link DatabaseColumn}
 */
final class FieldColumn extends DatabaseColumn {

	private Field field;
	private Column annotation;

	FieldColumn(Field field, Column annotation) {
		this.field = field;
		field.setAccessible(true);
		this.annotation = annotation;
	}

	@Override
	public Class<?> getType() {
		return field.getType();
	}

	@Override
	public String getName() {
		return annotation.databaseName();
	}

	@Override
	public int getPosition() {
		return annotation.position();
	}

	@Override
	public String getDefaultValue() {
		return annotation.defaultValue();
	}

	@Override
	public int[] getLength() {
		return annotation.length();
	}

	@Override
	public boolean isAutoIncrementing() {
		return annotation.autoIncrement() || annotation.id();
	}

	@Override
	public boolean isNotNull() {
		return annotation.notNull();
	}

	@Override
	public void setValue(TableObject tableObject, Object object) {
		try {
			Class type = getType();
			if (type == AtomicBoolean.class) {
				if (!(object instanceof Boolean)) {
					throw new QueryException("The selected boolean was not a Boolean and I was unable to create a AtomicBoolean!");
				}
				AtomicBoolean atomicBoolean = (AtomicBoolean) getValue(tableObject);
				if (atomicBoolean == null) {
					field.set(tableObject, new AtomicBoolean((Boolean) object));
				} else {
					atomicBoolean.set((Boolean) object);
				}
			} else if (type == AtomicInteger.class) {
				if (!(object instanceof Integer)) {
					throw new QueryException("The selected integer was not a Integer and I was unable to create a AtomicInteger!");
				}
				AtomicInteger atomicInteger = (AtomicInteger) getValue(tableObject);
				if (atomicInteger == null) {
					field.set(tableObject, new AtomicInteger((Integer) object));
				} else {
					atomicInteger.set((Integer) object);
				}
			} else if (type == AtomicLong.class) {
				if (!(object instanceof Long)) {
					throw new QueryException("The selected long was not a Long and I was unable to create a AtomicLong!");
				}
				AtomicLong atomicLong = ((AtomicLong) getValue(tableObject));
				if (atomicLong == null) {
					field.set(tableObject, new AtomicLong((Long) object));
				} else {
					atomicLong.set((Long) object);
				}
			} else {
				field.set(tableObject, object);
			}

		} catch (IllegalAccessException e) {
			throw new QueryException(e);
		}
	}

	@Override
	public Object getValue(TableObject tableObject) {
		try {
			return field.get(tableObject);
		} catch (IllegalAccessException e) {
			throw new QueryException(e);
		}
	}

	@Override
	public Index[] getIndices() {
		return annotation.indices();
	}

	@Override
	public String toString() {
		return "FieldColumn{" +
				"column-name=" + getName() +
				", field=" + (field == null ? null : field.getName()) +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		FieldColumn that = (FieldColumn) o;

		return !(field != null ? !field.equals(that.field) : that.field != null);
	}

	@Override
	public boolean isID() {
		return annotation.id();
	}

	@Override
	public int hashCode() {
		return field != null ? field.hashCode() : 0;
	}
}
