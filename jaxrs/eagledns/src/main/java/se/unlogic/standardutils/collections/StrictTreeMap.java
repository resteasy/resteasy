/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.collections;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class StrictTreeMap<Key, Value> implements StrictMap<Key, Value> {

	protected final TreeMap<Key, Value> treeMap;

	public StrictTreeMap (Comparator<Key> comparator){
		
		treeMap = new TreeMap<Key, Value>(comparator);
	}
	
	public Value put(Key key, Value value) throws KeyAlreadyCachedException {
		if (this.treeMap.containsKey(key)) {
			throw new KeyAlreadyCachedException(key);
		} else {
			return treeMap.put(key, value);
		}
	}

	public Value update(Key key, Value value) throws KeyNotCachedException {
		if (this.treeMap.containsKey(key)) {
			treeMap.remove(key);
			return treeMap.put(key, value);
		} else {
			throw new KeyNotCachedException(key);
		}
	}

	public Value remove(Object key) throws KeyNotCachedException {
		if (treeMap.containsKey(key)) {
			return treeMap.remove(key);
		} else {
			throw new KeyNotCachedException(key);
		}
	}

	public void clear() {
		treeMap.clear();
	}

	@Override
	public Object clone() {
		return treeMap.clone();
	}

	public boolean containsKey(Object key) {
		return treeMap.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return treeMap.containsValue(value);
	}

	public Set<Entry<Key, Value>> entrySet() {
		return treeMap.entrySet();
	}

	@Override
	public boolean equals(Object o) {
		return treeMap.equals(o);
	}

	public Value get(Object key) {
		return treeMap.get(key);
	}

	@Override
	public int hashCode() {
		return treeMap.hashCode();
	}

	public boolean isEmpty() {
		return treeMap.isEmpty();
	}

	public Set<Key> keySet() {
		return treeMap.keySet();
	}

	public int size() {
		return treeMap.size();
	}

	@Override
	public String toString() {
		return treeMap.toString();
	}

	public Collection<Value> values() {
		return treeMap.values();
	}

	public void putAll(Map<? extends Key, ? extends Value> map)  throws KeyAlreadyCachedException{

		for (Entry<? extends Key, ? extends Value> entry : map.entrySet()) {

			if (this.treeMap.containsKey(entry.getKey())) {
				throw new KeyAlreadyCachedException(entry.getKey());
			}
		}

		this.treeMap.putAll(map);
	}
}
