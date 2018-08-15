package org.jboss.resteasy.test.resource.param.resource;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;

public class MultiValuedParamPersonConverterProvider implements ParamConverterProvider {

        @SuppressWarnings("unchecked")
        @Override
        public <T> ParamConverter<T> getConverter(Class<T> aClass, Type type, Annotation[] annotations) {
            if (List.class.isAssignableFrom(aClass)) {
                return (ParamConverter<T>) new MultiValuedParamPersonListConverter();
            }
            if (SortedSet.class.isAssignableFrom(aClass)) {
                return (ParamConverter<T>) new MultiValuedParamPersonSortedSetConverter();
            }
            if (Set.class.isAssignableFrom(aClass)) {
                return (ParamConverter<T>) new MultiValuedParamPersonSetConverter();
            }
            if (MultiValuedParamPersonWithConverter[].class.isAssignableFrom(aClass)) {
                return (ParamConverter<T>) new MultiValuedParamPersonArrayConverter();
            }
            return null;
        }
}