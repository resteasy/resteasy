/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.dao;

import se.unlogic.standardutils.populators.BeanStringPopulator;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ResultSetField {

	private final Field beanField;
	private final Method resultSetColumnNameMethod;
	private final Method resultSetColumnIndexMethod;
	private final BeanStringPopulator<?> beanStringPopulator;
	private final String alias;

	public ResultSetField(Field beanField, Method resultSetColumnNameMethod, Method resultSetColumnIndexMethod, String alias , BeanStringPopulator<?> typePopulator) {
		super();
		this.beanField = beanField;
		this.resultSetColumnNameMethod = resultSetColumnNameMethod;
		this.resultSetColumnIndexMethod = resultSetColumnIndexMethod;
		this.alias = alias;
		this.beanStringPopulator = typePopulator;
	}

	public BeanStringPopulator<?> getBeanStringPopulator() {
		return beanStringPopulator;
	}

	public Field getBeanField() {
		return beanField;
	}

	public Method getResultSetColumnNameMethod() {
		return resultSetColumnNameMethod;
	}

	public String getAlias() {
		return alias;
	}

	public Method getResultSetColumnIndexMethod() {
		return resultSetColumnIndexMethod;
	}
}
