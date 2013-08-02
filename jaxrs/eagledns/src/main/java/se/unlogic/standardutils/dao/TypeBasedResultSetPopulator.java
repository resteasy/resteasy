/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.dao;

import se.unlogic.standardutils.populators.BeanStringPopulator;

import java.sql.ResultSet;
import java.sql.SQLException;


public class TypeBasedResultSetPopulator<Type> implements BeanResultSetPopulator<Type> {

	private BeanStringPopulator<Type> beanStringPopulator;
	private String columnName;

	public TypeBasedResultSetPopulator(BeanStringPopulator<Type> typePopulator, String columnName) {

		this.beanStringPopulator = typePopulator;
		this.columnName = columnName;
	}

	public Type populate(ResultSet rs) throws SQLException {

		String value = rs.getString(columnName);

		if(value == null){

			return null;
		}

		return beanStringPopulator.getValue(value);
	}
}
