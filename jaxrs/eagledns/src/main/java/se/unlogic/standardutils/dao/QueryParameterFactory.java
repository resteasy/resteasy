/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.dao;

import se.unlogic.standardutils.collections.CollectionUtils;

import java.util.Collection;
import java.util.List;

public class QueryParameterFactory<Bean, Type> {

	private Column<Bean,? super Type> column;

	QueryParameterFactory(Column<Bean,? super Type> column) {
		super();
		this.column = column;
	}

	public QueryParameter<Bean,Type> getParameter(Type value){

		if(value == null){
			
			throw new RuntimeException("Value cannot be null, it will result in invalid SQL");
		}
		
		return new QueryParameter<Bean, Type>(column, value);
	}

	public QueryParameter<Bean,Type> getParameter(Type value, QueryOperators queryOperator){

		if(value == null){
			
			throw new RuntimeException("Value cannot be null, it will result in invalid SQL");
		}
		
		return new QueryParameter<Bean, Type>(column, value, queryOperator.getOperator());
	}
	
	public QueryParameter<Bean,Type> getIsNullParameter(){

		return new QueryParameter<Bean, Type>(column, "IS NULL");
	}
	
	public QueryParameter<Bean,Type> getIsNotNullParameter(){

		return new QueryParameter<Bean, Type>(column, "IS NOT NULL");
	}	
	
	public QueryParameter<Bean,Type> getWhereInParameter(Collection<Type> values){

		if(CollectionUtils.isEmpty(values)){
			
			throw new RuntimeException("Values cannot be null or empty, it will result in invalid SQL");
		}
		
		return new QueryParameter<Bean, Type>(column, values, "IN");
	}	
	
	public QueryParameter<Bean,Type> getWhereNotInParameter(List<Type> values){

		if(CollectionUtils.isEmpty(values)){
			
			throw new RuntimeException("Values cannot be null or empty, it will result in invalid SQL");
		}
		
		return new QueryParameter<Bean, Type>(column, values, "NOT IN");
	}
}
