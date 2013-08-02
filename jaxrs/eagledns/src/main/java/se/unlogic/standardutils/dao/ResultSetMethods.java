/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.dao;

import java.lang.reflect.Method;
import java.sql.Blob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.LinkedHashMap;


public class ResultSetMethods {

	private static final LinkedHashMap<Class<?>,Method> RESULTSET_COLUMNNAME_METHODS = new LinkedHashMap<Class<?>, Method>();

	static{
		try {
			RESULTSET_COLUMNNAME_METHODS.put(String.class, ResultSet.class.getMethod("getString", String.class));

			RESULTSET_COLUMNNAME_METHODS.put(Timestamp.class, ResultSet.class.getMethod("getTimestamp", String.class));

			RESULTSET_COLUMNNAME_METHODS.put(Blob.class, ResultSet.class.getMethod("getBlob", String.class));

			RESULTSET_COLUMNNAME_METHODS.put(Date.class, ResultSet.class.getMethod("getDate", String.class));

			RESULTSET_COLUMNNAME_METHODS.put(Boolean.class, ResultSet.class.getMethod("getBoolean", String.class));
			RESULTSET_COLUMNNAME_METHODS.put(boolean.class, ResultSet.class.getMethod("getBoolean", String.class));

			RESULTSET_COLUMNNAME_METHODS.put(Integer.class, ResultSet.class.getMethod("getInt", String.class));
			RESULTSET_COLUMNNAME_METHODS.put(int.class, ResultSet.class.getMethod("getInt", String.class));

			RESULTSET_COLUMNNAME_METHODS.put(Long.class, ResultSet.class.getMethod("getLong", String.class));
			RESULTSET_COLUMNNAME_METHODS.put(long.class, ResultSet.class.getMethod("getLong", String.class));

			RESULTSET_COLUMNNAME_METHODS.put(Float.class, ResultSet.class.getMethod("getFloat", String.class));
			RESULTSET_COLUMNNAME_METHODS.put(float.class, ResultSet.class.getMethod("getFloat", String.class));

			RESULTSET_COLUMNNAME_METHODS.put(Double.class, ResultSet.class.getMethod("getDouble", String.class));
			RESULTSET_COLUMNNAME_METHODS.put(double.class, ResultSet.class.getMethod("getDouble", String.class));
		} catch (SecurityException e) {

			throw new RuntimeException(e);

		} catch (NoSuchMethodException e) {

			throw new RuntimeException(e);
		}
	}

	private static final LinkedHashMap<Class<?>,Method> RESULTSET_COLUMNINDEX_METHODS = new LinkedHashMap<Class<?>, Method>();

	static{
		try {
			RESULTSET_COLUMNINDEX_METHODS.put(int.class, ResultSet.class.getMethod("getString", int.class));

			RESULTSET_COLUMNINDEX_METHODS.put(Timestamp.class, ResultSet.class.getMethod("getTimestamp", int.class));

			RESULTSET_COLUMNINDEX_METHODS.put(Blob.class, ResultSet.class.getMethod("getBlob", int.class));

			RESULTSET_COLUMNINDEX_METHODS.put(Date.class, ResultSet.class.getMethod("getDate", int.class));

			RESULTSET_COLUMNINDEX_METHODS.put(Boolean.class, ResultSet.class.getMethod("getBoolean", int.class));
			RESULTSET_COLUMNINDEX_METHODS.put(boolean.class, ResultSet.class.getMethod("getBoolean", int.class));

			RESULTSET_COLUMNINDEX_METHODS.put(Integer.class, ResultSet.class.getMethod("getInt", int.class));
			RESULTSET_COLUMNINDEX_METHODS.put(int.class, ResultSet.class.getMethod("getInt", int.class));

			RESULTSET_COLUMNINDEX_METHODS.put(Long.class, ResultSet.class.getMethod("getLong", int.class));
			RESULTSET_COLUMNINDEX_METHODS.put(long.class, ResultSet.class.getMethod("getLong", int.class));

			RESULTSET_COLUMNINDEX_METHODS.put(Float.class, ResultSet.class.getMethod("getFloat", int.class));
			RESULTSET_COLUMNINDEX_METHODS.put(float.class, ResultSet.class.getMethod("getFloat", int.class));

			RESULTSET_COLUMNINDEX_METHODS.put(Double.class, ResultSet.class.getMethod("getDouble", int.class));
			RESULTSET_COLUMNINDEX_METHODS.put(double.class, ResultSet.class.getMethod("getDouble", int.class));
		} catch (SecurityException e) {

			throw new RuntimeException(e);

		} catch (NoSuchMethodException e) {

			throw new RuntimeException(e);
		}
	}

	public static Method getColumnNameMethod(Class<?> clazz){

		return RESULTSET_COLUMNNAME_METHODS.get(clazz);
	}

	public static Method getColumnIndexMethod(Class<?> clazz){

		return RESULTSET_COLUMNINDEX_METHODS.get(clazz);
	}
}
