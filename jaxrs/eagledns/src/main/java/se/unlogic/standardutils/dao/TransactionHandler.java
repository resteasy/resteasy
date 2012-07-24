/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.dao;

import se.unlogic.standardutils.dao.querys.ArrayListQuery;
import se.unlogic.standardutils.dao.querys.BooleanQuery;
import se.unlogic.standardutils.dao.querys.HashMapQuery;
import se.unlogic.standardutils.dao.querys.ObjectQuery;
import se.unlogic.standardutils.dao.querys.PreparedStatementQuery;
import se.unlogic.standardutils.dao.querys.UpdateQuery;
import se.unlogic.standardutils.db.DBUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map.Entry;

public class TransactionHandler {

	private final Connection connection;
	private ArrayList<PreparedStatementQuery> queryList;
	private boolean aborted;
	private boolean commited;

	public TransactionHandler(DataSource dataSource) throws SQLException {

		super();
		this.connection = dataSource.getConnection();
		connection.setAutoCommit(false);
	}

	public TransactionHandler(Connection connection) throws SQLException {

		super();
		this.connection = connection;
		connection.setAutoCommit(false);
	}

	public UpdateQuery getUpdateQuery(String sqlExpression) throws SQLException {

		this.checkStatus();

		UpdateQuery query = new UpdateQuery(connection, false, sqlExpression);

		checkQueryList();
		
		this.queryList.add(query);

		return query;
	}

	private synchronized void checkQueryList() {

		if(queryList == null){
			
			queryList = new ArrayList<PreparedStatementQuery>();
		}
		
	}

	public BooleanQuery getBooleanQuery(String sql) throws SQLException {

		BooleanQuery query = new BooleanQuery(connection, false, sql);
		
		checkQueryList();
		
		this.queryList.add(query);
		
		return query;
	}

	public <T> ObjectQuery<T> getObjectQuery(String sql, BeanResultSetPopulator<T> populator) throws SQLException {

		ObjectQuery<T> query = new ObjectQuery<T>(connection, false, sql, populator);
		
		checkQueryList();
		
		this.queryList.add(query);
		
		return query;
	}

	public <T> ArrayListQuery<T> getArrayListQuery(String sql, BeanResultSetPopulator<T> populator) throws SQLException {

		ArrayListQuery<T> query = new ArrayListQuery<T>(connection, false, sql, populator);
		
		checkQueryList();
		
		this.queryList.add(query);
		
		return query;
	}

	public <K, V> HashMapQuery<K, V> getHashMapQuery(String sql, BeanResultSetPopulator<? extends Entry<K, V>> populator) throws SQLException {

		HashMapQuery<K, V> query = new HashMapQuery<K, V>(connection, false, sql, populator);
		
		checkQueryList();
		
		this.queryList.add(query);
		
		return query;
	}

	public synchronized void commit() throws SQLException {

		this.checkStatus();

		try {
			connection.commit();
			this.commited = true;
		} finally {

			if (!this.commited) {
				this.abort();
			} else {
				this.closeConnection();
			}
		}
	}

	public synchronized int getQueryCount() {

		if(queryList == null){
			
			return 0;
		}
		
		return this.queryList.size();
	}

	public synchronized void abort() {

		this.checkStatus();

		if(queryList != null){
			for (PreparedStatementQuery query : queryList) {
				query.abort();
			}
		}
		
		if (connection != null) {
			try {
				connection.rollback();
			} catch (SQLException e) {}
		}

		this.closeConnection();
		this.aborted = true;
	}

	private void closeConnection() {

		DBUtils.closeConnection(connection);
	}

	private void checkStatus() {

		if (aborted) {
			throw new TransactionAlreadyAbortedException();
		} else if (commited) {
			throw new TransactionAlreadyComittedException();
		}
	}

	@Override
	protected void finalize() throws Throwable {

		if (!commited && !aborted) {
			this.abort();
		}

		super.finalize();
	}

	public boolean isClosed() {

		return commited || aborted;
	}

	public static void autoClose(TransactionHandler transactionHandler) {

		if (transactionHandler != null && !transactionHandler.isClosed()) {
			transactionHandler.abort();
		}
	}

	//Workaround for AnnotatedDAO, needs a better solution in the long run
	Connection getConnection() {

		return connection;
	}

	public boolean isAborted() {

		return aborted;
	}

	public boolean isCommited() {

		return commited;
	}
}
