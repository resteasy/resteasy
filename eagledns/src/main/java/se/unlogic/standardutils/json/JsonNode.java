/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.json;

import java.io.Serializable;

/**
 * Abstraction interface for all JSON components
 * 
 * Equalizes the "component" of the "composite pattern" design pattern.
 * 
 * @author sikstromj
 *
 */
public interface JsonNode extends Serializable {

	String toJson();
	
	String toJson(StringBuilder stringBuilder);
	
}
