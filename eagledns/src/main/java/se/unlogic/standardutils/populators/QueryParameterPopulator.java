/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.populators;

import se.unlogic.standardutils.dao.querys.PreparedStatementQuery;

import java.sql.SQLException;


public interface QueryParameterPopulator<T> {

	Class<? extends T> getType();

	void populate(PreparedStatementQuery query, int paramIndex, Object bean) throws SQLException;
}
