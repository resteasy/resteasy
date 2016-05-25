/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.dao.querys;

import java.sql.ResultSet;
import java.sql.SQLException;


public class IntegerKeyCollector implements GeneratedKeyCollector {

	private Integer keyValue;
	private int columnIndex = 1;

	public IntegerKeyCollector() {}

	public IntegerKeyCollector(int columnIndex) {
		super();
		this.columnIndex = columnIndex;
	}

	public void collect(ResultSet rs) throws SQLException {

		keyValue = rs.getInt(columnIndex);
	}

	public Integer getKeyValue() {

		return keyValue;
	}
}
