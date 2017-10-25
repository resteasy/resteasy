package org.jboss.resteasy.test.resource.param.resource;

import java.lang.reflect.Array;

import javax.ws.rs.ext.ParamConverter;

/**
 * 
 * @author Nicolas NESMON
 *
 */

public class PathParamWrapperArrayConverter implements ParamConverter<PathParamWrapper<Object>[]> {

	private final ParamConverter<Object> paramConverter;

	public PathParamWrapperArrayConverter(ParamConverter<Object> paramConverter) {
		this.paramConverter = paramConverter;
	}

	@Override
	public PathParamWrapper<Object>[] fromString(String param) {
		if (param == null || param.trim().isEmpty()) {
			return null;
		}
		String[] params = param.split("/");
		@SuppressWarnings("unchecked")
		PathParamWrapper<Object>[] array = (PathParamWrapper<Object>[]) Array.newInstance(PathParamWrapper.class,
				params.length);
		for (int i = 0; i < params.length; i++) {
			array[i] = new PathParamWrapper<>(this.paramConverter.fromString(params[i]));
		}
		return array;
	}

	@Override
	public String toString(PathParamWrapper<Object>[] array) {
		if (array == null || array.length == 0) {
			return null;
		}
		StringBuilder stringBuilder = new StringBuilder();
		int size = array.length;
		for (int i = 0; i < array.length; i++) {
			stringBuilder.append(this.paramConverter.toString(array[i].getElement()));
			if (i != size) {
				stringBuilder.append('/');
			}
		}
		return stringBuilder.toString();
	}

}
