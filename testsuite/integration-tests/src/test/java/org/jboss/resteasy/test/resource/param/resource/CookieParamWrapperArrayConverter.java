package org.jboss.resteasy.test.resource.param.resource;

import java.lang.reflect.Array;

import javax.ws.rs.ext.ParamConverter;

/**
 * 
 * @author Nicolas NESMON
 *
 */

public class CookieParamWrapperArrayConverter implements ParamConverter<CookieParamWrapper<Object>[]> {

	private final ParamConverter<Object> paramConverter;

	public CookieParamWrapperArrayConverter(ParamConverter<Object> paramConverter) {
		this.paramConverter = paramConverter;
	}

	@Override
	public CookieParamWrapper<Object>[] fromString(String param) {
		if (param == null || param.trim().isEmpty()) {
			return null;
		}
		String[] params = param.split("-");
		@SuppressWarnings("unchecked")
		CookieParamWrapper<Object>[] array = (CookieParamWrapper<Object>[]) Array.newInstance(CookieParamWrapper.class,
				params.length);
		for (int i = 0; i < params.length; i++) {
			array[i] = new CookieParamWrapper<>(this.paramConverter.fromString(params[i]));
		}
		return array;
	}

	@Override
	public String toString(CookieParamWrapper<Object>[] array) {
		if (array == null || array.length == 0) {
			return null;
		}
		StringBuilder stringBuilder = new StringBuilder();
		int size = array.length;
		for (int i = 0; i < array.length; i++) {
			stringBuilder.append(this.paramConverter.toString(array[i].getElement()));
			if (i != size) {
				stringBuilder.append('-');
			}
		}
		return stringBuilder.toString();
	}

}
