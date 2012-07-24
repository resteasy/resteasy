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


public class IntegerPopulator extends BaseStringPopulator<Integer> implements BeanResultSetPopulator<Integer>{

	private static final IntegerPopulator POPULATOR = new IntegerPopulator();

	public static IntegerPopulator getPopulator(){
		return POPULATOR;
	}

	private int columnIndex = 1;

	public IntegerPopulator() {
		super();
	}

	public IntegerPopulator(int columnIndex) {
		super();

		this.columnIndex = columnIndex;
	}

	public IntegerPopulator(String populatorID, StringFormatValidator formatValidator) {
		super(populatorID, formatValidator);
	}

	public IntegerPopulator(String populatorID) {
		super(populatorID);
	}

	public Integer populate(ResultSet rs) throws SQLException {
		return rs.getInt(columnIndex);
	}

	public Integer getValue(String value) {

		return Integer.valueOf(value);
	}

	@Override
	public boolean validateDefaultFormat(String value) {

		return NumberUtils.isInt(value);
	}

	public Class<? extends Integer> getType() {

		return Integer.class;
	}
}
