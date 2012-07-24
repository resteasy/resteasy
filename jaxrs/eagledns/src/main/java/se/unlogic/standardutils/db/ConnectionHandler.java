/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.db;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Deprecated
public class ConnectionHandler {
	private DataSource datasource = null;
	
	public ConnectionHandler(String datasource) throws NamingException{
		setDataSource(datasource);
	}
	
	public ConnectionHandler(DataSource datasource){
		setDataSource(datasource);
	}	
	
	public void setDataSource(DataSource ds){
		this.datasource = ds;
	}
	
	public void setDataSource(String datasource) throws NamingException{
		Context initContext = new InitialContext();
		Context envContext  = (Context)initContext.lookup("java:/comp/env");
		this.datasource = (DataSource)envContext.lookup(datasource);
	}
	
	public DataSource getDataSource(){
		return this.datasource;
	}
	
	public Connection getConnection() throws SQLException{
		return datasource.getConnection();
	}
	
	public String toString(){
		return this.datasource.toString();
	}
}
