package org.jboss.resteasy.test.resource.param.resource;

import java.lang.annotation.Annotation;
import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import jakarta.ws.rs.ext.Provider;
import java.lang.reflect.Type;

@Provider
public class HeaderParamParamConverterProvider implements ParamConverterProvider {
    public static class MyClassConverter implements ParamConverter<HeaderParamMyClass> {
        @Override
        public HeaderParamMyClass fromString(String value) {
            final HeaderParamMyClass result = new HeaderParamMyClass();
            result.setValue(value + "-MORE");
            return result;
        }
        @Override
        public String toString(HeaderParamMyClass value) {
            return "paramConverter";
        }
    }

    public HeaderParamParamConverterProvider () {
    }

    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType,
                                              Annotation[] annotations) {
        final ParamConverter<T> result;
        if (rawType.isAssignableFrom(HeaderParamMyClass .class)) {
            result = (ParamConverter<T>)new MyClassConverter();
        } else {
            result = null;
        }
        return result;
    }
}
