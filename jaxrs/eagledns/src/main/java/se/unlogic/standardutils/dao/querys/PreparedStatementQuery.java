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
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

public abstract class PreparedStatementQuery {

	protected Connection connection;
	protected boolean closeConnectionOnExit;
	protected PreparedStatement pstmt;
	protected boolean closed;

	public PreparedStatementQuery(Connection connection, boolean closeConnectionOnExit, String query) throws SQLException {
		this.closeConnectionOnExit = closeConnectionOnExit;
		this.connection = connection;

		try {
			this.pstmt = this.getPreparedStatement(query);
		} catch (SQLException e) {

			this.abort();

			throw e;
		}
	}

	protected PreparedStatement getPreparedStatement(String query) throws SQLException {

		return connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
	}

	public PreparedStatementQuery(DataSource dataSource, boolean closeConnectionOnExit, String query) throws SQLException {
		this(dataSource.getConnection(), closeConnectionOnExit, query);
	}

	public void setArray(int arg0, java.sql.Array arg1) throws SQLException {

		try {

			pstmt.setArray(arg0, arg1);

		} catch (SQLException e) {

			this.abort();
			throw e;
		}
	}

	public void setAsciiStream(int arg0, InputStream arg1, int arg2) throws SQLException {

		try {

			pstmt.setAsciiStream(arg0, arg1, arg2);

		} catch (SQLException e) {

			this.abort();
			throw e;
		}
	}

	public void setBigDecimal(int arg0, BigDecimal arg1) throws SQLException {

		try {

			pstmt.setBigDecimal(arg0, arg1);

		} catch (SQLException e) {

			this.abort();
			throw e;
		}
	}

	public void setBinaryStream(int arg0, InputStream arg1, int arg2) throws SQLException {

		try {

			pstmt.setBinaryStream(arg0, arg1, arg2);

		} catch (SQLException e) {

			this.abort();
			throw e;
		}
	}

	public void setBlob(int arg0, Blob arg1) throws SQLException {

		try {

			pstmt.setBlob(arg0, arg1);

		} catch (SQLException e) {

			this.abort();
			throw e;
		}
	}

	public void setBoolean(int arg0, boolean arg1) throws SQLException {

		try {

			pstmt.setBoolean(arg0, arg1);

		} catch (SQLException e) {

			this.abort();
			throw e;
		}
	}

	public void setByte(int arg0, byte arg1) throws SQLException {

		try {

			pstmt.setByte(arg0, arg1);

		} catch (SQLException e) {

			this.abort();
			throw e;
		}
	}

	public void setBytes(int arg0, byte[] arg1) throws SQLException {

		try {

			pstmt.setBytes(arg0, arg1);

		} catch (SQLException e) {

			this.abort();
			throw e;
		}
	}

	public void setCharacterStream(int arg0, Reader arg1, int arg2) throws SQLException {

		try {

			pstmt.setCharacterStream(arg0, arg1, arg2);

		} catch (SQLException e) {

			this.abort();
			throw e;
		}
	}

	public void setClob(int arg0, Clob arg1) throws SQLException {

		try {

			pstmt.setClob(arg0, arg1);

		} catch (SQLException e) {

			this.abort();
			throw e;
		}
	}

	public void setCursorName(String arg0) throws SQLException {

		try {

			pstmt.setCursorName(arg0);

		} catch (SQLException e) {

			this.abort();
			throw e;
		}
	}

	public void setDate(int arg0, Date arg1, Calendar arg2) throws SQLException {

		try {

			pstmt.setDate(arg0, arg1, arg2);

		} catch (SQLException e) {

			this.abort();
			throw e;
		}
	}

	public void setDate(int arg0, Date arg1) throws SQLException {

		try {

			pstmt.setDate(arg0, arg1);

		} catch (SQLException e) {

			this.abort();
			throw e;
		}
	}

	public void setDouble(int arg0, double arg1) throws SQLException {

		try {

			pstmt.setDouble(arg0, arg1);

		} catch (SQLException e) {

			this.abort();
			throw e;
		}
	}

	public void setEscapeProcessing(boolean arg0) throws SQLException {

		try {

			pstmt.setEscapeProcessing(arg0);

		} catch (SQLException e) {

			this.abort();
			throw e;
		}
	}

	public void setFetchDirection(int arg0) throws SQLException {

		try {

			pstmt.setFetchDirection(arg0);

		} catch (SQLException e) {

			this.abort();
			throw e;
		}
	}

	public void setFetchSize(int arg0) throws SQLException {

		try {

			pstmt.setFetchSize(arg0);

		} catch (SQLException e) {

			this.abort();
			throw e;
		}
	}

