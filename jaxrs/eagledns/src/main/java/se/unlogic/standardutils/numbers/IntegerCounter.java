/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.numbers;

public class IntegerCounter {

	private Integer value;

	IntegerCounter(Integer value) {
		super();
		this.value = value;
	}

	public IntegerCounter() {
		super();
		this.value = 0;
	}

	public Integer getValue() {
		return value;
	}

	public synchronized void setValue(Integer value) {
		this.value = value;
	}

	public synchronized Integer increment(){

		value++;

		return value;
	}

	public synchronized Integer decrement(){

		value--;

		return value;
	}
}
