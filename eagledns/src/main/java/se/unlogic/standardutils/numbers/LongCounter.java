/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.numbers;

public class LongCounter {

	private Long value;

	LongCounter(Long value) {
		super();
		this.value = value;
	}

	public LongCounter() {
		super();
		this.value = 0l;
	}

	public Long getValue() {
		return value;
	}

	public synchronized void setValue(Long value) {
		this.value = value;
	}

	public synchronized Long increment(){

		value++;

		return value;
	}

	public synchronized Long decrement(){

		value--;

		return value;
	}
}
