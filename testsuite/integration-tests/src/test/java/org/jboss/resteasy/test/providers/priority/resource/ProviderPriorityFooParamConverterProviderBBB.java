package org.jboss.resteasy.test.providers.priority.resource;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import jakarta.annotation.Priority;
import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import jakarta.ws.rs.ext.Provider;

@Provider
@Priority(20)
public class ProviderPriorityFooParamConverterProviderBBB implements ParamConverterProvider {

    @SuppressWarnings("unchecked")
    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        return (ParamConverter<T>) new ProviderPriorityFooParamConverter("BBB");
    }
}
