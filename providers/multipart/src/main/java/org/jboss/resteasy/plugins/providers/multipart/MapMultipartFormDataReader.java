package org.jboss.resteasy.plugins.providers.multipart;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;

import org.jboss.resteasy.util.Types;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@Consumes("multipart/form-data")
public class MapMultipartFormDataReader implements MessageBodyReader<Map<?, ?>> {
	protected @Context
	Providers workers;

	public boolean isReadable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return type.equals(Map.class) && genericType != null
				&& genericType instanceof ParameterizedType;
	}

	public Map<?, ?> readFrom(Class<Map<?, ?>> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {
		String boundary = mediaType.getParameters().get("boundary");
		if (boundary == null)
			throw new IOException("Unable to get boundary for multipart");

		if (!(genericType instanceof ParameterizedType))
			throw new IllegalArgumentException("Reader = " + this
					+ " recived genericType = " + genericType
					+ ", but it is not instance of " + ParameterizedType.class);

		ParameterizedType param = (ParameterizedType) genericType;
		Type baseType = param.getActualTypeArguments()[1];
		Class<?> rawType = Types.getRawType(baseType);

		MultipartFormDataInputImpl input = new MultipartFormDataInputImpl(
				mediaType, workers);
		input.parse(entityStream);

		Map<Object, Object> map = new HashMap<Object, Object>();

		for (Map.Entry<String, List<InputPart>> entry : input.getFormDataMap()
				.entrySet())
			map.put(entry.getKey(), entry.getValue().get(0).getBody(rawType,
					baseType));
		return map;
	}
}