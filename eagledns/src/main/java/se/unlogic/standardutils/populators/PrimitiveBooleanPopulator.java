/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.populators;


public class PrimitiveBooleanPopulator extends BaseStringPopulator<Boolean> implements BeanStringPopulator<Boolean> {

	public Boolean getValue(String value) {

		if(value == null || value.equalsIgnoreCase("false")){
			return false;
		}else{
			return true;
		}
	}

	@Override
	public boolean validateDefaultFormat(String value) {

		return true;
	}

	public Class<? extends Boolean> getType() {

		return boolean.class;
	}
}
