/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.populators;

import se.unlogic.standardutils.dao.BeanResultSetPopulator;
import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.standardutils.validation.StringFormatValidator;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DoublePopulator extends BaseStringPopulator<Double> implements BeanResultSetPopulator<Double>, BeanStringPopulator<Double> {

	public DoublePopulator() {
		super();
	}

	public DoublePopulator(String populatorID, StringFormatValidator formatValidator) {
		super(populatorID, formatValidator);
	}

	public DoublePopulator(String populatorID) {
		super(populatorID);
	}

	public Double populate(ResultSet rs) throws SQLException {

		return rs.getDouble(1);
	}

	public Double getValue(String value) {

		return Double.valueOf(value);
	}

	@Override
	public boolean validateDefaultFormat(String value) {

		return NumberUtils.isDouble(value);
	}

	public Class<? extends Double> getType() {

		return Double.class;
	}

}
