/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.populators;

import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.validation.StringFormatValidator;


public class StringURLPopulator extends BaseStringPopulator<String> implements BeanStringPopulator<String> {

	public StringURLPopulator(String populatorID, StringFormatValidator formatValidator) {
		super(populatorID, formatValidator);
	}

	public StringURLPopulator(String populatorID) {
		super(populatorID);
	}

	public StringURLPopulator(){
		super("url");
	}

	public Class<? extends String> getType() {
		return String.class;
	}

	public String getValue(String value) {
		return value;
	}

	@Override
	public boolean validateDefaultFormat(String value) {
		return StringUtils.isValidURL(value);
	}

}
