package com.damnhandy.resteasy.common;

import java.util.HashMap;
import java.util.Map;

import com.damnhandy.resteasy.util.TypeConverter;

/**
 * 
 * @author Ryan J. McDonough
 * @Since 1.0
 */
public class HttpHeaders {

	private Map<String, String> headerValues = new HashMap<String, String>();

	/**
	 * 
	 * @param name
	 * @param value
	 */
	public void addHeader(String name, String value) {
		headerValues.put(name, value);
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public Object getHeaderValue(String name) {
		return headerValues.get(name);
	}

	/**
	 * 
	 * @param <T>
	 * @param key
	 * @param type
	 * @return
	 */
	public <T> T get(String key, Class<T> type) {
		String value = headerValues.get(key);
		return TypeConverter.getType(value,type);
	}
}
