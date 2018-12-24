package org.jboss.resteasy.test.resource.constructor.resource;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.Type;
import java.lang.annotation.Annotation;

@Provider
public class ItemParamConverterProvider   implements ParamConverterProvider {
    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType,
                                              Type genericType,
                                              Annotation[] annotations) {

        if (rawType.getName().equals(Item.class.getName())) {
            return new ParamConverter<T>() {

                @Override
                public T fromString(String value) {
                    throw new IllegalArgumentException("Some strange exception");
                }

                @Override
                public String toString(T value) {
                    return value.toString();
                }
            };
        }
        return null;
    }
}
