package org.jboss.resteasy.test.resource.param.resource;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;

public class DateParamConverterProvider implements ParamConverterProvider {

	@SuppressWarnings(value = "unchecked")
	@Override
	public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
		if (Date.class.isAssignableFrom(rawType)) {
			return (ParamConverter<T>) new DateParamConverter();
		} else if (List.class.isAssignableFrom(rawType)) {
			Class<?> type = getType(genericType);
			if (type != null && Date.class.isAssignableFrom(type)) {
				return (ParamConverter<T>) new DateListParamConverter();
			}
		}
		return null;
	}

	private static Class<?> getType(Type genericType) {
		ParameterizedType parameterizedType = (ParameterizedType) genericType;
		Type type = parameterizedType.getActualTypeArguments()[0];
		return (type instanceof Class) ? (Class<?>) type : null;
	}

}
