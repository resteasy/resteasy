package org.jboss.resteasy.test.resource.param.resource;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.*;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;

public class MultiValuedPersonParamConverterProvider implements ParamConverterProvider {

        @SuppressWarnings("unchecked")
        @Override
        public <T> ParamConverter<T> getConverter(Class<T> aClass, Type type, Annotation[] annotations) {
            if (List.class.isAssignableFrom(aClass)) {
                return (ParamConverter<T>) new PersonParamListConverter();
            }
            if (SortedSet.class.isAssignableFrom(aClass)) {
                return (ParamConverter<T>) new PersonParamSortedSetConverter();
            }
            if (Set.class.isAssignableFrom(aClass)) {
                return (ParamConverter<T>) new PersonParamSetConverter();
            }
            if (PersonWithConverter[].class.isAssignableFrom(aClass)) {
                return (ParamConverter<T>) new PersonParamArrayConverter();
            }
            return null;
        }
}