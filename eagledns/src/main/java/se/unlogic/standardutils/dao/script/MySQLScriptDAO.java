/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.dao.script;

import se.unlogic.standardutils.dao.TransactionHandler;
import se.unlogic.standardutils.dao.querys.UpdateQuery;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.List;

public class MySQLScriptDAO implements ScriptDAO {
	
	protected final DataSource dataSource;

	public MySQLScriptDAO(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void executeScript(InputStream inputStream) throws SQLException, IOException {

		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		
		StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            throw e;
        } finally {
            try {
            	reader.close();
            	inputStream.close();
            } catch (IOException e) {
                throw e;
            }
        }
        
        this.executeScript(sb.toString());

	}
	
	public void executeScript(String script) throws SQLException {

		TransactionHandler transactionHandler = null;
		UpdateQuery updateQuery;

		ScriptUtility scriptUtility = new MySQLScriptUtility();
		List<String> statements = scriptUtility.getStatements(script);
		
		try {
			
			transactionHandler = new TransactionHandler(this.dataSource);

			for(String query : statements) {
				updateQuery = transactionHandler.getUpdateQuery(query.toString());
				updateQuery.executeUpdate();
			}
			
			transactionHandler.commit();

		} finally {

			TransactionHandler.autoClose(transactionHandler);
		
		}
	}
}
