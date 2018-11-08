/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.dao.querys;

import se.unlogic.standardutils.db.DBUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BooleanQuery extends PreparedStatementQuery {

   public BooleanQuery(final Connection connection, final boolean closeConnectionOnExit, final String query) throws SQLException {
      super(connection, closeConnectionOnExit, query);
   }

   public BooleanQuery(final DataSource dataSource, final boolean closeConnectionOnExit, final String query) throws SQLException {
      super(dataSource, closeConnectionOnExit, query);
   }

   public boolean executeQuery() throws SQLException {

      ResultSet rs = null;

      try {
         rs = pstmt.executeQuery();

         if (rs.next()) {
            return true;
         } else {
            return false;
         }
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