	public void setFloat(int arg0, float arg1) throws SQLException {

		try {

			pstmt.setFloat(arg0, arg1);

		} catch (SQLException e) {

			this.abort();
			throw e;
		}
	}

	public void setInt(int arg0, int arg1) throws SQLException {

		try {

			pstmt.setInt(arg0, arg1);

		} catch (SQLException e) {

			this.abort();
			throw e;
		}
	}

	public void setLong(int arg0, long arg1) throws SQLException {

		try {

			pstmt.setLong(arg0, arg1);

		} catch (SQLException e) {

			this.abort();
			throw e;
		}
	}

	public void setMaxFieldSize(int arg0) throws SQLException {

		try {

			pstmt.setMaxFieldSize(arg0);

		} catch (SQLException e) {

			this.abort();
			throw e;
		}
	}

	public void setMaxRows(int arg0) throws SQLException {

		try {

			pstmt.setMaxRows(arg0);

		} catch (SQLException e) {

			this.abort();
			throw e;
		}
	}

	public void setNull(int arg0, int arg1, String arg2) throws SQLException {

		try {

			pstmt.setNull(arg0, arg1, arg2);

		} catch (SQLException e) {

			this.abort();
			throw e;
		}
	}

	public void setNull(int arg0, int arg1) throws SQLException {

		try {

			pstmt.setNull(arg0, arg1);

		} catch (SQLException e) {

			this.abort();
			throw e;
		}
	}

	public void setObject(int arg0, Object arg1, int arg2, int arg3) throws SQLException {

		try {

			pstmt.setObject(arg0, arg1, arg2, arg3);

		} catch (SQLException e) {

			this.abort();
			throw e;
		}
	}

	public void setObject(int arg0, Object arg1, int arg2) throws SQLException {

		try {

			pstmt.setObject(arg0, arg1, arg2);

		} catch (SQLException e) {

			this.abort();
			throw e;
		}
	}

	public void setObject(int arg0, Object arg1) throws SQLException {

		try {

			pstmt.setObject(arg0, arg1);

		} catch (SQLException e) {

			this.abort();
			throw e;
		}
	}

	public void setQueryTimeout(int arg0) throws SQLException {

		try {

			pstmt.setQueryTimeout(arg0);

		} catch (SQLException e) {

			this.abort();
			throw e;
		}
	}

	public void setRef(int arg0, Ref arg1) throws SQLException {

		try {

			pstmt.setRef(arg0, arg1);

		} catch (SQLException e) {

			this.abort();
			throw e;
		}
	}

	public void setShort(int arg0, short arg1) throws SQLException {

		try {

			pstmt.setShort(arg0, arg1);

		} catch (SQLException e) {

			this.abort();
			throw e;
		}
	}

	public void setString(int arg0, String arg1) throws SQLException {

		try {

			pstmt.setString(arg0, arg1);

		} catch (SQLException e) {

			this.abort();
			throw e;
		}
	}

	public void setTime(int arg0, Time arg1, Calendar arg2) throws SQLException {

		try {

			pstmt.setTime(arg0, arg1, arg2);

		} catch (SQLException e) {

			this.abort();
			throw e;
		}
	}

	public void setTime(int arg0, Time arg1) throws SQLException {

		try {

			pstmt.setTime(arg0, arg1);

		} catch (SQLException e) {

			this.abort();
			throw e;
		}
	}

	public void setTimestamp(int arg0, Timestamp arg1, Calendar arg2) throws SQLException {

		try {

			pstmt.setTimestamp(arg0, arg1, arg2);

		} catch (SQLException e) {

			this.abort();
			throw e;
		}
	}

	public void setTimestamp(int arg0, Timestamp arg1) throws SQLException {

		try {

			pstmt.setTimestamp(arg0, arg1);

		} catch (SQLException e) {

			this.abort();
			throw e;
		}
	}

	public void setURL(int arg0, URL arg1) throws SQLException {

		try {

			pstmt.setURL(arg0, arg1);

		} catch (SQLException e) {

			this.abort();
			throw e;
		}
	}

	public boolean isCloseConnectionOnExit() {
		return closeConnectionOnExit;
	}

	public void abort() {

		DBUtils.closePreparedStatement(pstmt);

		if (this.closeConnectionOnExit) {

			DBUtils.closeConnection(connection);
		}

		this.closed = true;
	}

	public static void autoCloseQuery(PreparedStatementQuery query){

		if(query != null && !query.isClosed()){

			query.abort();
		}
	}

	public boolean isClosed() {
		return closed;
	}
}
