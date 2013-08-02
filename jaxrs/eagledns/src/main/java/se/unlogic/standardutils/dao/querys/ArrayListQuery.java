/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.dao.querys;

import se.unlogic.standardutils.dao.BeanResultSetPopulator;
import se.unlogic.standardutils.db.DBUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ArrayListQuery<ReturnType> extends PopulatedQuery<ReturnType> {

	public ArrayListQuery(Connection connection, boolean closeConnectionOnExit, String query, BeanResultSetPopulator<ReturnType> bp) throws SQLException {
		super(connection, closeConnectionOnExit, query, bp);
	}

	public ArrayListQuery(DataSource dataSource, boolean closeConnectionOnExit, String query, BeanResultSetPopulator<ReturnType> bp) throws SQLException {
		super(dataSource, closeConnectionOnExit, query, bp);
	}

	public ArrayList<ReturnType> executeQuery() throws SQLException {

		ResultSet rs = null;
		ArrayList<ReturnType> returnTypeList = null;

		try {
			// Send query to database and store results.
			rs = pstmt.executeQuery();

			if (rs.next()) {
				rs.last();
				returnTypeList = new ArrayList<ReturnType>(rs.getRow());
				rs.beforeFirst();

				while (rs.next()) {
					returnTypeList.add(beanPopulator.populate(rs));
				}
			}

			return returnTypeList;

		} catch (SQLException sqle) {
			throw sqle;
		} finally {
			DBUtils.closeResultSet(rs);
			DBUtils.closePreparedStatement(pstmt);

			if (this.closeConnectionOnExit) {
				DBUtils.closeConnection(connection);
			}

			this.closed = true;
		}
	}

	@Override
	protected PreparedStatement getPreparedStatement(String query) throws SQLException {

		return connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	}
}
