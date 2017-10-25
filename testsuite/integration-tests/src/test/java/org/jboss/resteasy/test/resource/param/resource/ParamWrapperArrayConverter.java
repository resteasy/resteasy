package org.jboss.resteasy.test.resource.param.resource;

import java.lang.reflect.Array;

import javax.ws.rs.ext.ParamConverter;

/**
 * 
 * @author Nicolas NESMON
 *
 */

public class ParamWrapperArrayConverter implements ParamConverter<ParamWrapper<Object>[]> {

	private final ParamConverter<Object> paramConverter;

	public ParamWrapperArrayConverter(ParamConverter<Object> paramConverter) {
		this.paramConverter = paramConverter;
	}

	@Override
	public ParamWrapper<Object>[] fromString(String param) {
		if (param == null || param.trim().isEmpty()) {
			return null;
		}
		String[] params = param.split(",");
		@SuppressWarnings("unchecked")
		ParamWrapper<Object>[] array = (ParamWrapper<Object>[]) Array.newInstance(ParamWrapper.class, params.length);
		for (int i = 0; i < params.length; i++) {
			array[i] = new ParamWrapper<>(this.paramConverter.fromString(params[i]));
		}
		return array;
	}

	@Override
	public String toString(ParamWrapper<Object>[] array) {
		if (array == null || array.length == 0) {
			return null;
		}
		StringBuilder stringBuilder = new StringBuilder();
		int size = array.length;
		for (int i = 0; i < array.length; i++) {
			stringBuilder.append(this.paramConverter.toString(array[i].getElement()));
			if (i != size) {
				stringBuilder.append(',');
			}
		}
		return stringBuilder.toString();
	}

}
