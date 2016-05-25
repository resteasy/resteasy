/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.dao;

public enum QueryOperators {

	EQUALS("="),
	NOT_EQUALS("!="),
	BIGGER_THAN(">"),
	SMALLER_THAN("<"),
	BIGGER_THAN_OR_EUALS("<="),
	SMALLER_THAN_OR_EUALS(">="),
	LIKE("LIKE");

	private String value;

	private QueryOperators (String value){
		this.value = value;
	}

	public String getOperator(){
		return value;
	}
}
