/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.threads;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

public class MutexKeyProvider<T> {

	private final WeakHashMap<MutexKey<T>, WeakReference<MutexKey<T>>> keyMap = new WeakHashMap<MutexKey<T>, WeakReference<MutexKey<T>>>();

	public MutexKey<T> getKey(T object) {

		MutexKey<T> newKey = new MutexKey<T>(object);

		synchronized (keyMap) {

			WeakReference<MutexKey<T>> reference = keyMap.get(newKey);

			if (reference == null) {
				return this.addKey(newKey);
			}

			MutexKey<T> currentKey = reference.get();

			if (currentKey == null) {
				return this.addKey(newKey);
			}

			// System.out.println("Returning cached instance of key " + currentKey.getValue() + "[" + this.keyMap.size() + "]");

			return currentKey;
		}
	}

	private MutexKey<T> addKey(MutexKey<T> key) {

		// System.out.println("\n[mutex] adding id " + key.getValue() + " to mutex map [" + this.keyMap.size() + "]\n");

		this.keyMap.put(key, new WeakReference<MutexKey<T>>(key));
		return key;
	}
}
