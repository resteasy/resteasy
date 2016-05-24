/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.dao;

import java.util.Collection;


public class QueryParameter<BeanType,ColumnType> {

	private final ColumnType value;
	private final Collection<ColumnType> values;
	private Column<BeanType, ? super ColumnType> column;
	private String operator = QueryOperators.EQUALS.getOperator();

	QueryParameter(Column<BeanType,? super ColumnType> column, ColumnType value) {
		super();
		this.column = column;
		this.value = value;
		this.values = null;
	}

	QueryParameter(Column<BeanType,? super ColumnType> column, ColumnType value, String operator) {
		super();
		this.column = column;
		this.value = value;
		this.operator = operator;
		this.values = null;
	}

	QueryParameter(Column<BeanType,? super ColumnType> column, String operator) {
		super();
		this.column = column;
		this.operator = operator;
		this.value = null;
		this.values = null;
	}	
	
	QueryParameter(Column<BeanType, ? super ColumnType> column, Collection<ColumnType> values, String operator) {

		this.column = column;
		this.operator = operator;
		this.value = null;
		this.values = values;
	}

	public ColumnType getValue() {
		return value;
	}

	public Collection<ColumnType> getValues() {
		return values;
	}
	
	public Column<BeanType,? super ColumnType> getColumn() {
		return column;
	}

	public String getOperator() {
		return operator;
	}
	
	public boolean hasValues(){
		
		return this.value != null || this.values != null;
	}
	
	public boolean hasMultipleValues(){
		
		return this.values != null;
	}
}
