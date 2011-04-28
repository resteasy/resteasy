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

	public SettingNode getSetting(String expression);
	
	public List<? extends SettingNode> getSettings(String expression);
	
	public Integer getInteger(String expression);
	
	public List<Integer> getIntegers(String expression);
	
	public int getInt(String expression);
	
	public Double getDouble(String expression);
	
	public List<Double> getDoubles(String expression);
	
	public Long getLong(String expression);
	
	public List<Long> getLongs(String expression);
	
	public String getString(String expression);
	
	public List<String> getStrings(String expression);
	
	public Boolean getBoolean(String expression);
	
	public boolean getPrimitiveBoolean(String expression);

}
