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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map.Entry;

public class HashMapQuery<KeyType, ValueType> extends PopulatedQuery<Entry<KeyType, ValueType>> {

	public HashMapQuery(Connection connection, boolean closeConnectionOnExit, String query, BeanResultSetPopulator<? extends Entry<KeyType, ValueType>> bp) throws SQLException {
		super(connection, closeConnectionOnExit, query, bp);
	}

	public HashMapQuery(DataSource dataSource, boolean closeConnectionOnExit, String query, BeanResultSetPopulator<? extends Entry<KeyType, ValueType>> bp) throws SQLException {
		super(dataSource, closeConnectionOnExit, query, bp);
	}

	public HashMap<KeyType, ValueType> executeQuery() throws SQLException {

		ResultSet rs = null;
		HashMap<KeyType, ValueType> returnTypeMap = null;

		try {
			// Send query to database and store results.
			rs = pstmt.executeQuery();

			if (rs.next()) {
				returnTypeMap = new HashMap<KeyType, ValueType>();
				rs.beforeFirst();

				while (rs.next()) {
					Entry<KeyType, ValueType> entry = beanPopulator.populate(rs);
					returnTypeMap.put(entry.getKey(), entry.getValue());
				}
			}

			return returnTypeMap;

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
}
