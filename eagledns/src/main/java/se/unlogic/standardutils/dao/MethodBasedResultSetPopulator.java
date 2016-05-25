/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.dao;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;


public class MethodBasedResultSetPopulator<Type> implements BeanResultSetPopulator<Type> {

	private Method method;
	private String columnName;

	public MethodBasedResultSetPopulator(Method method, String columnName) {

		this.method = method;
		this.columnName = columnName;
	}

	@SuppressWarnings("unchecked")
	public Type populate(ResultSet rs) throws SQLException {

		try {
			return (Type) method.invoke(rs, columnName);

		} catch (IllegalArgumentException e) {

			throw new RuntimeException(e);

		} catch (IllegalAccessException e) {

			throw new RuntimeException(e);

		} catch (InvocationTargetException e) {

			throw new RuntimeException(e);
		}
	}
}
