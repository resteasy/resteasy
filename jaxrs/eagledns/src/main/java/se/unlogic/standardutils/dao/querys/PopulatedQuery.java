/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.dao.querys;

import se.unlogic.standardutils.dao.BeanResultSetPopulator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class PopulatedQuery<ReturnType> extends PreparedStatementQuery {

	protected BeanResultSetPopulator<? extends ReturnType> beanPopulator;

	public PopulatedQuery(Connection connection, boolean closeConnectionOnExit, String query, BeanResultSetPopulator<? extends ReturnType> bp) throws SQLException {
		super(connection, closeConnectionOnExit, query);
		this.beanPopulator = bp;
	}

	public PopulatedQuery(DataSource dataSource, boolean closeConnectionOnExit, String query, BeanResultSetPopulator<? extends ReturnType> bp) throws SQLException {
		super(dataSource, closeConnectionOnExit, query);
		this.beanPopulator = bp;
	}

	public BeanResultSetPopulator<? extends ReturnType> getBeanPopulator() {
		return beanPopulator;
	}
}
