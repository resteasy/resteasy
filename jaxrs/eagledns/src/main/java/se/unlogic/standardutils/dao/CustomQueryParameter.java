/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.dao;

import se.unlogic.standardutils.populators.QueryParameterPopulator;

import java.lang.reflect.Method;

public class CustomQueryParameter<T>{

	private QueryParameterPopulator<?> queryParameterPopulator;
	private Method queryMethod;
	private Object paramValue;

	public CustomQueryParameter(Column<T,?> column , T bean) {

		super();
		this.queryParameterPopulator = column.getQueryParameterPopulator();
		this.queryMethod = column.getQueryMethod();
		this.paramValue = column.getBeanValue(bean);
	}

	public QueryParameterPopulator<?> getQueryParameterPopulator() {

		return queryParameterPopulator;
	}

	public Method getQueryMethod() {

		return queryMethod;
	}

	public Object getParamValue() {

		return paramValue;
	}
}
