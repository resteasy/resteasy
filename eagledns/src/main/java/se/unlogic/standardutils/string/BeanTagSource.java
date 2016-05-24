/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.string;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Set;


public class BeanTagSource<T> implements TagSource{

	private final T bean;
	private final Set<String> tagSet;
	private final HashMap<String,Method> tagMethodMap;
	private final HashMap<String,Field> tagFieldMap;

	public BeanTagSource(T bean, HashMap<String, Method> tagMethodMap, HashMap<String, Field> tagFieldMap, Set<String> tagSet) {

		super();
		this.bean = bean;
		this.tagMethodMap = tagMethodMap;
		this.tagFieldMap = tagFieldMap;
		this.tagSet = tagSet;
	}

	public String getTagValue(String tag) {

		Field field = this.tagFieldMap.get(tag);

		if(field != null){

			try {
				Object value = field.get(bean);

				if(value != null){

					return value.toString();
				}

				return null;

			} catch (IllegalArgumentException e) {

				throw new RuntimeException(e);

			} catch (IllegalAccessException e) {

				throw new RuntimeException(e);
			}
		}

		Method method = tagMethodMap.get(tag);

		if(method != null){

			try {
				Object value = method.invoke(bean);

				if(value != null){

					return value.toString();
				}

				return null;

			} catch (IllegalArgumentException e) {

				throw new RuntimeException(e);

			} catch (IllegalAccessException e) {

				throw new RuntimeException(e);

			} catch (InvocationTargetException e) {

				throw new RuntimeException(e);
			}
		}

		return null;
	}

	public Set<String> getTags() {

		return this.tagSet;
	}
}
