package org.jboss.resteasy.test.client.proxy.resource;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;

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
