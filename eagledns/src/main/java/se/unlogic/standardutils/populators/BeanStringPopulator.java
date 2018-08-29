/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.populators;

import se.unlogic.standardutils.validation.StringFormatValidator;



/**
 * This interface is used for populating a bean or type from a String
 * 
 * @author Robert "Unlogic" Olofsson
 *
 * @param <T>
 */
public interface BeanStringPopulator<T> extends StringFormatValidator{

	T getValue(String value);

	Class<? extends T> getType();

	String getPopulatorID();
}
