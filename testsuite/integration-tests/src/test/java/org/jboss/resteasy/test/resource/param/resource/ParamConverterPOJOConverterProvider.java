package org.jboss.resteasy.test.resource.param.resource;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;

public class ParamConverterPOJOConverterProvider implements ParamConverterProvider {
    @SuppressWarnings(value = "unchecked")
    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if (!ParamConverterPOJO.class.equals(rawType)) {
            return null;
        }
        return (ParamConverter<T>) new ParamConverterPOJOConverter();
    }
}
