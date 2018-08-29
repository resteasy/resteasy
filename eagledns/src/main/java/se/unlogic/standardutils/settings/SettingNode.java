/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.settings;

import java.util.List;

public interface SettingNode {

	SettingNode getSetting(String expression);
	
	List<? extends SettingNode> getSettings(String expression);
	
	Integer getInteger(String expression);
	
	List<Integer> getIntegers(String expression);
	
	int getInt(String expression);
	
	Double getDouble(String expression);
	
	List<Double> getDoubles(String expression);
	
	Long getLong(String expression);
	
	List<Long> getLongs(String expression);
	
	String getString(String expression);
	
	List<String> getStrings(String expression);
	
	Boolean getBoolean(String expression);
	
	boolean getPrimitiveBoolean(String expression);

}
