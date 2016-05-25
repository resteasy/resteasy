/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.populators;

import se.unlogic.standardutils.dao.BeanResultSetPopulator;
import se.unlogic.standardutils.datatypes.SimpleEntry;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StringEntryPopulator implements BeanResultSetPopulator<SimpleEntry<String, String>> {

	public SimpleEntry<String, String> populate(ResultSet rs) throws SQLException {

		return new SimpleEntry<String, String>(rs.getString(1),rs.getString(2));
	}
}
