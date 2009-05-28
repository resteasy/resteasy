package org.jboss.resteasy.plugins.providers.multipart;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.util.FindAnnotation;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@Consumes("multipart/form-data")
public class MultipartFormAnnotationReader implements MessageBodyReader<Object> {
	protected @Context
	Providers workers;

	public boolean isReadable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return FindAnnotation.findAnnotation(annotations, MultipartForm.class) != null
				|| type.isAnnotationPresent(MultipartForm.class);
	}

	public Object readFrom(Class<Object> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {
		String boundary = mediaType.getParameters().get("boundary");
		if (boundary == null)
			throw new IOException("Unable to get boundary for multipart");
		MultipartFormDataInputImpl input = new MultipartFormDataInputImpl(
				mediaType, workers);
		input.parse(entityStream);

		Object obj;
		try {
			obj = type.newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}

		Class<?> theType = type;
		while (theType != null && !theType.equals(Object.class)) {
			setFields(theType, input, obj);
			theType = theType.getSuperclass();
		}

		for (Method method : type.getMethods()) {
			if (method.isAnnotationPresent(FormParam.class)
					&& method.getName().startsWith("set")
					&& method.getParameterTypes().length == 1) {
				FormParam param = method.getAnnotation(FormParam.class);
				List<InputPart> list = input.getFormDataMap()
						.get(param.value());
				if (list == null || list.isEmpty())
					continue;
				InputPart part = list.get(0);
				// if (part == null) throw new
				// LoggableFailure("Unable to find @FormParam in multipart: " +
				// param.value());
				if (part == null)
					continue;
				Object data = part.getBody(method.getParameterTypes()[0],
						method.getGenericParameterTypes()[0]);
				try {
					method.invoke(obj, data);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				} catch (InvocationTargetException e) {
					throw new RuntimeException(e);
				}
			}
		}

		return obj;
	}

	protected void setFields(Class<?> type, MultipartFormDataInputImpl input,
			Object obj) throws IOException {
		for (Field field : type.getDeclaredFields()) {
			if (field.isAnnotationPresent(FormParam.class)) {
				field.setAccessible(true);
				FormParam param = field.getAnnotation(FormParam.class);
				List<InputPart> list = input.getFormDataMap()
						.get(param.value());
				if (list == null || list.isEmpty())
					continue;
				InputPart part = list.get(0);
				// if (part == null) throw new
				// LoggableFailure("Unable to find @FormParam in multipart: " +
				// param.value());
				if (part == null)
					continue;
				Object data = part.getBody(field.getType(), field
						.getGenericType());
				try {
					field.set(obj, data);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

}