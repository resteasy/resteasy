package org.jboss.resteasy.test.client.proxy.resource;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ProxyWithGenericReturnTypeMessageBodyWriter implements MessageBodyWriter<List<String>> {
    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public long getSize(List<String> t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(List<String> t, Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream) throws IOException,
            WebApplicationException {
        String val;
        if (genericType == null) {
            val = "null";
        } else if (genericType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericType;
            val = ((Class) parameterizedType.getRawType()).getSimpleName() + "<";
            Type paramType = parameterizedType.getActualTypeArguments()[0];
            if (paramType instanceof Class) {
                val += ((Class) paramType).getSimpleName();
            } else {
                val += paramType.toString();
            }
            val += ">";
        } else if (genericType instanceof TypeVariable) {
            val = "TypeVariable";
        } else if (genericType instanceof GenericArrayType) {
            val = "GenericArrayType";
        } else {
            val = "Type";
        }

        entityStream.write(val.getBytes());
    }
}
