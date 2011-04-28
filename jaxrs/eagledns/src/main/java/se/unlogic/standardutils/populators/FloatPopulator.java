/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.populators;

import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.standardutils.validation.StringFormatValidator;

public class FloatPopulator extends BaseStringPopulator<Float> implements BeanStringPopulator<Float> {

	public FloatPopulator() {
		super();
	}

	public FloatPopulator(String populatorID, StringFormatValidator formatValidator) {
		super(populatorID, formatValidator);
	}

	public FloatPopulator(String populatorID) {
		super(populatorID);
	}

	public Float getValue(String value) {

		return Float.valueOf(value);
	}

	@Override
	public boolean validateDefaultFormat(String value) {

		return NumberUtils.isFloat(value);
	}

	public Class<? extends Float> getType() {

		return Float.class;
	}
}
