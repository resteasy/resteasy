package org.jboss.resteasy.test.resource.constructor.resource;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import jakarta.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
public class Item2ParamConverterProvider implements ParamConverterProvider {
    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType,
                                              Type genericType,
                                              Annotation[] annotations) {

        if (rawType.getName().equals(Item2.class.getName())) {
            return new ParamConverter<T>() {

                @Override
                public T fromString(String value) {
                    // Picking 405 randomly just to be different from 400 and 404.
                    throw new SomeWAE(405);
                }

                @Override
                public String toString(T value) {
                    return value.toString();
                }
            };
        }
        return null;
    }

    class SomeWAE extends WebApplicationException {
        SomeWAE(final int status) {
            super(status);
        }
    }
}
