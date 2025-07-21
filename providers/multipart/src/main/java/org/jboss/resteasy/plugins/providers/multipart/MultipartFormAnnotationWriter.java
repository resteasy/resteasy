package org.jboss.resteasy.plugins.providers.multipart;

import java.beans.Introspector;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.concurrent.CompletionStage;

import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.annotations.providers.multipart.PartFilename;
import org.jboss.resteasy.annotations.providers.multipart.PartType;
import org.jboss.resteasy.plugins.providers.ProviderHelper;
import org.jboss.resteasy.spi.AsyncMessageBodyWriter;
import org.jboss.resteasy.spi.AsyncOutputStream;
import org.jboss.resteasy.spi.WriterException;
import org.jboss.resteasy.spi.util.FindAnnotation;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@Produces("multipart/form-data")
public class MultipartFormAnnotationWriter extends AbstractMultipartFormDataWriter implements AsyncMessageBodyWriter<Object> {
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return FindAnnotation.findAnnotation(annotations, MultipartForm.class) != null
                || type.isAnnotationPresent(MultipartForm.class);
    }

    public long getSize(Object o, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    public void writeTo(Object obj, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {

        write(getMultipart(obj, type), mediaType, httpHeaders, entityStream, annotations);

    }

    private MultipartFormDataOutput getMultipart(Object obj, Class<?> type) throws IOException {
        MultipartFormDataOutput multipart = new MultipartFormDataOutput();

        Class<?> theType = type;
        while (theType != null && !theType.equals(Object.class)) {
            getFields(theType, multipart, obj);
            theType = theType.getSuperclass();
        }

        for (Method method : type.getMethods()) {
            if ((method.isAnnotationPresent(FormParam.class)
                    || method.isAnnotationPresent(org.jboss.resteasy.annotations.jaxrs.FormParam.class))
                    && method.getName().startsWith("get") && method.getParameterCount() == 0
                    && method.isAnnotationPresent(PartType.class)) {
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
                Object value = null;
                try {
                    value = method.invoke(obj);
                } catch (IllegalAccessException e) {
                    throw new WriterException(e);
                } catch (InvocationTargetException e) {
                    throw new WriterException(e.getCause());
                }
                PartType partType = method.getAnnotation(PartType.class);
                String filename = getFilename(method);

                multipart.addFormData(name, value, method.getReturnType(), method.getGenericReturnType(),
                        MediaType.valueOf(partType.value()), filename);
            }
        }
        return multipart;
    }

    @Override
    public CompletionStage<Void> asyncWriteTo(Object obj, Class<?> type, Type genericType, Annotation[] annotations,
            MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, AsyncOutputStream entityStream) {
        try {
            return asyncWrite(getMultipart(obj, type), mediaType, httpHeaders, entityStream, annotations);
        } catch (IOException e) {
            return ProviderHelper.completedException(e);
        }
    }

    protected String getFilename(AccessibleObject method) {
        PartFilename fname = method.getAnnotation(PartFilename.class);
        return fname == null ? null : fname.value();
    }

    protected void getFields(Class<?> type, MultipartFormDataOutput output, Object obj)
            throws IOException {
        for (Field field : type.getDeclaredFields()) {
            if ((field.isAnnotationPresent(FormParam.class)
                    || field.isAnnotationPresent(org.jboss.resteasy.annotations.jaxrs.FormParam.class))
                    && field.isAnnotationPresent(PartType.class)) {
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
                Object value = null;
                try {
                    value = field.get(obj);
                } catch (IllegalAccessException e) {
                    throw new WriterException(e);
                }
                PartType partType = field.getAnnotation(PartType.class);
                String filename = getFilename(field);

                output.addFormData(name, value, field.getType(), field.getGenericType(), MediaType.valueOf(partType.value()),
                        filename);
            }
        }
    }

}
