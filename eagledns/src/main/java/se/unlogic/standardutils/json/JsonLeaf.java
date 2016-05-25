/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.json;

import se.unlogic.standardutils.numbers.NumberUtils;

/**
 * A JSON value component (no children)
 * 
 * Equalizes the "leaf" of the "composite pattern" design pattern.
 * @author sikstromj
 *
 */
public class JsonLeaf implements JsonNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1198871458505471824L;
	private String value;
	
	public JsonLeaf(String value) {
		this.value = value;
	}

	public String toJson() {
		return this.toJson(new StringBuilder());
	}
	
	public String toJson(StringBuilder stringBuilder) {
		if(value == null) {
			return stringBuilder.append("null").toString();
		}
		if(NumberUtils.isNumber(this.value)) {
			stringBuilder.append(value);
		} else if(this.value.equals("false")) {
			stringBuilder.append(false);
		} else if(this.value.equals("true")) {
			stringBuilder.append(true);
		} else {
			stringBuilder.append("\"");
			stringBuilder.append(value);
			stringBuilder.append("\"");
		}
		return stringBuilder.toString();
	}

}
