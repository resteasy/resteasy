/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.collections;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StrictHashMap<Key, Value> implements StrictMap<Key, Value>{

	HashMap<Key, Value> hashMap = new HashMap<Key, Value>();

	public Value put(Key key, Value value) throws KeyAlreadyCachedException {
		if (this.hashMap.containsKey(key)) {
			throw new KeyAlreadyCachedException(key);
		} else {
			return hashMap.put(key, value);
		}
	}

	public Value update(Key key, Value value) throws KeyNotCachedException {
		if (this.hashMap.containsKey(key)) {
			hashMap.remove(key);
			return hashMap.put(key, value);
		} else {
			throw new KeyNotCachedException(key);
		}
	}

	public Value remove(Object key) throws KeyNotCachedException {
		if (hashMap.containsKey(key)) {
			return hashMap.remove(key);
		} else {
			throw new KeyNotCachedException(key);
		}
	}

	public void clear() {
		hashMap.clear();
	}

	@Override
	public Object clone() {
		return hashMap.clone();
	}

	public boolean containsKey(Object key) {
		return hashMap.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return hashMap.containsValue(value);
	}

	public Set<Entry<Key, Value>> entrySet() {
		return hashMap.entrySet();
	}

	@Override
	public boolean equals(Object o) {
		return hashMap.equals(o);
	}

	public Value get(Object key) {
		return hashMap.get(key);
	}

	@Override
	public int hashCode() {
		return hashMap.hashCode();
	}

	public boolean isEmpty() {
		return hashMap.isEmpty();
	}

	public Set<Key> keySet() {
		return hashMap.keySet();
	}

	public int size() {
		return hashMap.size();
	}

	@Override
	public String toString() {
		return hashMap.toString();
	}

	public Collection<Value> values() {
		return hashMap.values();
	}

	public void putAll(Map<? extends Key, ? extends Value> map) throws KeyAlreadyCachedException{

		for (Entry<? extends Key, ? extends Value> entry : map.entrySet()) {

			if (this.hashMap.containsKey(entry.getKey())) {
				throw new KeyAlreadyCachedException(entry.getKey());
			}
		}

		this.hashMap.putAll(map);
	}
}
