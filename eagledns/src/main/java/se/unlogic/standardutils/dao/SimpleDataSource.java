/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.dao;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;


public class SimpleDataSource implements DataSource {


	private String url;
	private String username;
	private String password;
	
	public SimpleDataSource(String driver, String url, String username, String password) throws ClassNotFoundException {

		super();
		this.url = url;
		this.username = username;
		this.password = password;
		
		Class.forName(driver);
	}

	public Connection getConnection() throws SQLException {

		return DriverManager.getConnection(this.url, username, password);
	}

	public Connection getConnection(String username, String password) throws SQLException {

		return DriverManager.getConnection(this.url, username, password);
	}

	public PrintWriter getLogWriter() throws SQLException {

		throw new UnsupportedOperationException();
	}

	public int getLoginTimeout() throws SQLException {

		throw new UnsupportedOperationException();
	}

	public void setLogWriter(PrintWriter out) throws SQLException {

		throw new UnsupportedOperationException();
	}

	public void setLoginTimeout(int seconds) throws SQLException {

		throw new UnsupportedOperationException();
	}

	public boolean isWrapperFor(Class<?> arg0) throws SQLException {
		return false;
	}

	public <T> T unwrap(Class<T> arg0) throws SQLException {
		return null;
	}

   public Logger getParentLogger() throws SQLFeatureNotSupportedException
   {
      return null;
   }
}
