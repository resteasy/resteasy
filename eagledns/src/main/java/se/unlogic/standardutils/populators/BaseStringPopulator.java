/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.populators;

import se.unlogic.standardutils.validation.StringFormatValidator;

public abstract class BaseStringPopulator<T> implements BeanStringPopulator<T> {

	private final String populatorID;
	private final StringFormatValidator formatValidator;

	public BaseStringPopulator(String populatorID, StringFormatValidator formatValidator) {
		super();
		this.populatorID = populatorID;
		this.formatValidator = formatValidator;
	}

	public BaseStringPopulator(String populatorID) {
		super();
		this.populatorID = populatorID;
		this.formatValidator = null;
	}

	public BaseStringPopulator() {
		super();
		this.populatorID = null;
		this.formatValidator = null;
	}

	public String getPopulatorID() {
		return populatorID;
	}

	public final boolean validateFormat(String value) {

		if(formatValidator == null){

			return this.validateDefaultFormat(value);

		}else{

			return this.formatValidator.validateFormat(value);
		}
	}

	protected abstract boolean validateDefaultFormat(String value);
}
