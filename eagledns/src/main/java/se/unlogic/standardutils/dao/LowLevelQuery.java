/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.dao;

import se.unlogic.standardutils.dao.querys.GeneratedKeyCollector;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class represents a SQL query and is used together with the {@link AnnotatedDAO} class.
 * <p>
 * 
 * A low level query lets you write the SQL and set any parameters in contrast to the {@link HighLevelQuery}.
 * <p>
 * 
 * The SQL query is written like a normal prepared statement with '?' chars representing the parameters to be set.
 * <p>
 * 
 * The parameters will be set on the prepared statement using the "class -> method mapping" in the {@link PreparedStatementQueryMethods} class.<br>
 * Basically this means that any parameter that has a set method matching it's type in the {@link PreparedStatement} interface will work.
 * <p>
 * 
 * 
 * @author Robert "Unlogic" Olofsson (unlogic@unlogic.se)
 */
public class LowLevelQuery<T> extends RelationQuery {

	private String sql;
	private List<Object> parameters;
	private List<GeneratedKeyCollector> generatedKeyCollectors;
	private List<ChainedResultSetPopulator<T>> chainedResultSetPopulators;

	public String getSql() {

		return sql;
	}

	public void setSql(String sql) {

		this.sql = sql;
	}

	public List<Object> getParameters() {

		return parameters;
	}

	public void setParameters(List<Object> parameters) {

		this.parameters = parameters;
	}

	public void addParameter(Object parameter) {

		checkParameterList();

		this.parameters.add(parameter);
	}

	public void addParameters(Object... parameters) {

		checkParameterList();

		this.parameters.addAll(Arrays.asList(parameters));
	}

	public void addParameters(List<?> parameters) {

		checkParameterList();

		this.parameters.addAll(parameters);
	}

	private void checkParameterList() {

		if (this.parameters == null) {

			this.parameters = new ArrayList<Object>();
		}
	}

	public void addGeneratedKeyCollector(GeneratedKeyCollector keyCollector) {

		checkGeneratedKeyCollectorList();

		this.generatedKeyCollectors.add(keyCollector);
	}

	public void addGeneratedKeyCollector(List<GeneratedKeyCollector> keyCollectors) {

		checkGeneratedKeyCollectorList();

		this.generatedKeyCollectors.addAll(keyCollectors);
	}

	public void addGeneratedKeyCollector(GeneratedKeyCollector... keyCollectors) {

		checkGeneratedKeyCollectorList();

		this.generatedKeyCollectors.addAll(Arrays.asList(keyCollectors));
	}

	private void checkGeneratedKeyCollectorList() {

		if (this.generatedKeyCollectors == null) {

			this.generatedKeyCollectors = new ArrayList<GeneratedKeyCollector>();
		}
	}

	public List<GeneratedKeyCollector> getGeneratedKeyCollectors() {

		return generatedKeyCollectors;
	}

	public void addChainedResultSetPopulator(ChainedResultSetPopulator<T> chainedPopulator) {

		checkChainedResultSetPopulatorList();

		this.chainedResultSetPopulators.add(chainedPopulator);
	}

	public void addChainedResultSetPopulator(List<ChainedResultSetPopulator<T>> chainedPopulators) {

		checkChainedResultSetPopulatorList();

		this.chainedResultSetPopulators.addAll(chainedPopulators);
	}

	public void addChainedResultSetPopulator(ChainedResultSetPopulator<T>... chainedPopulators) {

		checkChainedResultSetPopulatorList();

		this.chainedResultSetPopulators.addAll(Arrays.asList(chainedPopulators));
	}

	private void checkChainedResultSetPopulatorList() {

		if (this.chainedResultSetPopulators == null) {

			this.chainedResultSetPopulators = new ArrayList<ChainedResultSetPopulator<T>>();
		}
	}

	public List<ChainedResultSetPopulator<T>> getChainedResultSetPopulators() {

		return chainedResultSetPopulators;
	}
	
	public boolean hasChainedBeanResultSetPopulators(){
		
		return this.chainedResultSetPopulators != null && !this.chainedResultSetPopulators.isEmpty();
	}
}
