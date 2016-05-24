/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.collections;

public class KeyAlreadyCachedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6139847169384918434L;
	private Object key;
	
	public KeyAlreadyCachedException(Object key) {
		this.key = key;
	}

	public Object getKey() {
		return key;
	}

	@Override
	public String toString() {
		if(key != null){
			return "KeyAlreadyCachedException, key: " + key.toString();
		}else{
			return "KeyAlreadyCachedException, key: null";
		}
	}	
}
