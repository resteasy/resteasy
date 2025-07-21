package org.jboss.resteasy.plugins.providers.multipart;

import java.beans.Introspector;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.Providers;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.plugins.providers.multipart.i18n.Messages;
import org.jboss.resteasy.spi.ReaderException;
import org.jboss.resteasy.spi.util.FindAnnotation;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@Consumes("multipart/form-data")
public class MultipartFormAnnotationReader implements MessageBodyReader<Object> {
    protected @Context Providers workers;

    @Override
    public boolean isReadable(Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType) {
        return FindAnnotation.findAnnotation(annotations, MultipartForm.class) != null
                || type.isAnnotationPresent(MultipartForm.class);
    }

    @Override
    public Object readFrom(Class<Object> type, Type genericType,
            Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
            throws IOException, WebApplicationException {
        String boundary = mediaType.getParameters().get("boundary");
        if (boundary == null)
            throw new IOException(Messages.MESSAGES.unableToGetBoundary());
        MultipartFormDataInputImpl input = new MultipartFormDataInputImpl(
                mediaType, workers);
        input.parse(entityStream);

        Object obj;
        try {
            obj = type.newInstance();
        } catch (InstantiationException e) {
            throw new ReaderException(e.getCause());
        } catch (IllegalAccessException e) {
            throw new ReaderException(e);
        }

        boolean hasInputStream = false;

        Class<?> theType = type;
        while (theType != null && !theType.equals(Object.class)) {
            if (setFields(theType, input, obj)) {
                hasInputStream = true;
            }
            theType = theType.getSuperclass();
        }

        for (Method method : type.getMethods()) {
            if ((method.isAnnotationPresent(FormParam.class)
                    || method.isAnnotationPresent(org.jboss.resteasy.annotations.jaxrs.FormParam.class))
                    && method.getName().startsWith("set")
                    && method.getParameterCount() == 1) {
                FormParam param = method.getAnnotation(FormParam.class);
                String name;
                if (param != null) {
                    name = param.value();
                } else {
                    org.jboss.resteasy.annotations.jaxrs.FormParam param2 = method
                            .getAnnotation(org.jboss.resteasy.annotations.jaxrs.FormParam.class);
                    name = param2.value();
                    if (name == null || name.isEmpty())
                        name = Introspector.decapitalize(method.getName().substring(3));
                }
                List<InputPart> list = input.getFormDataMap()
                        .get(name);
                if (list == null || list.isEmpty())
                    continue;
                InputPart part = list.get(0);
                // if (part == null) throw new
                // LoggableFailure("Unable to find @FormParam in multipart: " +
                // param.value());
                if (part == null)
                    continue;
                Class<?> type1 = method.getParameterTypes()[0];
                Object data;
                if (InputPart.class.equals(type1)) {
                    hasInputStream = true;
                    data = part;
                } else {
                    if (InputStream.class.equals(type1)) {
                        hasInputStream = true;
                    }
                    data = part.getBody(type1, method.getGenericParameterTypes()[0]);
                }
                try {
                    method.invoke(obj, data);
                } catch (IllegalAccessException e) {
                    throw new ReaderException(e);
                } catch (InvocationTargetException e) {
                    throw new ReaderException(e.getCause());
                }
            }
        }
        if (!hasInputStream) {
            input.close();
        }
        return obj;
    }

    protected boolean setFields(Class<?> type, MultipartFormDataInputImpl input,
            Object obj) throws IOException {
        boolean hasInputStream = false;
        for (Field field : type.getDeclaredFields()) {
            if (field.isAnnotationPresent(FormParam.class)
                    || field.isAnnotationPresent(org.jboss.resteasy.annotations.jaxrs.FormParam.class)) {
                field.setAccessible(true);
                FormParam param = field.getAnnotation(FormParam.class);
                String name;
                if (param != null) {
                    name = param.value();
                } else {
                    org.jboss.resteasy.annotations.jaxrs.FormParam param2 = field
                            .getAnnotation(org.jboss.resteasy.annotations.jaxrs.FormParam.class);
                    name = param2.value();
                    if (name == null || name.isEmpty())
                        name = field.getName();
                }
                List<InputPart> list = input.getFormDataMap()
                        .get(name);
                if (list == null || list.isEmpty())
                    continue;
                InputPart part = list.get(0);
                // if (part == null) throw new
                // LoggableFailure("Unable to find @FormParam in multipart: " +
                // param.value());
                if (part == null)
                    continue;
                Object data;
                if (InputPart.class.equals(field.getType())) {
                    hasInputStream = true;
                    data = part;
                } else {
                    if (InputStream.class.equals(field.getType())) {
                        hasInputStream = true;
                    }
                    data = part.getBody(field.getType(), field
                            .getGenericType());
                }
                try {
                    field.set(obj, data);
                } catch (IllegalAccessException e) {
                    throw new ReaderException(e);
                }
            }
        }
        return hasInputStream;
    }

}
