/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.datatypes;

import java.io.Serializable;
import java.util.Map.Entry;

public class SimpleEntry<KeyType, ValueType> implements Entry<KeyType,ValueType>, Serializable{

	private static final long serialVersionUID = 2017770345032632182L;

	public SimpleEntry(KeyType key, ValueType value) {
		super();
		this.key = key;
		this.value = value;
	}

	private final KeyType key;
	private ValueType value;

	public ValueType getValue() {
		return value;
	}
	public ValueType setValue(ValueType value) {

		ValueType oldValue = this.value;

		this.value = value;

		return oldValue;
	}
	public KeyType getKey() {
		return key;
	}
	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SimpleEntry<?,?> other = (SimpleEntry<?,?>) obj;
		if (key == null) {
			if (other.key != null) {
				return false;
			}
		} else if (!key.equals(other.key)) {
			return false;
		}
		if (value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!value.equals(other.value)) {
			return false;
		}
		return true;
	}
}
