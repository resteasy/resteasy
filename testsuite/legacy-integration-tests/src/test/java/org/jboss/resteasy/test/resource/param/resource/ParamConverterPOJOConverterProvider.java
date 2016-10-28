package org.jboss.resteasy.test.resource.param.resource;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

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
