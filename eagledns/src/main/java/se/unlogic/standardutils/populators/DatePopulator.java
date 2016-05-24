/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.populators;

import se.unlogic.standardutils.date.DateUtils;
import se.unlogic.standardutils.validation.StringFormatValidator;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DatePopulator extends BaseStringPopulator<Date> {

	private final SimpleDateFormat dateFormat;

	public DatePopulator(SimpleDateFormat dateFormat) {

		super();

		this.dateFormat = dateFormat;
	}

	public DatePopulator(String populatorID, SimpleDateFormat dateFormat) {

		super(populatorID);

		this.dateFormat = dateFormat;
	}

	public DatePopulator(String populatorID, SimpleDateFormat dateFormat, StringFormatValidator formatValidator) {

		super(populatorID,formatValidator);
		this.dateFormat = dateFormat;
	}

	public Class<? extends Date> getType() {

		return Date.class;
	}

	public Date getValue(String value) {

		try {
			java.util.Date utilDate = this.dateFormat.parse(value);
			
			return new Date(utilDate.getTime());
			
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean validateDefaultFormat(String value) {

		return DateUtils.isValidDate(this.dateFormat, value);
	}
}
