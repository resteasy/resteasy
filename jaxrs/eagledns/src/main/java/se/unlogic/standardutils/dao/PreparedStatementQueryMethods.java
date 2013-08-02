/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.dao;

import se.unlogic.standardutils.dao.querys.PreparedStatementQuery;

import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.util.HashMap;


/**
 * This class provides a static map containing mappings from classes to their corresponding set methods in the {@link PreparedStatement} interface.<p>
 * 
 * {@link Integer}, {@link Long}, {@link Double}, {@link Float}, {@link Boolean} and {@link Byte} types are mapped to the setObject method in order to allow null values.<p>
 * 
 * All other types are mapped to their default set method in the {@link PreparedStatement} interface.
 * 
 * @author Robert "Unlogic" Olofsson (unlogic@unlogic.se)
 *
 */
public class PreparedStatementQueryMethods {

	protected static final HashMap<Class<?>,Method> QUERY_METHOD_MAP = new HashMap<Class<?>, Method>();
	protected static final Method SET_OBJECT_METHOD;
	static{
		try {
			//Special methods mappings
			QUERY_METHOD_MAP.put(Integer.class, PreparedStatementQuery.class.getMethod("setObject", int.class,Object.class));
			QUERY_METHOD_MAP.put(Long.class, PreparedStatementQuery.class.getMethod("setObject", int.class,Object.class));
			QUERY_METHOD_MAP.put(Double.class, PreparedStatementQuery.class.getMethod("setObject", int.class,Object.class));
			QUERY_METHOD_MAP.put(Float.class, PreparedStatementQuery.class.getMethod("setObject", int.class,Object.class));
			QUERY_METHOD_MAP.put(Boolean.class, PreparedStatementQuery.class.getMethod("setObject", int.class,Object.class));
			QUERY_METHOD_MAP.put(Byte.class, PreparedStatementQuery.class.getMethod("setObject", int.class,Object.class));

			Method[] methods = PreparedStatementQuery.class.getMethods();

			for(Method method : methods){

				if(method.getName().startsWith("set") && !method.getName().equals("setObject") && method.getParameterTypes().length == 2 && method.getParameterTypes()[0] == int.class){

					//System.out.println("Adding method " + method);
					QUERY_METHOD_MAP.put(method.getParameterTypes()[1], method);
				}
			}

			SET_OBJECT_METHOD = PreparedStatementQuery.class.getMethod("setObject", int.class,Object.class);

		} catch (SecurityException e) {

			throw new RuntimeException(e);

		} catch (NoSuchMethodException e) {

			throw new RuntimeException(e);
		}
	}

	public static Method getQueryMethod(Class<?> clazz) {

		return QUERY_METHOD_MAP.get(clazz);
	}

	public static Method getObjectQueryMethod(){

		return SET_OBJECT_METHOD;
	}
}
