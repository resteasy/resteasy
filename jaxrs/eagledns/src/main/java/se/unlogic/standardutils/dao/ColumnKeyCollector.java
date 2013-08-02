/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.dao;

import se.unlogic.standardutils.populators.annotated.AnnotatedResultSetPopulator;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;


public class ColumnKeyCollector<T> {

	private final ResultSetField resultSetField;
	private final int columnIndex;

	public ColumnKeyCollector(Field field, AnnotatedResultSetPopulator<T> populator, int columnIndex) {

		this.resultSetField = populator.getResultSetField(field);
		this.columnIndex = columnIndex;
	}

	public void collect(T bean, ResultSet rs) throws SQLException {

		try {
			if(resultSetField.getResultSetColumnIndexMethod() != null){

				Object value = resultSetField.getResultSetColumnIndexMethod().invoke(rs, columnIndex);

				if(value == null && !resultSetField.getBeanField().getType().isPrimitive()){

					resultSetField.getBeanField().set(bean, null);

				}else{

					resultSetField.getBeanField().set(bean, value);
				}

			}else{

				String value = rs.getString(columnIndex);

				if(value != null || resultSetField.getBeanStringPopulator().getType().isPrimitive()){

					resultSetField.getBeanField().set(bean, resultSetField.getBeanStringPopulator().getValue(value));
				}else{
					resultSetField.getBeanField().set(bean, null);
				}
			}

		} catch (IllegalArgumentException e) {

			throw new RuntimeException(e);

		} catch (IllegalAccessException e) {

			throw new RuntimeException(e);

		} catch (InvocationTargetException e) {

			throw new RuntimeException(e);
		}
	}
}
